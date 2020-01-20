package com.guch8017.myapplication.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.guch8017.myapplication.R;


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