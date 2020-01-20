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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        textView.setText("Hello World");
        final Button button = root.findViewById(R.id.btn_check_update);
        return root;
    }


}