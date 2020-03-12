package com.guch8017.myapplication.mainFragmentUI.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.guch8017.myapplication.R;
import com.guch8017.myapplication.database.module.DBEquipmentData;
import com.guch8017.myapplication.database.DatabaseReflector;
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
            if (equipment.equipment_id < 110000) {
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
            ImageView img1 = view.findViewById(R.id.equipment_list_image_1);
            ImageView img2 = view.findViewById(R.id.equipment_list_image_2);
            ImageView img3 = view.findViewById(R.id.equipment_list_image_3);
            ImageView img4 = view.findViewById(R.id.equipment_list_image_4);
            ImageView img5 = view.findViewById(R.id.equipment_list_image_5);
            ImageView img6 = view.findViewById(R.id.equipment_list_image_6);
            if (position == count - 1){
                int size = mList.size();
                int current = position * 6;
                if(current < size){
                    ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                            new ImageViewAware(img1, false), displayImageOptions);
                }
                current += 1;
                if(current < size){
                    ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                            new ImageViewAware(img2, false), displayImageOptions);
                }
                current += 1;
                if(current < size){
                    ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                            new ImageViewAware(img3, false), displayImageOptions);
                }
                current += 1;
                if(current < size){
                    ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                            new ImageViewAware(img4, false), displayImageOptions);
                }
                current += 1;
                if(current < size){
                    ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                            new ImageViewAware(img5, false), displayImageOptions);
                }
                current += 1;
                if(current < size){
                    ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                            new ImageViewAware(img6, false), displayImageOptions);
                }
            }else{
                int current = position * 6;
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                        new ImageViewAware(img1, false), displayImageOptions);
                current += 1;
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                        new ImageViewAware(img2, false), displayImageOptions);
                current += 1;
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                        new ImageViewAware(img3, false), displayImageOptions);
                current += 1;
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                        new ImageViewAware(img4, false), displayImageOptions);
                current += 1;
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                        new ImageViewAware(img5, false), displayImageOptions);
                current += 1;
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (mList.get(current)) + ".webp",
                        new ImageViewAware(img6, false), displayImageOptions);
                current += 1;
            }
            return view;
        }
    }
}