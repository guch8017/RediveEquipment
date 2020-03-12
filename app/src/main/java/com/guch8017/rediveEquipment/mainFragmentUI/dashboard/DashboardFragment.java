package com.guch8017.rediveEquipment.mainFragmentUI.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.activity.equipmentSearchActivity.EquipmentSearchActivity;
import com.guch8017.rediveEquipment.database.module.DBEquipmentData;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("WeakerAccess")
public class DashboardFragment extends Fragment {
    private DatabaseReflector databaseReflector;
    private ArrayList<Integer> equipmentIDs;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        databaseReflector = new DatabaseReflector(getContext());
        equipmentIDs = new ArrayList<>();
        List<DBEquipmentData> data = (List<DBEquipmentData>) (Object)(databaseReflector.reflectClass(DBEquipmentData.class.getName(), DBEquipmentData.tableName));
        if(data == null){
            return;
        }
        for (DBEquipmentData equipment: data
             ) {
            if (equipment.equipment_id < 810000) { //TODO: 测试掉落检测，改为110000
                equipmentIDs.add(equipment.equipment_id);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ListView listView = root.findViewById(R.id.equipment_list);
        EquipmentListAdapter adapter = new EquipmentListAdapter(getContext(), equipmentIDs);
        listView.setAdapter(adapter);
        return root;
    }

    private class EquipmentListAdapter extends BaseAdapter{
        private List<Integer> mList;

        private int count;
        private Context mContext;
        private LayoutInflater mInflater;
        private final DisplayImageOptions displayImageOptions = new
                DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_launcher_background).build();
        EquipmentListAdapter(Context context, List<Integer> equipmentIdList){
            mList = equipmentIdList;
            mContext = context;
            mInflater = LayoutInflater.from(context);
            int totalEquipCount = mList.size();
            count = totalEquipCount/6;
            int left = totalEquipCount%6;
            if(left != 0) count += 1;

        }

        @Override
        public int getCount(){
            return count;
        }

        @Override
        public Object getItem(int position){
            return 0;
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public boolean isEnabled(int position){
            return false;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            View view = mInflater.inflate(R.layout.equipment_list_item, null);
            int size = mList.size();
            for(int i = 0; i < 6 && (i + position * 6) < size; ++i){
                final int equipmentId = mList.get(i + position * 6);
                int viewId = getResources().getIdentifier("equipment_list_image_" + (i + 1),"id",getContext().getPackageName());
                ImageView img = view.findViewById(viewId);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 改为装备详情页
                        searchAndStartActivity(equipmentId);
                    }
                });
                ImageLoader.getInstance().displayImage(Constant.equipImageUrl(equipmentId),
                        new ImageViewAware(img, false), displayImageOptions);
            }
            return view;
        }

        private void searchAndStartActivity(final int eid){
            Intent intent = new Intent(getContext(), EquipmentSearchActivity.class);
            intent.putExtra("equipmentId", eid);
            startActivity(intent);
        }
    }
}