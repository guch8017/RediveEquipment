package com.guch8017.rediveEquipment.equipsolver;

import android.content.Context;
import android.content.Intent;

import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBEquipmentCraft;
import com.guch8017.rediveEquipment.database.module.DBEquipmentData;
import com.guch8017.rediveEquipment.database.module.DBQuest;
import com.guch8017.rediveEquipment.database.module.DBWave;
import com.guch8017.rediveEquipment.database.module.GetQuests;
import com.guch8017.rediveEquipment.util.Constant;
import com.guch8017.rediveEquipment.util.IO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DatabaseGenerater {
    /**
     * 生成一个地图掉落数据库并序列化到文件
     * @param context 上下文
     */
    public static void generateDropDatabase(Context context) throws IOException {
        DatabaseReflector database = new DatabaseReflector(context);
        HashMap<Long, ArrayList<EquipDropCapsule>> map = new HashMap<>();
        List<DBQuest> quests = GetQuests.getQuests(database, -1);
        for(DBQuest quest: quests){
            // 跳过Hard关卡和心碎宝珠本
            if(quest.quest_id > 12000000) continue;
            for(DBWave.Reward reward: quest.rewards){
                // 这里写死了是否为掉落物的判断逻辑，反正以后也不会有新的低等装备加入了
                if(reward.rewardID > 110000 || reward.rewardID < 102282 || reward.rewardID == 102461 || (reward.rewardID >= 102551 && reward.rewardID <= 102612)){
                    if(map.containsKey((long)reward.rewardID)){
                        map.get((long)reward.rewardID).add(new EquipDropCapsule(quest.quest_id, reward.rewardID, reward.odds));
                    }else{
                        ArrayList<EquipDropCapsule> list = new ArrayList<>();
                        list.add(new EquipDropCapsule(quest.quest_id, reward.rewardID, reward.odds));
                        map.put((long)reward.rewardID, list);
                    }
                }
            }
        }
        if(IO.isFileExist(Constant.compliedDropDataFilepath(context))){
            IO.deleteFile(Constant.compliedDropDataFilepath(context));
        }
        FileOutputStream fos = new FileOutputStream(Constant.compliedDropDataFilepath(context));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
    }

    /**
     * 生成一个装备合成数据库并序列化到文件
     * @param context 上下文
     * @// TODO: 2020/5/1 删除装备碎片 
     */
    public static void generateComposeDatabase(Context context)throws IOException{
        DatabaseReflector database = new DatabaseReflector(context);
        HashMap<Integer, HashMap<Integer, Integer>> composeData = new HashMap<>();
        List<DBEquipmentData> equipmentDatas = (List<DBEquipmentData>)database.reflectClass(DBEquipmentData.class.getName(), DBEquipmentData.tableName);
        if(equipmentDatas != null){
            for(DBEquipmentData data: equipmentDatas){
                composeData.put(data.equipment_id, generateComposeDatabase(database, data));
            }
        }
        if(IO.isFileExist(Constant.compliedComposeDataFilepath(context))){
            IO.deleteFile(Constant.compliedComposeDataFilepath(context));
        }
        FileOutputStream fos = new FileOutputStream(Constant.compliedComposeDataFilepath(context));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(composeData);
    }

    private static HashMap<Integer, Integer> generateComposeDatabase(DatabaseReflector database, DBEquipmentData data){
        HashMap<Integer, Integer> composeMap = new HashMap<>();
        if(data.craft_flg == 0){
            composeMap.put(data.equipment_id, 1);
        }else{
            List<DBEquipmentCraft> crafts = (List<DBEquipmentCraft>)database.reflectClass(DBEquipmentCraft.class.getName(), DBEquipmentCraft.tableName, "equipment_id="+data.equipment_id);
            if(crafts != null && crafts.size() > 0){
                DBEquipmentCraft craft = crafts.get(0);
                if(craft.condition_equipment_id_1 != 0){
                    DBEquipmentData subData = ((List<DBEquipmentData>)database.reflectClass(DBEquipmentData.class.getName(), DBEquipmentData.tableName, "equipment_id="+craft.condition_equipment_id_1)).get(0);
                    HashMap<Integer, Integer> singleMap = generateComposeDatabase(database, subData);
                    Iterator iter = singleMap.entrySet().iterator();
                    while (iter.hasNext()){
                        Map.Entry entry = (Map.Entry)iter.next();
                        int eqId = (Integer) entry.getKey();
                        int eqCnt = (Integer) entry.getValue();
                        eqCnt *= craft.consume_num_1;
                        if(composeMap.containsKey(eqId)){
                            composeMap.put(eqId, composeMap.get(eqId) + eqCnt);
                        }else{
                            composeMap.put(eqId, eqCnt);
                        }
                    }
                }
                if(craft.condition_equipment_id_2 != 0){
                    DBEquipmentData subData = ((List<DBEquipmentData>)database.reflectClass(DBEquipmentData.class.getName(), DBEquipmentData.tableName, "equipment_id="+craft.condition_equipment_id_2)).get(0);
                    HashMap<Integer, Integer> singleMap = generateComposeDatabase(database, subData);
                    Iterator iter = singleMap.entrySet().iterator();
                    while (iter.hasNext()){
                        Map.Entry entry = (Map.Entry)iter.next();
                        int eqId = (Integer) entry.getKey();
                        int eqCnt = (Integer) entry.getValue();
                        eqCnt *= craft.consume_num_2;
                        if(composeMap.containsKey(eqId)){
                            composeMap.put(eqId, composeMap.get(eqId) + eqCnt);
                        }else{
                            composeMap.put(eqId, eqCnt);
                        }
                    }
                }
                if(craft.condition_equipment_id_3 != 0){
                    DBEquipmentData subData = ((List<DBEquipmentData>)database.reflectClass(DBEquipmentData.class.getName(), DBEquipmentData.tableName, "equipment_id="+craft.condition_equipment_id_3)).get(0);
                    HashMap<Integer, Integer> singleMap = generateComposeDatabase(database, subData);
                    Iterator iter = singleMap.entrySet().iterator();
                    while (iter.hasNext()){
                        Map.Entry entry = (Map.Entry)iter.next();
                        int eqId = (Integer) entry.getKey();
                        int eqCnt = (Integer) entry.getValue();
                        eqCnt *= craft.consume_num_3;
                        if(composeMap.containsKey(eqId)){
                            composeMap.put(eqId, composeMap.get(eqId) + eqCnt);
                        }else{
                            composeMap.put(eqId, eqCnt);
                        }
                    }
                }
            }
        }
        return composeMap;
    }
}
