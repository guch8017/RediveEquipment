package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.module.DBCharacter;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

public class BoxCharacterActivity extends AppCompatActivity {
    private static final String TAG = "BoxCharacterActivity";
    private BoxDatabase mDatabase;
    private int boxId;
    private List<DBCharacter> characters;
    private ListView mListView;
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        boxId = getIntent().getIntExtra("boxId", -1);
        if(boxId == -1){
            return;
        }
        mDatabase = new BoxDatabase(this);
        setContentView(R.layout.activity_box_character);
        mListView = findViewById(R.id.character_list);
        characters = mDatabase.getCharacterList(boxId);
        mListView.setAdapter(new CharacterAdapter(this, R.layout.character_equip_item, characters));
        findViewById(R.id.add_char_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoxCharacterActivity.this, CharacterSelectActivity.class);
                intent.putExtra("boxId", boxId);
                startActivity(intent);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBCharacter character = characters.get(position);
                Intent intent = new Intent(BoxCharacterActivity.this, ModifyCharacterActivity.class);
                intent.putExtra("isNew", false);
                intent.putExtra("unitId", character.characterId);
                intent.putExtra("boxId", character.boxId);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        characters.clear();
        characters.addAll(mDatabase.getCharacterList(boxId));
        ((CharacterAdapter)mListView.getAdapter()).notifyDataSetChanged();
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
            if(character != null) {
                vh.targetRank.setText(String.format(getString(R.string.rank), character.targetRank));
                vh.currentRank.setText(String.format(getString(R.string.rank), character.currentRank));
                StringBuilder currentEquipBuilder = new StringBuilder();
                for(int i=1; i<33; i = i << 1){
                    if((character.currentEquip & i) != 0){
                        currentEquipBuilder.append('■');
                    }else{
                        currentEquipBuilder.append('□');
                    }
                }
                vh.currentEquip.setText(currentEquipBuilder.toString());
                StringBuilder targetEquipBuilder = new StringBuilder();
                for(int i=1; i<33; i = i << 1){
                    if((character.targetEquip & i) != 0){
                        targetEquipBuilder.append('■');
                    }else{
                        targetEquipBuilder.append('□');
                    }
                }
                vh.targetEquip.setText(targetEquipBuilder.toString());
                ImageLoader.getInstance().displayImage(Constant.unitImageUrl(character.characterId, 1),
                        new ImageViewAware(vh.characterImage, false),
                        Constant.displayImageOption);
            }
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
