package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.activity.mainActivity.home.HomeFragment;
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBCharacter;
import com.guch8017.rediveEquipment.database.module.DBUnitData;
import com.guch8017.rediveEquipment.database.module.DBUnitProfile;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class CharacterSelectActivity extends AppCompatActivity {
    private int mBoxId;
    private BoxDatabase mDatabase;
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        mBoxId = getIntent().getIntExtra("boxId", -1);
        mDatabase = new BoxDatabase(this);
        StringBuilder sqlLimitBuilder = new StringBuilder();
        if(mBoxId != -1){
            List<DBCharacter> characters = mDatabase.getCharacterList(mBoxId);
            if(characters.size() != 0) {
                sqlLimitBuilder.append("unit_id NOT IN (");

                for (DBCharacter character : characters) {
                    sqlLimitBuilder.append(character.characterId);
                    sqlLimitBuilder.append(',');
                }
                sqlLimitBuilder.deleteCharAt(sqlLimitBuilder.length() - 1);
                sqlLimitBuilder.append(");");
            }
        }
        DatabaseReflector reflector = new DatabaseReflector(this);
        List<DBUnitProfile> unitData = (List<DBUnitProfile>) reflector.reflectClass(DBUnitProfile.class.getName(), DBUnitProfile.tableName, (sqlLimitBuilder.length() == 0)?null:sqlLimitBuilder.toString());
        setContentView(R.layout.activity_box_char_selector);
        ListView listView = findViewById(R.id.character_list);
        UnitListAdapter adapter = new UnitListAdapter(this, R.layout.unit_list_item, unitData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBUnitProfile profile = (DBUnitProfile)parent.getItemAtPosition(position);
                Intent intent = new Intent(CharacterSelectActivity.this, ModifyCharacterActivity.class);
                intent.putExtra("unitId",profile.unit_id);
                intent.putExtra("boxId", mBoxId);
                intent.putExtra("isNew", true);
                startActivity(intent);
                CharacterSelectActivity.this.finish();
            }
        });
    }

    class UnitListAdapter extends ArrayAdapter<DBUnitProfile> {
        private int resourceId;
        UnitListAdapter(Context context, int resourceID, List<DBUnitProfile> profiles){
            super(context,resourceID, profiles);
            resourceId = resourceID;
        }
        @NonNull
        @Override public View getView(int position, View convertView, @NonNull ViewGroup parent){
            UnitListAdapter.UnitViewHolder viewHolder;
            DBUnitProfile profile = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new UnitListAdapter.UnitViewHolder();
                viewHolder.unit_id = convertView.findViewById(R.id.unit_id);
                viewHolder.unit_name = convertView.findViewById(R.id.unit_name);
                viewHolder.catch_copy = convertView.findViewById(R.id.unit_nickname);
                viewHolder.unit_icon = convertView.findViewById(R.id.unit_image);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (UnitListAdapter.UnitViewHolder) convertView.getTag();
            }
            viewHolder.unit_name.setText(profile.unit_name);
            viewHolder.catch_copy.setText(profile.catch_copy);
            viewHolder.unit_id.setText(String.valueOf(profile.unit_id));

            ImageLoader.getInstance().displayImage(Constant.unitImageUrl(profile.unit_id, 3),
                    viewHolder.unit_icon, Constant.displayImageOption);
            return convertView;
        }
        private class UnitViewHolder{
            TextView unit_id;
            TextView unit_name;
            TextView catch_copy;
            ImageView unit_icon;
        }


    }
}
