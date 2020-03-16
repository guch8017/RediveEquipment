package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.module.DBCharacter;

import java.util.ArrayList;
import java.util.List;

public class BoxCharacterActivity extends AppCompatActivity {
    private BoxDatabase mDatabase;
    private int boxId;
    private List<DBCharacter> characters;
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        boxId = getIntent().getIntExtra("boxId", -1);
        if(boxId == -1){
            return;
        }
        mDatabase = new BoxDatabase(this);
        setContentView(R.layout.activity_box_character);
        ListView charList = findViewById(R.id.character_list);

        findViewById(R.id.add_char_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoxCharacterActivity.this, AddBoxCharacterActivity.class);
                intent.putExtra("boxId", boxId);
                startActivity(intent);
            }
        });

    }

    class CharacterAdapter extends ArrayAdapter<DBCharacter> {
        CharacterAdapter(Context context, int ResId, List<DBCharacter> list){
            super(context, ResId, list);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent)
        {
            DBCharacter character = getItem(position);
            CharacterViewHolder vh;
            if(convertView == null){
                vh = new CharacterViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.character_equip_item, parent, false);
                vh.characterImage = convertView.findViewById(R.id.character_image);
                vh.currentEquip = convertView.findViewById(R.id.currentEquip);
                vh.currentRank = convertView.findViewById(R.id.currentRank);
                vh.targetEquip = convertView.findViewById(R.id.targetEquip);
                vh.targetRank = convertView.findViewById(R.id.targetRank);
                convertView.setTag(vh);
            }else{
                vh = (CharacterViewHolder)convertView.getTag();
            }
            vh.targetRank.setText("Rank " + character.targetRank);
            StringBuilder builder = new StringBuilder();
            // TODO: 角色rank及装备状态显示
            return convertView;
        }

        class CharacterViewHolder{
            ImageView characterImage;
            TextView currentRank;
            TextView targetRank;
            TextView currentEquip;
            TextView targetEquip;
        }
    }
}
