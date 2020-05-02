package com.guch8017.rediveEquipment.equipsolver;

import android.content.Context;
import android.util.Log;

import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBCharacter;
import com.guch8017.rediveEquipment.database.module.DBUnitPromotion;
import com.guch8017.rediveEquipment.util.Constant;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipRequirementCalculator {
    private static final String TAG = "EquipRequirementCalculator";
    public static HashMap<Integer, Integer> getPieceRequirement(Context context, HashMap<Integer, Integer> equipRequirement) throws IOException, ClassNotFoundException {
        HashMap<Integer, Integer> pieceMap = new HashMap<>();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Constant.compliedComposeDataFilepath(context)));
        HashMap<Integer, HashMap<Integer, Integer>> composeData = (HashMap<Integer, HashMap<Integer, Integer>>)ois.readObject();
        for(Map.Entry<Integer, Integer> entry: equipRequirement.entrySet()){
            for(Map.Entry<Integer, Integer> eqEntry: composeData.get(entry.getKey()).entrySet()){
                if(pieceMap.containsKey(eqEntry.getKey())){
                    pieceMap.put(eqEntry.getKey(), pieceMap.get(eqEntry.getKey()) + eqEntry.getValue() * entry.getValue());
                }else{
                    pieceMap.put(eqEntry.getKey(), eqEntry.getValue() * entry.getValue());
                }
            }
        }
        return pieceMap;
    }

    public static HashMap<Integer, Integer> getEquipRequirement(Context context, int mBoxId){
        HashMap<Integer, Integer> equip = new HashMap<>();
        BoxDatabase mBoxDatabase = new BoxDatabase(context);
        DatabaseReflector mDatabase = new DatabaseReflector(context);
        List<DBCharacter> charList = mBoxDatabase.getCharacterList(mBoxId);
        for(DBCharacter character: charList){
            List<DBUnitPromotion> UnitPromotion = (List<DBUnitPromotion>) mDatabase.reflectClass(DBUnitPromotion.class.getName(), DBUnitPromotion.tableName,
                    "unit_id = " + character.characterId);
            if(UnitPromotion == null){
                Log.e(TAG, "getEquipRequirement: Error: Can't get unit promotion data" );
                return equip;
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
        return equip;
    }
}
