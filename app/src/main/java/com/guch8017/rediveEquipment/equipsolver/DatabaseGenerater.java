package com.guch8017.rediveEquipment.equipsolver;

import android.content.Context;
import android.util.Log;

import com.guch8017.rediveEquipment.database.DatabaseReflector;
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
import java.util.List;

public class DatabaseGenerater {
    /**
     * 生成一个地图掉落数据库并序列化到文件
     * @param context 上下文
     */
    public static void generateDatabase(Context context) throws IOException {
        DatabaseReflector database = new DatabaseReflector(context);
        HashMap<Long, ArrayList<EquipDropCapsule>> map = new HashMap<>();
        List<DBQuest> quests = GetQuests.getQuests(database, -1);
        for(DBQuest quest: quests){
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
        if(IO.isFileExist(Constant.compliedDataFilepath(context))){
            IO.deleteFile(Constant.compliedDataFilepath(context));
        }
        FileOutputStream fos = new FileOutputStream(Constant.compliedDataFilepath(context));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
    }
}
