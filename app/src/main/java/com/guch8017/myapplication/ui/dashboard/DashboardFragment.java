package com.guch8017.myapplication.ui.dashboard;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.guch8017.myapplication.R;
import com.guch8017.myapplication.utli.Constant;
import com.guch8017.myapplication.utli.IO;
import com.netease.hearttouch.brotlij.Brotli;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;



@SuppressWarnings("WeakerAccess")
public class DashboardFragment extends Fragment {
    private long downloadTaskID;
    private int downloadTask; //task=1的回调代表检查更新完成，task=2的回调代表下载数据的操作完成
    private ProgressDialog mProgressDialog;
    private String downloadTempFilePath;
    private static final String updaterTag = "Update Checker";
    private String currentVersion;
    private String databaseFilePath;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        textView.setText("Hello World");
        final Button button = root.findViewById(R.id.btn_check_update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        downloadTempFilePath = context.getFilesDir().toString() + "/temp";
        databaseFilePath = context.getDatabasePath("database.db").toString();
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
                getActivity().sendBroadcast(intent);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(updaterTag, "Error while decompressing brotli file");
            }


        }else {
            Log.e(updaterTag,"Unknown task type ID: " + downloadTask);
        }
    }


    @Download.onTaskFail public void onTaskFail(DownloadTask task){
        mProgressDialog.dismiss();
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
}