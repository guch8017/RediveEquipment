package com.guch8017.rediveEquipment.activity.mainActivity.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.module.DBUnitProfile;
import com.guch8017.rediveEquipment.database.Database;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.activity.unitDetailActivity.UnitDetailActivity;
import com.guch8017.rediveEquipment.equipsolver.DatabaseGenerator;
import com.guch8017.rediveEquipment.util.BrotliUtils;
import com.guch8017.rediveEquipment.util.Constant;
import com.guch8017.rediveEquipment.util.IO;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    private List<DBUnitProfile> mUnitProfileList;
    private UnitListAdapter mAdapter;
    private final static ImageLoader imageLoader = ImageLoader.getInstance();
    private final static DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true)
            .showImageOnLoading(R.drawable.ic_launcher_background).build();
    private DatabaseRefreshReceiver mReceiver;
    private int downloadTask; //task=1的回调代表检查更新完成，task=2的回调代表下载数据的操作完成
    private ProgressDialog mProgressDialog;
    private String downloadTempFilePath;
    private static final String updaterTag = "Update Checker";
    // TODO:改用设置界面存储版本数据
    private String currentVersion;
    private String databaseFilePath;
    private long downloadTaskID;
    private Context mContext;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.guch8017.pcr.DATABASE_REFRESH");
        mReceiver = new DatabaseRefreshReceiver();
        mContext.registerReceiver(mReceiver,filter);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ListView unitList = root.findViewById(R.id.unit_list);
        swipeRefreshLayout = root.findViewById(R.id.unit_list_refresh);
        mAdapter = new UnitListAdapter(getContext(), R.layout.unit_list_item, mUnitProfileList);
        unitList.setAdapter(mAdapter);
        unitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView unitIDTextView = view.findViewById(R.id.unit_id);
                final String unitIDText = unitIDTextView.getText().toString();

                int unitID = Integer.valueOf(unitIDText);
                
                Intent intent = new Intent(getContext(), UnitDetailActivity.class);
                Log.i("Unit List Fragment", "Intent值为 unit_id = "+unitID);
                intent.putExtra("unit_id", unitID);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkUpdate();
            }
        });
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Aria.download(this).register();
        currentVersion = "";
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
        loadDatabase(context);
        downloadTempFilePath = context.getFilesDir().toString() + "/temp";
        databaseFilePath = context.getDatabasePath("database.db").toString();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mContext.unregisterReceiver(mReceiver);
        mContext = null;
    }

    private void loadDatabase(Context context){
        Object obj = null;
        if(mUnitProfileList == null){
            mUnitProfileList = new ArrayList<>();
        }
        if(!IO.isFileExist(context.getDatabasePath("database.db").getAbsolutePath())){
            Log.i("角色列表读取器","游戏数据库不存在");
        }
        else {
            DatabaseReflector reflector = new DatabaseReflector(context);
            obj = reflector.reflectClass(DBUnitProfile.class.getName(), "unit_profile");
        }
        if(obj != null){
            mUnitProfileList.clear();
            mUnitProfileList.addAll((List<DBUnitProfile>)obj);
        }else {
            Log.e("角色列表读取器","无法获取列表信息，数据库可能损坏");
            mUnitProfileList.clear();
        }
    }

    private void checkUpdate() {
        mProgressDialog = new ProgressDialog(this.getContext());
        mProgressDialog.setTitle("Checking Update");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Downloading update list...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        downloadTask = 1;
        Log.d("Check Update","Target file path: "+ downloadTempFilePath);
        // 删除原文件防止下载错误
        if(IO.isFileExist(downloadTempFilePath)){
            IO.deleteFile(downloadTempFilePath);
        }
        downloadTaskID = Aria.download(this).load(Constant.databaseVersionUrl).
                ignoreFilePathOccupy().
                ignoreCheckPermissions().
                setFilePath(downloadTempFilePath).create();
    }

    @Download.onTaskComplete public void onTaskComplete(DownloadTask task){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
        if(downloadTask == 1){
            Log.i(updaterTag, "版本数据下载完成");
            try{
                File file = new File(downloadTempFilePath);
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String jsonText = bufferedReader.readLine();
                bufferedReader.close();
                reader.close();
                inputStream.close();

                JSONObject jsonObject = new JSONObject(jsonText);
                String version = jsonObject.getString("TruthVersion");
                Log.i(updaterTag, version);

                if(!version.equals(currentVersion)){
                    Log.d(updaterTag, "当前数据库版本: " + currentVersion +
                            " 在线数据库版本: " + version + "开始下载数据库文件");
                    downloadTask = 2;
                    mProgressDialog = new ProgressDialog(getContext());
                    mProgressDialog.setTitle("正在下载数据库");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    downloadTaskID = Aria.download(this).load(Constant.databaseUrl).
                            ignoreFilePathOccupy().
                            ignoreCheckPermissions().
                            setFilePath(downloadTempFilePath).create();
                }else {
                    Log.d(updaterTag, "当前数据库版本为最新");
                    Toast.makeText(mContext,"数据库已为最新版", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                swipeRefreshLayout.setRefreshing(false);
                Log.i(updaterTag, "JSON解码失败 ERR:JSON_DECODE");
            }
        }else if(downloadTask == 2){
            Log.i(updaterTag, "数据库压缩文件下载完成");
            try {
                if(IO.isFileExist(databaseFilePath)){
                    boolean status = IO.deleteFile(databaseFilePath);
                    Log.i("HomeFragment","Delete Database :"+status);
                }
                if(IO.isFileExist(databaseFilePath+"-shm")){
                    boolean status = IO.deleteFile(databaseFilePath+"-shm");
                    Log.i("HomeFragment","Delete Database -shm:"+status);
                }
                if(IO.isFileExist(databaseFilePath+"-wal")){
                    boolean status = IO.deleteFile(databaseFilePath+"-wal");
                    Log.i("HomeFragment","Delete Database -wal:"+status);
                }
                BrotliUtils.deCompress(downloadTempFilePath, databaseFilePath);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            DatabaseGenerator.generateComposeDatabase(HomeFragment.this.getContext());
                            DatabaseGenerator.generateDropDatabase(HomeFragment.this.getContext());
                        }catch (Exception e){
                            Looper.prepare();
                            Toast.makeText(HomeFragment.this.getContext(), "预编译数据库失败，装备计算功能将不可用。", Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }.start();

                Intent intent = new Intent("com.guch8017.pcr.DATABASE_REFRESH");
                swipeRefreshLayout.setRefreshing(false);
                getContext().sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
                Log.e(updaterTag, "解压缩Brotli文件失败 ERR:DECOMPRESS_BROTLI");
            }


        }else {
            Log.e(updaterTag,"未知状态码 ERR:UNKNOWN_STATUS_CODE:" + downloadTask);
        }
    }


    @Download.onTaskFail public void onTaskFail(DownloadTask task){
        mProgressDialog.dismiss();
        swipeRefreshLayout.setRefreshing(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String title = (downloadTask == 1)?"更新检查失败":"更新下载失败";
        builder.setTitle(title)
                .setMessage("网络错误，请更换网络环境或再次尝试")
                .setPositiveButton("确认",null)
                .create().show();
    }

    @Download.onTaskRunning public void onTaskRunning(DownloadTask task){
        if(mProgressDialog != null && downloadTask == 2){
            mProgressDialog.setProgress(task.getPercent());
        }

    }


    private class DatabaseRefreshReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Database.notifyDatabaseChange();
            loadDatabase(context);
            mAdapter.notifyDataSetChanged();

        }
    }

    class UnitListAdapter extends ArrayAdapter<DBUnitProfile> {
        private int resourceId;
        UnitListAdapter(Context context, int resourceID, List<DBUnitProfile> profiles){
            super(context,resourceID, profiles);
            resourceId = resourceID;
        }


        @NonNull @Override public View getView(int position, View convertView, @NonNull ViewGroup parent){
            UnitViewHolder viewHolder;
            DBUnitProfile profile = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new UnitViewHolder();
                viewHolder.unit_id = convertView.findViewById(R.id.unit_id);
                viewHolder.unit_name = convertView.findViewById(R.id.unit_name);
                viewHolder.catch_copy = convertView.findViewById(R.id.unit_nickname);
                viewHolder.unit_icon = convertView.findViewById(R.id.unit_image);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (UnitViewHolder) convertView.getTag();
            }
            viewHolder.unit_name.setText(profile.unit_name);
            viewHolder.catch_copy.setText(profile.catch_copy);
            viewHolder.unit_id.setText(String.valueOf(profile.unit_id));

            imageLoader.displayImage(Constant.unitImageUrl(profile.unit_id, 3),
                    viewHolder.unit_icon, displayImageOption);
            return convertView;
        }
        private class UnitViewHolder{
            TextView unit_id;
            TextView unit_name;
            TextView catch_copy;
            ImageView unit_icon;
        }


    }
}