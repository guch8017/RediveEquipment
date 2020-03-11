package com.guch8017.myapplication.equipmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.myapplication.R;
import com.guch8017.myapplication.database.DBEquipmentCraft;
import com.guch8017.myapplication.database.DBEquipmentData;
import com.guch8017.myapplication.database.DBEquipmentEnhanceData;
import com.guch8017.myapplication.database.DBEquipmentEnhanceRate;
import com.guch8017.myapplication.database.DBUniqueEquip;
import com.guch8017.myapplication.database.DBUniqueEquipmentCraft;
import com.guch8017.myapplication.database.DBUniqueEquipmentEnhanceData;
import com.guch8017.myapplication.database.DatabaseReflector;

import java.util.ArrayList;
import java.util.List;

public class EquipmentActivity extends AppCompatActivity {
    private static final String TAG = "EquipmentActivity";
    private int mEquipmentID;
    private boolean isUnique;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mEquipmentID = getIntent().getIntExtra("equipment_id", -1);
        if(mEquipmentID == -1){
            Log.e(TAG, "onCreate: 无法读取intent数据 ERR:UNABLE_READ_EQUIP_ID");
            return;
        }
        isUnique = getIntent().getBooleanExtra("isUnique", false);
        setContentView(R.layout.activity_equipment);
        final ListView listView = findViewById(R.id.activity_equip_list);

    }

    private class EquipmentAdapter extends BaseAdapter{
        private static final String TAG = "EquipmentAdapter";
        private ArrayList<String> mAttrName;
        private ArrayList<String> mAttrValue;
        private DBEquipmentData mData;  // 通用
        private DBEquipmentCraft mCraft;  // 普通
        private List<DBEquipmentEnhanceData> mEnhanceData;  // 普通
        private DBEquipmentEnhanceRate mEnhanceRate;  // 通用
        private DBUniqueEquipmentCraft mUniqueCraft;  // 专属
        private List<DBUniqueEquipmentEnhanceData> mUniqueEnhanceData;  // 专属

        @SuppressWarnings("unchecked")
        EquipmentAdapter(){
            mAttrName = new ArrayList<>();
            mAttrValue = new ArrayList<>();
            String sqlLimit = "equipment_id="+mEquipmentID;
            DatabaseReflector reflector = new DatabaseReflector(EquipmentActivity.this);
            // EquipmentData
            Object dataObj = reflector.reflectClass(DBEquipmentData.class.getName(),
                    (isUnique)?DBEquipmentData.tableNameUnique:DBEquipmentData.tableName,
                    sqlLimit);
            if(dataObj != null){
                List<DBEquipmentData> dataList = (List<DBEquipmentData>) dataObj;
                if(dataList.size() > 0){
                    mData = dataList.get(0);
                }
                else{
                    Log.e(TAG, "EquipmentAdapter: 无法获取武器数据 ERR:EQUIP_DATA_NOT_FOUND");
                    return;
                }
            }
            // EnhanceRate
            Object enhanceObj = reflector.reflectClass(DBEquipmentEnhanceRate.class.getName(),
                    (isUnique)?DBEquipmentEnhanceRate.tableNameUnique:DBEquipmentEnhanceRate.tableName,
                    sqlLimit);
            if(enhanceObj != null){
                List<DBEquipmentEnhanceRate>  rateList = (List<DBEquipmentEnhanceRate>) enhanceObj;
                if(rateList.size() > 0){
                    mEnhanceRate = rateList.get(0);
                }
                else {
                    Log.e(TAG, "EquipmentAdapter: 无法获取装备升级率 ERR:ENHANCE_RATE_NOT_FOUND");
                    return;
                }
            }
            if(isUnique){
                Object craftObj = reflector.reflectClass(DBUniqueEquipmentCraft.class.getName(),
                        DBUniqueEquipmentCraft.tableName, sqlLimit);
                if(craftObj != null){
                    List<DBUniqueEquipmentCraft> craftList = (List<DBUniqueEquipmentCraft>)craftObj;
                    if(craftList.size() > 0){
                        mUniqueCraft = craftList.get(0);
                    }else {
                        Log.e(TAG, "EquipmentAdapter: 无法获取专属武器合成数据 ERR:UNIQUE_CRAFT_NOT_FOUND");
                        return;
                    }
                }

                Object enhanceDataObj = reflector.reflectClass(DBUniqueEquipmentEnhanceData.class.getName(),
                        DBUniqueEquipmentEnhanceData.tableName, sqlLimit);
                if(enhanceDataObj != null){
                    mUniqueEnhanceData = (List<DBUniqueEquipmentEnhanceData>)craftObj;
                }else {
                    Log.e(TAG, "EquipmentAdapter: 无法获取专属武器升级数据 ERR:UNIQUE_DATA_NOT_FOUND");
                    return;
                }
            }else{
                if(mData.craft_flg == 1) {
                    Object craftObj = reflector.reflectClass(DBEquipmentCraft.class.getName(),
DBEquipmentCraft.tableName);
                    if (craftObj != null) {
                        List<DBEquipmentCraft> craftList = (List<DBEquipmentCraft>) craftObj;
                        if (craftList.size() > 0) {
                            mCraft = craftList.get(0);
                        } else {
                            Log.e(TAG, "EquipmentAdapter: 无法获取装备合成数据 ERR:EQUIP_CRAFT_NOT_FOUND");
                            return;
                        }
                    }
                }

                Object enhanceDataObj = reflector.reflectClass(DBEquipmentEnhanceData.class.getName(),
                        DBEquipmentEnhanceData.tableName, sqlLimit);
                if(enhanceDataObj != null){
                    List<DBEquipmentEnhanceData> enhanceDataList = (List<DBEquipmentEnhanceData>)enhanceDataObj;
                    if(enhanceDataList.size() > 0){
                        //mEnhanceData = enhanceDataList.get(0);
                    }else {
                        Log.e(TAG, "EquipmentAdapter: 无法获取装备升级数据 ERR:EQUIP_DATA_NOT_FOUND");
                        return;
                    }
                }
            }

            if(mData.atk != 0){
                mAttrName.add(EquipmentActivity.this.getString(R.string.atk));
                double value = mData.atk;
                if(isUnique){
                    if(true){

                    }
                }
            }




        }

        @Override
        public int getCount(){
            return mAttrValue.size();
        }

        @Override
        public View getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return null;
        }

    }

}
