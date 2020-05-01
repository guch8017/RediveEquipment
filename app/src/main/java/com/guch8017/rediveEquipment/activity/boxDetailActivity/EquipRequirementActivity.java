package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBCharacter;
import com.guch8017.rediveEquipment.database.module.DBUnitPromotion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class EquipRequirementActivity extends AppCompatActivity {
    private int mBoxId;
    private ListView mListView;
    private HashMap<Integer, Integer> equip;
    private BoxDatabase mBoxDatabase;
    private DatabaseReflector mDatabase;
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        mBoxId = getIntent().getIntExtra("boxId", -1);
        setContentView(R.layout.activity_box_char_selector);
        mListView = findViewById(R.id.character_list);
        mBoxDatabase = new BoxDatabase(this);
        mDatabase = new DatabaseReflector(this);
        equip = new HashMap<>();

    }

    private void getEquipRequirement(){
        List<DBCharacter> charList = mBoxDatabase.getCharacterList(mBoxId);
        for(DBCharacter character: charList){
            List<DBUnitPromotion> UnitPromotion = (List<DBUnitPromotion>) mDatabase.reflectClass(DBUnitPromotion.class.getName(), DBUnitPromotion.tableName,
                    "unit_id = " + character.characterId);
            if(UnitPromotion == null){
                Toast.makeText(this, "Error: Can't get unit promotion data", Toast.LENGTH_LONG).show();
                return;
            }
            UnitPromotion.sort(new Comparator<DBUnitPromotion>() {
                @Override
                public int compare(DBUnitPromotion o1, DBUnitPromotion o2) {
                    if(o1.promotion_level == o2.promotion_level) return 0;
                    return (o1.promotion_level > o2.promotion_level) ? 1 : -1;
                }
            });
            for(int i = character.currentRank; i < character.targetRank - 1; ++i){
                DBUnitPromotion curPromotion = UnitPromotion.get(i - 1);
                if(equip.containsKey(curPromotion.equip_slot_1)) equip.put(curPromotion.equip_slot_1, 1 + equip.get(curPromotion.equip_slot_1));
                else equip.put(curPromotion.equip_slot_1, 1);
                if(equip.containsKey(curPromotion.equip_slot_2)) equip.put(curPromotion.equip_slot_2, 1 + equip.get(curPromotion.equip_slot_2));
                else equip.put(curPromotion.equip_slot_2, 1);
                if(equip.containsKey(curPromotion.equip_slot_3)) equip.put(curPromotion.equip_slot_3, 1 + equip.get(curPromotion.equip_slot_3));
                else equip.put(curPromotion.equip_slot_3, 1);
                if(equip.containsKey(curPromotion.equip_slot_4)) equip.put(curPromotion.equip_slot_4, 1 + equip.get(curPromotion.equip_slot_4));
                else equip.put(curPromotion.equip_slot_4, 1);
                if(equip.containsKey(curPromotion.equip_slot_5)) equip.put(curPromotion.equip_slot_5, 1 + equip.get(curPromotion.equip_slot_5));
                else equip.put(curPromotion.equip_slot_5, 1);
                if(equip.containsKey(curPromotion.equip_slot_6)) equip.put(curPromotion.equip_slot_6, 1 + equip.get(curPromotion.equip_slot_6));
                else equip.put(curPromotion.equip_slot_6, 1);
            }
        }
    }
}
