package com.guch8017.rediveEquipment.activity.unitDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.module.DBUniqueEquip;
import com.guch8017.rediveEquipment.database.module.DBUnitPromotion;
import com.guch8017.rediveEquipment.activity.equipmentActivity.EquipmentActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

public class UnitEquipmentFragment extends Fragment {
    private List<DBUnitPromotion> mUnitPromotion;
    private DBUniqueEquip mUniqueEquip;
    private Context mContext;

    public static UnitEquipmentFragment getInstance(List<DBUnitPromotion> promotion, DBUniqueEquip uniqueEquip){
        UnitEquipmentFragment fragment = new UnitEquipmentFragment();
        fragment.mUnitPromotion = promotion;
        fragment.mUniqueEquip = uniqueEquip;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_unit_detail_list, container, false);
        final ListView listView = root.findViewById(R.id.unit_detail_list);
        EquipmentAdapter adapter = new EquipmentAdapter(mContext, mUnitPromotion, mUniqueEquip);
        listView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }


    public class EquipmentAdapter extends BaseAdapter {
        Context mContext;
        LayoutInflater mInflater;
        List<DBUnitPromotion> unitPromotions;
        DBUniqueEquip uniqueEquip;
        private final DisplayImageOptions displayImageOptions = new
                DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_launcher_background).build();

        public EquipmentAdapter(Context context, List<DBUnitPromotion> data, DBUniqueEquip uniqueEquip){
            mContext = context;
            unitPromotions = data;
            this.uniqueEquip = uniqueEquip;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount(){
            if(uniqueEquip != null) {
                return unitPromotions.size() + 1;
            }else{
                return unitPromotions.size();
            }
        }

        @Override
        public boolean isEnabled(int position){
            return false;
        }

        @Override
        public int getViewTypeCount(){
            return 2;
        }

        @Override
        public int getItemViewType(int position){
            if(uniqueEquip != null && position == 0) return 1;
            else return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            int type = getItemViewType(position);
            if(type == 0) {
                // 不使用Item复用防止点击事件异常
                EquipViewHolder viewHolder;
                View view;
                int index = position;
                if(mUniqueEquip != null) index -= 1;
                final DBUnitPromotion unit_promotion = unitPromotions.get(index);
                view = mInflater.inflate(R.layout.unit_equipment_list_item, parent, false);
                viewHolder = new EquipViewHolder();
                viewHolder.te = view.findViewById(R.id.unit_equipment_list_rank_text);
                viewHolder.e1 = view.findViewById(R.id.unit_equipment_list_image_1);
                if (unit_promotion.equip_slot_1 != 999999) {
                    viewHolder.e1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EquipmentActivity.class);
                            intent.putExtra("equipment_id", unit_promotion.equip_slot_1);
                            intent.putExtra("isUnique",false);
                            //startActivity(intent);
                        }
                    });
                }
                viewHolder.e2 = view.findViewById(R.id.unit_equipment_list_image_2);
                if (unit_promotion.equip_slot_2 != 999999) {
                    viewHolder.e2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EquipmentActivity.class);
                            intent.putExtra("equipment_id", unit_promotion.equip_slot_2);
                            intent.putExtra("isUnique",false);
                            //startActivity(intent);
                        }
                    });
                }
                viewHolder.e3 = view.findViewById(R.id.unit_equipment_list_image_3);
                if (unit_promotion.equip_slot_3 != 999999) {
                    viewHolder.e3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EquipmentActivity.class);
                            intent.putExtra("equipment_id", unit_promotion.equip_slot_3);
                            intent.putExtra("isUnique",false);
                            //startActivity(intent);
                        }
                    });
                }
                viewHolder.e4 = view.findViewById(R.id.unit_equipment_list_image_4);
                if (unit_promotion.equip_slot_4 != 999999) {
                    viewHolder.e4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EquipmentActivity.class);
                            intent.putExtra("equipment_id", unit_promotion.equip_slot_4);
                            intent.putExtra("isUnique",false);
                            //startActivity(intent);
                        }
                    });
                }
                viewHolder.e5 = view.findViewById(R.id.unit_equipment_list_image_5);
                if (unit_promotion.equip_slot_5 != 999999) {
                    viewHolder.e5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EquipmentActivity.class);
                            intent.putExtra("equipment_id", unit_promotion.equip_slot_5);
                            intent.putExtra("isUnique",false);
                            //startActivity(intent);
                        }
                    });
                }
                viewHolder.e6 = view.findViewById(R.id.unit_equipment_list_image_6);
                if (unit_promotion.equip_slot_6 != 999999) {
                    viewHolder.e6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EquipmentActivity.class);
                            intent.putExtra("equipment_id", unit_promotion.equip_slot_6);
                            intent.putExtra("isUnique",false);
                            //startActivity(intent);
                        }
                    });
                }
                view.setTag(viewHolder);
                viewHolder.te.setText("Rank " + unit_promotion.promotion_level);
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (unit_promotion.equip_slot_1) + ".webp",
                        new ImageViewAware(viewHolder.e1, false), displayImageOptions);
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (unit_promotion.equip_slot_2) + ".webp",
                        new ImageViewAware(viewHolder.e2, false), displayImageOptions);
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (unit_promotion.equip_slot_3) + ".webp",
                        new ImageViewAware(viewHolder.e3, false), displayImageOptions);
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (unit_promotion.equip_slot_4) + ".webp",
                        new ImageViewAware(viewHolder.e4, false), displayImageOptions);
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (unit_promotion.equip_slot_5) + ".webp",
                        new ImageViewAware(viewHolder.e5, false), displayImageOptions);
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (unit_promotion.equip_slot_6) + ".webp",
                        new ImageViewAware(viewHolder.e6, false), displayImageOptions);
                return view;
            }else{
                UniqueEquipViewHolder viewHolder = new UniqueEquipViewHolder();
                View view = mInflater.inflate(R.layout.unit_equipment_list_item, parent, false);
                viewHolder.te = view.findViewById(R.id.unit_equipment_list_rank_text);
                viewHolder.iv = view.findViewById(R.id.unit_equipment_list_image_1);
                view.setTag(viewHolder);
                viewHolder.te.setText("专属装备");
                viewHolder.iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, EquipmentActivity.class);
                        intent.putExtra("equipment_id", uniqueEquip.equip_id);
                        intent.putExtra("isUnique",true);
                        //startActivity(intent);
                    }
                });
                ImageLoader.getInstance().displayImage("https://redive.estertion.win/icon/equipment/" + (uniqueEquip.equip_id) + ".webp",
                        new ImageViewAware(viewHolder.iv, false), displayImageOptions);
                return view;
            }
        }

        @Override
        public Object getItem(int position){
            return 0;
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        class EquipViewHolder{
            TextView te;
            ImageView e1,e2,e3,e4,e5,e6;
        }

        class UniqueEquipViewHolder{
            TextView te;
            ImageView iv;
        }
    }

}
