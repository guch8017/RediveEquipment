package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;

import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBCharacter;
import com.guch8017.rediveEquipment.database.module.DBUnitPromotion;
import com.guch8017.rediveEquipment.equipsolver.EquipNeedCapsule;
import com.guch8017.rediveEquipment.equipsolver.EquipRequirementCalculator;
import com.guch8017.rediveEquipment.util.Constant;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipRequirementActivity extends AppCompatActivity {
    private static final String TAG = "EquipRequirementActivity";
    private int mBoxId;
    private ListView mListView;
    private HashMap<Integer, Integer> equip;
    private List<EquipNeedCapsule> mNeedList;
    private FinishLoadingReceiver mReceiver;
    private ProgressDialog mProgressDialog;
    private static final String LOADING_INTENT = "EQUIP_CALC_DONE";
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在计算");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mBoxId = getIntent().getIntExtra("boxId", -1);
        final boolean isPiece = getIntent().getBooleanExtra("isPiece", false);
        setContentView(R.layout.activity_box_char_selector);
        mListView = findViewById(R.id.character_list);
        equip = new HashMap<>();
        mNeedList = new ArrayList<>();
        mListView.setAdapter(new EquipmentRequirementAdapter(this, R.layout.equipment_detail_item_1, mNeedList));

        IntentFilter filter = new IntentFilter();
        filter.addAction(LOADING_INTENT);
        mReceiver = new FinishLoadingReceiver();
        this.registerReceiver(mReceiver, filter);

        new Thread(){
            @Override
            public void run() {
                EquipRequirementActivity.this.equip = EquipRequirementCalculator.getEquipRequirement(EquipRequirementActivity.this, EquipRequirementActivity.this.mBoxId);
                if(isPiece){
                    try {
                        EquipRequirementActivity.this.equip = EquipRequirementCalculator.getPieceRequirement(EquipRequirementActivity.this, EquipRequirementActivity.this.equip);
                    }catch (Exception e){
                        Log.e(TAG, "run: " + e.getMessage());
                    }
                }
                ArrayList<EquipNeedCapsule> list = EquipNeedCapsule.forHashMap(EquipRequirementActivity.this, equip);

                mNeedList.clear();
                mNeedList.addAll(list);
                mNeedList.sort(new Comparator<EquipNeedCapsule>() {
                    @Override
                    public int compare(EquipNeedCapsule o1, EquipNeedCapsule o2) {
                        return - o1.compareTo(o2);
                    }
                });
                Intent intent = new Intent(LOADING_INTENT);
                EquipRequirementActivity.this.sendBroadcast(intent);
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
            ((EquipmentRequirementAdapter)mListView.getAdapter()).notifyDataSetChanged();
        }
    }


}
