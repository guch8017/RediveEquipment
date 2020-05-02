package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBQuestData;
import com.guch8017.rediveEquipment.equipsolver.EquipDropCapsule;
import com.guch8017.rediveEquipment.equipsolver.EquipDropMapCapsule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MapPlanningAdapter extends ArrayAdapter<EquipDropMapCapsule> {
    List<String> mapName;
    public MapPlanningAdapter(Context context, int resId, List<EquipDropMapCapsule> capsules){
        super(context, resId, capsules);
        DatabaseReflector reflector = new DatabaseReflector(context);
        mapName = new ArrayList<>();
        capsules.sort(new Comparator<EquipDropMapCapsule>() {
            @Override
            public int compare(EquipDropMapCapsule o1, EquipDropMapCapsule o2) {
                return o2.getIntTime() - o1.getIntTime();
            }
        });
        for(EquipDropMapCapsule capsule: capsules){
            List<DBQuestData> data = (List<DBQuestData>)reflector.reflectClass(DBQuestData.class.getName(), DBQuestData.tableName, "quest_id = " + capsule.getMapQuestId());
            if(data != null && data.size() != 0){
                mapName.add(data.get(0).quest_name);
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.map_requirement_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.questName = convertView.findViewById(R.id.map_name);
            viewHolder.time = convertView.findViewById(R.id.map_count);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.time.setText(String.valueOf(getItem(position).getIntTime()));
        viewHolder.questName.setText(mapName.get(position));
        return convertView;
    }

    private class ViewHolder{
        TextView questName;
        TextView time;
    }
}
