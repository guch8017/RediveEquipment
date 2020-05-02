package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.activity.equipmentSearchActivity.EquipmentSearchActivity;
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBCharacter;
import com.guch8017.rediveEquipment.database.module.DBUnitPromotion;
import com.guch8017.rediveEquipment.equipsolver.EquipNeedCapsule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class EquipRequirementActivity extends AppCompatActivity {
    private int mBoxId;
    private ListView mListView;
    private HashMap<Integer, Integer> equip;
    private List<EquipNeedCapsule> mNeedList;
    private BoxDatabase mBoxDatabase;
    private DatabaseReflector mDatabase;
    private FinishLoadingReceiver mReceiver;
    private ProgressDialog mProgressDialog;
    private static final String LOADING_INTENT = "EQUIP_CALC_DONE";
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("正在计算");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mBoxId = getIntent().getIntExtra("boxId", -1);
        setContentView(R.layout.activity_box_char_selector);
        mListView = findViewById(R.id.character_list);
        mBoxDatabase = new BoxDatabase(this);
        mDatabase = new DatabaseReflector(this);
        equip = new HashMap<>();
        mListView = findViewById(R.id.character_list);
        mNeedList = new ArrayList<>();
        mListView.setAdapter(new EquipmentRequirementAdapter(this, R.layout.equipment_detail_item_1, mNeedList));

        IntentFilter filter = new IntentFilter();
        filter.addAction(LOADING_INTENT);
        mReceiver = new FinishLoadingReceiver();
        this.registerReceiver(mReceiver, filter);

        new Thread(){
            @Override
            public void run() {
                getEquipRequirement();
                ArrayList<EquipNeedCapsule> list = EquipNeedCapsule.forHashMap(EquipRequirementActivity.this, equip);
                mNeedList.clear();
                mNeedList.addAll(list);
                mNeedList.sort(new Comparator<EquipNeedCapsule>() {
                    @Override
                    public int compare(EquipNeedCapsule o1, EquipNeedCapsule o2) {
                        return - o1.compareTo(o2);
                    }
                });
                Intent intent = new Intent(LOADING_INTENT);
                EquipRequirementActivity.this.sendBroadcast(intent);
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
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
            ((EquipmentRequirementAdapter)mListView.getAdapter()).notifyDataSetChanged();
        }
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
            for(int i = character.currentRank; i < character.targetRank; ++i){
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
            if(character.currentRank == character.targetRank){
                if((character.targetEquip & 0b000001) != 0 && (character.currentEquip & 0b000001) == 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_1;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b000010) != 0 && (character.currentEquip & 0b000010) == 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_2;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b000100) != 0 && (character.currentEquip & 0b000100) == 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_3;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b001000) != 0 && (character.currentEquip & 0b001000) == 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_4;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b010000) != 0 && (character.currentEquip & 0b010000) == 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_5;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b100000) != 0 && (character.currentEquip & 0b100000) == 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_6;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
            }else{
                if((character.targetEquip & 0b000001) != 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_1;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b000010) != 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_2;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b000100) != 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_3;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b001000) != 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_4;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b010000) != 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_5;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.targetEquip & 0b100000) != 0){
                    int equipId = UnitPromotion.get(character.targetRank - 1).equip_slot_6;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                // 当前装备
                if((character.currentEquip & 0b000001) == 0){
                    int equipId = UnitPromotion.get(character.currentRank - 1).equip_slot_1;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.currentEquip & 0b000010) == 0){
                    int equipId = UnitPromotion.get(character.currentRank - 1).equip_slot_2;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.currentEquip & 0b000100) == 0){
                    int equipId = UnitPromotion.get(character.currentRank - 1).equip_slot_3;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.currentEquip & 0b001000) == 0){
                    int equipId = UnitPromotion.get(character.currentRank - 1).equip_slot_4;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.currentEquip & 0b010000) == 0){
                    int equipId = UnitPromotion.get(character.currentRank - 1).equip_slot_5;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
                if((character.currentEquip & 0b100000) == 0){
                    int equipId = UnitPromotion.get(character.currentRank - 1).equip_slot_6;
                    if(equipId != 999999){
                        if(equip.containsKey(equipId)){
                            equip.put(equipId, equip.get(equipId) + 1);
                        }else {
                            equip.put(equipId, 1);
                        }
                    }
                }
            }
        }
    }
}
