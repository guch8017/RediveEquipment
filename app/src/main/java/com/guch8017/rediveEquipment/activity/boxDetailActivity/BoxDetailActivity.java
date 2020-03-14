package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;

import java.util.ArrayList;

public class BoxDetailActivity extends AppCompatActivity {
    private int boxId;
    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        boxId = getIntent().getIntExtra("boxId", -1);
        setContentView(R.layout.activity_box_detail);
        listView = findViewById(R.id.boxDetailList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: Box设置/浏览按钮响应
            }
        });
    }

    class BDLAdapter extends ArrayList<String>{

    }
}
