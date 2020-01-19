package com.guch8017.myapplication.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.guch8017.myapplication.R;
import com.guch8017.myapplication.database.DBUnitProfile;
import com.guch8017.myapplication.database.DatabaseReflector;
import com.guch8017.myapplication.utli.Constant;
import com.guch8017.myapplication.utli.IO;
import com.netease.hearttouch.brotlij.Brotli;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    private String currentVersion;
    private String databaseFilePath;
    private long downloadTaskID;
    androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.guch8017.pcr.DATABASE_REFRESH");
        mReceiver = new DatabaseRefreshReceiver();
        getActivity().registerReceiver(mReceiver,filter);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ListView unitList = root.findViewById(R.id.unit_list);
        swipeRefreshLayout = root.findViewById(R.id.unit_list_refresh);
        mAdapter = new UnitListAdapter(getContext(), R.layout.unit_list_item, mUnitProfileList);
        unitList.setAdapter(mAdapter);
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
        loadDatabase(context);
        downloadTempFilePath = context.getFilesDir().toString() + "/temp";
        databaseFilePath = context.getDatabasePath("database.db").toString();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void loadDatabase(Context context){
        if(!IO.isFileExist(context.getDatabasePath("database.db").getAbsolutePath())){
            Log.i("角色列表读取器","游戏数据库不存在");
        }
        DatabaseReflector reflector = new DatabaseReflector(context);
        Object obj = reflector.reflectClass(DBUnitProfile.class.getName(),"unit_profile");
        if(obj != null){
            mUnitProfileList = (List<DBUnitProfile>)obj;
        }else {
            Log.e("角色列表读取器","无法获取列表信息，数据库可能损坏");
            mUnitProfileList = null;
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
                setFilePath(downloadTempFilePath).create();
    }

    @Download.onTaskComplete public void onTaskComplete(DownloadTask task){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
        if(downloadTask == 1){
            Log.i(updaterTag, "Task 1 finished. Start checking ");
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
                    Log.d(updaterTag, "Current version is: " + currentVersion +
                            " Target version is: " + version + "Start download database.");
                    downloadTask = 2;
                    mProgressDialog = new ProgressDialog(getContext());
                    mProgressDialog.setTitle("正在下载数据库");
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                    downloadTaskID = Aria.download(this).load(Constant.databaseUrl).
                            ignoreFilePathOccupy().
                            setFilePath(downloadTempFilePath).create();
                }

            }catch (Exception e){
                swipeRefreshLayout.setRefreshing(false);
                Log.i(updaterTag, "JSON Decode Error");
            }
        }else if(downloadTask == 2){
            Log.i(updaterTag, "Task 2 finished. Start decompress");
            try {
                if(IO.isFileExist(databaseFilePath)){
                    IO.deleteFile(databaseFilePath);
                }
                Brotli.decompressFile(downloadTempFilePath, databaseFilePath);
                Intent intent = new Intent("com.guch8017.pcr.DATABASE_REFRESH");
                swipeRefreshLayout.setRefreshing(false);
                getActivity().sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
                swipeRefreshLayout.setRefreshing(false);
                Log.e(updaterTag, "Error while decompressing brotli file");
            }


        }else {
            Log.e(updaterTag,"Unknown task type ID: " + downloadTask);
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
            loadDatabase(context);
            mAdapter.notifyDataSetChanged();
        }
    }

    class UnitListAdapter extends ArrayAdapter<DBUnitProfile> {
        private int resourceId;
        public UnitListAdapter(Context context, int resourceID, List<DBUnitProfile> profiles){
            super(context,resourceID, profiles);
            resourceId = resourceID;
        }
        @NonNull @Override public View getView(int position, View convertView, ViewGroup parent){
            UnitViewHolder viewHolder;
            DBUnitProfile profile = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new UnitViewHolder();
                viewHolder.unit_id = convertView.findViewById(R.id.unit_id);
                viewHolder.unit_name = convertView.findViewById(R.id.unit_name);
                viewHolder.unit_nickname = convertView.findViewById(R.id.unit_nickname);
                viewHolder.unit_icon = convertView.findViewById(R.id.unit_image);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (UnitViewHolder) convertView.getTag();
            }
            viewHolder.unit_name.setText(profile.unit_name);
            viewHolder.unit_nickname.setText(profile.voice);
            viewHolder.unit_id.setText(String.valueOf(profile.unit_id));

            imageLoader.displayImage(Constant.unitImageUrl(profile.unit_id, 3),
                    viewHolder.unit_icon, displayImageOption);
            return convertView;
        }


        private class UnitViewHolder{
            TextView unit_id;
            TextView unit_name;
            TextView unit_nickname;
            ImageView unit_icon;
        }


    }
}