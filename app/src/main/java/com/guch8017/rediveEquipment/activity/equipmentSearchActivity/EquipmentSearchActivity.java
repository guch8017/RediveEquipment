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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBEquipmentData;
import com.guch8017.rediveEquipment.database.module.DBQuest;
import com.guch8017.rediveEquipment.database.module.DBWave;
import com.guch8017.rediveEquipment.database.module.GetQuests;
import com.guch8017.rediveEquipment.mainFragmentUI.home.HomeFragment;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
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
                StringBuilder builder = new StringBuilder("数据查询结果:");
                for(DBQuest quest : quests){
                    builder.append(quest.quest_name);
                    builder.append(',');
                }
                Log.i("Debug", builder.toString());
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
            //mList.setAdapter(new ListAdapter(quests));
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
            return null;
        }

        @Override
        public boolean isEnabled(int position){
            return false;
        }

        private class ListViewHolder{
            TextView mapName;

        }
    }
}
