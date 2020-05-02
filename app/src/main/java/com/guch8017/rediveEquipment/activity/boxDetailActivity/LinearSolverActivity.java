package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.activity.boxDetailActivity.EquipRequirementActivity;
import com.guch8017.rediveEquipment.equipsolver.EquipDropMapCapsule;
import com.guch8017.rediveEquipment.equipsolver.EquipDropMatrix;
import com.guch8017.rediveEquipment.equipsolver.EquipDropNeedList;
import com.guch8017.rediveEquipment.equipsolver.EquipNeedCapsule;
import com.guch8017.rediveEquipment.equipsolver.EquipRequirementCalculator;
import com.guch8017.rediveEquipment.equipsolver.EquipSolver;

import java.util.ArrayList;
import java.util.HashMap;

public class LinearSolverActivity extends AppCompatActivity {
    private static final String TAG = "LinearSolverActivity";
    private final static String LOADING_INTENT = "LINEAR_SOLVER_DONE";
    private int mBoxId;
    private FinishLoadingReceiver mReceiver;
    private ProgressDialog mProgressDialog;
    private ListView mListView;
    private MapPlanningAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: START CALC");
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在计算");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        setContentView(R.layout.activity_box_char_selector);
        IntentFilter filter = new IntentFilter();
        filter.addAction(LOADING_INTENT);
        mReceiver = new FinishLoadingReceiver();
        this.registerReceiver(mReceiver, filter);
        mListView = findViewById(R.id.character_list);
        mBoxId = getIntent().getIntExtra("boxId", -1);
        new Thread(){
            @Override
            public void run() {
                try {
                    HashMap<Integer, Integer> equipMap = EquipRequirementCalculator.getEquipRequirement(LinearSolverActivity.this, LinearSolverActivity.this.mBoxId);
                    HashMap<Integer, Integer> pieceMap = EquipRequirementCalculator.getPieceRequirement(LinearSolverActivity.this, equipMap);
                    ArrayList<EquipNeedCapsule> list = EquipNeedCapsule.forHashMap(LinearSolverActivity.this, pieceMap);
                    ArrayList<EquipDropMapCapsule> mapList =  EquipSolver.SingleSolve(EquipDropMatrix.getInstance(LinearSolverActivity.this), EquipDropNeedList.forCapsule(list));
                    adapter = new MapPlanningAdapter(LinearSolverActivity.this, R.layout.map_requirement_item, mapList);
                    
                }catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(LOADING_INTENT);
                LinearSolverActivity.this.sendBroadcast(intent);
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private class FinishLoadingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            mListView.setAdapter(adapter);
        }
    }
}
