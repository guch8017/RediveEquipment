package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.equipsolver.EquipNeedCapsule;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

public class EquipmentRequirementAdapter extends ArrayAdapter<EquipNeedCapsule> {
    EquipmentRequirementAdapter(Context context, int ResId, List<EquipNeedCapsule> list){
        super(context, ResId, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.equipment_detail_item_1, parent, false);
            viewHolder.equipImg = convertView.findViewById(R.id.equipment_image);
            viewHolder.equipName = convertView.findViewById(R.id.equipment_name);
            viewHolder.requirement = convertView.findViewById(R.id.equipment_count);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        EquipNeedCapsule capsule = getItem(position);
        viewHolder.requirement.setText(String.valueOf(capsule.getRequirement()));
        viewHolder.equipName.setText(capsule.getEquipmentName());
        ImageLoader.getInstance().displayImage(Constant.equipImageUrl(capsule.getEquipmentId()),
                new ImageViewAware(viewHolder.equipImg, false), Constant.displayImageOption);
        return convertView;
    }

    public class ViewHolder{
        ImageView equipImg;
        TextView equipName;
        TextView requirement;
    }
}
