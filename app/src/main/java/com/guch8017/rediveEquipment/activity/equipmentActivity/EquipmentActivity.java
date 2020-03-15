package com.guch8017.rediveEquipment.activity.equipmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.activity.equipmentSearchActivity.EquipmentSearchActivity;
import com.guch8017.rediveEquipment.database.module.DBEquipmentCraft;
import com.guch8017.rediveEquipment.database.module.DBEquipmentData;
import com.guch8017.rediveEquipment.database.module.DBEquipmentEnhanceData;
import com.guch8017.rediveEquipment.database.module.DBEquipmentEnhanceRate;
import com.guch8017.rediveEquipment.database.module.DBUniqueEquipmentCraft;
import com.guch8017.rediveEquipment.database.module.DBUniqueEquipmentEnhanceData;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.lang.reflect.Field;
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
        EquipmentAdapter adapter = new EquipmentAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EquipmentAttr attr = (EquipmentAttr)parent.getAdapter().getItem(position);
                if(attr.needCraft){
                    Intent intent = new Intent(EquipmentActivity.this, EquipmentActivity.class);
                    intent.putExtra("equipment_id", attr.equipId);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(EquipmentActivity.this, EquipmentSearchActivity.class);
                    intent.putExtra("equipmentId", attr.equipId);
                    startActivity(intent);
                }
            }
        });
    }

    enum ViewType{
        Collection,
        Craft,
        Property
    }

    class EquipmentAttr{
        ViewType type;
        String attrName;
        String attrValue;
        boolean needCraft;
        int equipId; // Only available when type == Craft

        EquipmentAttr(String attrName, String attrValue){
            this.attrName = attrName;
            this.attrValue = attrValue;
            this.type = ViewType.Property;
        }

        EquipmentAttr(int equipId, String equipName, int equipCount, boolean needCraft){
            this.attrName = equipName;
            this.attrValue = String.valueOf(equipCount);
            this.equipId = equipId;
            this.needCraft = needCraft;
            this.type = ViewType.Craft;
        }
    }

    private class EquipmentAdapter extends BaseAdapter{
        private static final String TAG = "EquipmentAdapter";
        private List<EquipmentAttr> attrList;
        private DBEquipmentData mData;  // 通用
        private DBEquipmentCraft mCraft;  // 普通
        private List<DBEquipmentEnhanceData> mEnhanceData;  // 普通
        private DBEquipmentEnhanceRate mEnhanceRate;  // 通用
        private DBUniqueEquipmentCraft mUniqueCraft;  // 专属
        private List<DBUniqueEquipmentEnhanceData> mUniqueEnhanceData;  // 专属

        @SuppressWarnings("unchecked")
        EquipmentAdapter(){
            attrList = new ArrayList<>();
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
DBEquipmentCraft.tableName, sqlLimit);
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
            }

            if(mData.craft_flg == 0){
                // No need to craft.
                attrList.add(new EquipmentAttr(mData.equipment_id, mData.equipment_name, 1, false));
            }else{
                if(isUnique){
                    if(mUniqueCraft.consume_num_1 != 0){
                        Object data = reflector.reflectClass(DBEquipmentData.class.getName(),
                                DBEquipmentData.tableName, "equipment_id="+mUniqueCraft.item_id_1);
                        if(data != null && ((List<DBEquipmentData>)data).size() > 0){
                            DBEquipmentData data1 = ((List<DBEquipmentData>)data).get(0);
                            attrList.add(new EquipmentAttr(mUniqueCraft.item_id_1, data1.equipment_name, mUniqueCraft.consume_num_1, data1.craft_flg == 1)) ;
                        }
                    }

                    if(mUniqueCraft.consume_num_2 != 0){
                        Object data = reflector.reflectClass(DBEquipmentData.class.getName(),
                                DBEquipmentData.tableName, "equipment_id="+mUniqueCraft.item_id_2);
                        if(data != null && ((List<DBEquipmentData>)data).size() > 0){
                            DBEquipmentData data1 = ((List<DBEquipmentData>)data).get(0);
                            attrList.add(new EquipmentAttr(mUniqueCraft.item_id_2, data1.equipment_name, mUniqueCraft.consume_num_2, data1.craft_flg == 1)) ;
                        }
                    }

                    if(mUniqueCraft.crafted_cost != 0){
                        attrList.add(new EquipmentAttr(EquipmentActivity.this.getString(R.string.crafted_cost), String.valueOf(mUniqueCraft.crafted_cost)));
                    }
                }else{
                    if(mCraft.consume_num_1 != 0){
                        Object data = reflector.reflectClass(DBEquipmentData.class.getName(),
                                DBEquipmentData.tableName, "equipment_id="+mCraft.condition_equipment_id_1);
                        if(data != null && ((List<DBEquipmentData>)data).size() > 0){
                            DBEquipmentData data1 = ((List<DBEquipmentData>)data).get(0);
                            attrList.add(new EquipmentAttr(mCraft.condition_equipment_id_1, data1.equipment_name, mCraft.consume_num_1, data1.craft_flg == 1)) ;
                        }
                    }
                    if(mCraft.consume_num_2 != 0){
                        Object data = reflector.reflectClass(DBEquipmentData.class.getName(),
                                DBEquipmentData.tableName, "equipment_id="+mCraft.condition_equipment_id_2);
                        if(data != null && ((List<DBEquipmentData>)data).size() > 0){
                            DBEquipmentData data1 = ((List<DBEquipmentData>)data).get(0);
                            attrList.add(new EquipmentAttr(mCraft.condition_equipment_id_2, data1.equipment_name, mCraft.consume_num_2, data1.craft_flg == 1)) ;
                        }
                    }
                    if(mCraft.consume_num_3 != 0){
                        Object data = reflector.reflectClass(DBEquipmentData.class.getName(),
                                DBEquipmentData.tableName, "equipment_id="+mCraft.condition_equipment_id_3);
                        if(data != null && ((List<DBEquipmentData>)data).size() > 0){
                            DBEquipmentData data1 = ((List<DBEquipmentData>)data).get(0);
                            attrList.add(new EquipmentAttr(mCraft.condition_equipment_id_3, data1.equipment_name, mCraft.consume_num_3, data1.craft_flg == 1)) ;
                        }
                    }
                    if(mCraft.consume_num_4 != 0){
                        Object data = reflector.reflectClass(DBEquipmentData.class.getName(),
                                DBEquipmentData.tableName, "equipment_id="+mCraft.condition_equipment_id_4);
                        if(data != null && ((List<DBEquipmentData>)data).size() > 0){
                            DBEquipmentData data1 = ((List<DBEquipmentData>)data).get(0);
                            attrList.add(new EquipmentAttr(mCraft.condition_equipment_id_4, data1.equipment_name, mCraft.consume_num_4, data1.craft_flg == 1)) ;
                        }
                    }
                    if(mCraft.crafted_cost != 0){
                        attrList.add(new EquipmentAttr(EquipmentActivity.this.getString(R.string.crafted_cost), String.valueOf(mCraft.crafted_cost)));
                    }
                }
            }

            try {
                Class reflectClass = Class.forName(DBEquipmentData.class.getName());
                Field[] fields = reflectClass.getFields();
                for(Field field: fields){
                    if(field.getGenericType().toString().equals("double")){
                        double data = field.getDouble(mData);
                        if(data != 0.0d){
                            attrList.add(new EquipmentAttr(EquipmentActivity.this.getString(EquipmentActivity.this.getResources().getIdentifier(field.getName(), "string", EquipmentActivity.this.getPackageName())), String.valueOf(data)));
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public int getCount(){
            return attrList.size();
        }

        @Override
        public EquipmentAttr getItem(int position){
            return attrList.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public int getItemViewType(int position){
            switch (attrList.get(position).type){
                case Craft:
                    return 1;
                case Property:
                    return 2;
                case Collection:
                    return 3;
                default:
                    return -1;
            }
        }

        @Override
        public int getViewTypeCount(){
            return 3;
        }

        @Override
        public boolean isEnabled(int position){
            EquipmentAttr attr = attrList.get(position);
            return attr.type == ViewType.Craft;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            EquipmentAttr attr = attrList.get(position);
            ViewHolder1 vh1;
            ViewHolder2 vh2;
            switch (attr.type){
                case Property:
                    if(convertView == null){
                        convertView = LayoutInflater.from(EquipmentActivity.this).inflate(R.layout.equipment_detail_item_2, parent, false);
                        vh2 = new ViewHolder2();
                        vh2.attrName = convertView.findViewById(R.id.attrName);
                        vh2.attrValue = convertView.findViewById(R.id.attrValue);
                        convertView.setTag(vh2);
                    }else{
                        vh2 = (ViewHolder2) convertView.getTag();
                    }

                    vh2.attrValue.setText(attr.attrValue);
                    vh2.attrName.setText(attr.attrName);
                    break;
                case Craft:
                    if(convertView == null){
                        convertView = LayoutInflater.from(EquipmentActivity.this).inflate(R.layout.equipment_detail_item_1, parent, false);
                        vh1 = new ViewHolder1();
                        vh1.equipCount = convertView.findViewById(R.id.equipment_count);
                        vh1.equipName = convertView.findViewById(R.id.equipment_name);
                        vh1.equipImage = convertView.findViewById(R.id.equipment_image);
                        convertView.setTag(vh1);
                    }else{
                        vh1 = (ViewHolder1) convertView.getTag();
                    }

                    vh1.equipName.setText(attr.attrName);
                    vh1.equipCount.setText(attr.attrValue);
                    ImageLoader.getInstance().displayImage(Constant.equipImageUrl(attr.equipId),
                            new ImageViewAware(vh1.equipImage, false),
                            Constant.displayImageOption);
                    break;
                default:
                    break;
            }
            return convertView;
        }

        class ViewHolder1{
            ImageView equipImage;
            TextView equipName;
            TextView equipCount;
        }

        class ViewHolder2{
            TextView attrName;
            TextView attrValue;
        }

    }

}
