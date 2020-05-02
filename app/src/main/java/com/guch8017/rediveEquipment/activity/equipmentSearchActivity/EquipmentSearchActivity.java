package com.guch8017.rediveEquipment.activity.equipmentSearchActivity;

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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBEquipmentData;
import com.guch8017.rediveEquipment.database.module.DBQuest;
import com.guch8017.rediveEquipment.database.module.DBWave;
import com.guch8017.rediveEquipment.database.module.GetQuests;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

public class EquipmentSearchActivity extends AppCompatActivity {
    private int equipmentId;
    private ProgressDialog mProgressDialog;
    private List<DBQuest> quests;
    public String equipmentName;
    private ListView mList;
    private FinishLoadingReceiver mReceiver;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在查询");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        equipmentId = getIntent().getIntExtra("equipmentId", -1);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.guch8017.pcr.FINISH_LOADING");
        mReceiver = new FinishLoadingReceiver();
        this.registerReceiver(mReceiver, filter);

        setContentView(R.layout.activity_equipment_search_list);
        mList = findViewById(R.id.list);
        mList.setNestedScrollingEnabled(false);

        new Thread(){
            @Override
            public void run(){

                final DatabaseReflector reflector = new DatabaseReflector(EquipmentSearchActivity.this);
                equipmentName = ((List<DBEquipmentData>)(reflector.reflectClass(DBEquipmentData.class.getName(), DBEquipmentData.tableName, "equipment_id = " + equipmentId))).get(0).equipment_name;
                setTitle(equipmentName);
                quests = GetQuests.getQuests(reflector, equipmentId);
                Intent intent = new Intent("com.guch8017.pcr.FINISH_LOADING");
                EquipmentSearchActivity.this.sendBroadcast(intent);
                mProgressDialog.dismiss();
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
            mList.setAdapter(new ListAdapter(quests));
        }
    }

    class ListAdapter extends BaseAdapter{
        private List<DBQuest> quests;
        //private GridLayoutManager layoutManager;
        ListAdapter(List<DBQuest> quests){
            this.quests = quests;
        }

        @Override
        public int getCount(){
            return quests.size();
        }

        @Override
        public Object getItem(int position){
            return position;
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            DBQuest quest = quests.get(position);
            int i = 1;
            View view = LayoutInflater.from(EquipmentSearchActivity.this).inflate(R.layout.drop_result_item, parent, false);
            TextView map = view.findViewById(R.id.mapName);
            TextView type = view.findViewById(R.id.mapType);
            map.setText(quest.quest_name);
            switch (quest.getAreaType()){
                case hard:
                    type.setText("Hard");
                    break;
                case normal:
                    type.setText("Normal");
                    break;
                case veryhard:
                    type.setText("Very Hard");
                    break;
                case exploration:
                    type.setText("探索");
                    break;
                case shrine:
                    type.setText("圣迹调查");
                    break;
                case temple:
                    type.setText("神殿调查");
                    break;
                case unknown:
                    type.setText("未知地图类型");
                    break;
            }
            for(DBWave.Reward reward : quest.rewards){
                int imgViewId = getResources().getIdentifier("drop_item_img" + i,"id",EquipmentSearchActivity.this.getPackageName());
                int oddViewId = getResources().getIdentifier("drop_item_odd" + i,"id",EquipmentSearchActivity.this.getPackageName());
                ImageView img = view.findViewById(imgViewId);
                TextView text = view.findViewById(oddViewId);
                text.setText(reward.odds + "%");
                ImageLoader.getInstance().displayImage(Constant.equipImageUrl(reward.rewardID),
                        new ImageViewAware(img, false), Constant.displayImageOption);
                i += 1;
            }
            return view;
        }

        @Override
        public boolean isEnabled(int position){
            return false;
        }
    }
}
