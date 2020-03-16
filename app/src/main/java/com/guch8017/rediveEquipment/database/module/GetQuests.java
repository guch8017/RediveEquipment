package com.guch8017.rediveEquipment.database.module;

import com.guch8017.rediveEquipment.database.DatabaseReflector;

import java.util.ArrayList;
import java.util.List;

public class GetQuests {
    public static List<DBQuest> getQuests(DatabaseReflector reflector, int equipmentId){
        List<DBQuest> quests = new ArrayList<>();
        List<DBQuestData> questDatas = (List<DBQuestData>) reflector.reflectClass(DBQuestData.class.getName(), DBQuestData.tableName);
        if(questDatas == null){
            return quests;
        }
        for (DBQuestData questData : questDatas){
            ArrayList<Integer> waveList = new ArrayList<>();
            if(questData.wave_group_id_1 != 0){
                waveList.add(questData.wave_group_id_1);
            }
            if(questData.wave_group_id_2 != 0){
                waveList.add(questData.wave_group_id_2);
            }
            if(questData.wave_group_id_3 != 0){
                waveList.add(questData.wave_group_id_3);
            }
            List<DBWave> waves = getWaves(reflector, waveList);
            DBQuest quest = new DBQuest(waves, questData);
            if(equipmentId == -1 || quest.containsEquip(equipmentId)) {
                quests.add(quest);
            }
        }
        return quests;
    }

    public static List<DBWave> getWaves(DatabaseReflector reflector, List<Integer> waveIds){
        ArrayList<DBWave> waves = new ArrayList<>();
        if(waveIds.size() == 0){
            return waves;
        }
        StringBuilder waveStringBuilder = new StringBuilder();
        for(int wave :waveIds){
            waveStringBuilder.append(wave);
            waveStringBuilder.append(',');
        }
        waveStringBuilder.deleteCharAt(waveStringBuilder.length() - 1);
        List<DBWaveGroupData> waveDatas = (List<DBWaveGroupData>) reflector.reflectClass(DBWaveGroupData.class.getName(),
                DBWaveGroupData.dbName, String.format("wave_group_id IN (%s)", waveStringBuilder.toString()));
        if(waveDatas == null){
            return waves;
        }

        for(DBWaveGroupData waveGroupData: waveDatas){
            StringBuilder rewardStringBuilder = new StringBuilder();
            if(waveGroupData.drop_reward_id_1 != 0){
                rewardStringBuilder.append(waveGroupData.drop_reward_id_1);
                rewardStringBuilder.append(',');
            }
            if(waveGroupData.drop_reward_id_2 != 0){
                rewardStringBuilder.append(waveGroupData.drop_reward_id_2);
                rewardStringBuilder.append(',');
            }
            if(waveGroupData.drop_reward_id_3 != 0){
                rewardStringBuilder.append(waveGroupData.drop_reward_id_3);
                rewardStringBuilder.append(',');
            }
            if(waveGroupData.drop_reward_id_4 != 0){
                rewardStringBuilder.append(waveGroupData.drop_reward_id_4);
                rewardStringBuilder.append(',');
            }
            if(waveGroupData.drop_reward_id_5 != 0){
                rewardStringBuilder.append(waveGroupData.drop_reward_id_5);
                rewardStringBuilder.append(',');
            }
            if(rewardStringBuilder.length() > 0){
                rewardStringBuilder.deleteCharAt(rewardStringBuilder.length() - 1);
            }
            List<DBEnemyRewardData> rewardDatas = (List<DBEnemyRewardData>) reflector.reflectClass(
                    DBEnemyRewardData.class.getName(), DBEnemyRewardData.dbName,
                    String.format("drop_reward_id IN (%s)", rewardStringBuilder.toString()));
            DBWave wave = new DBWave(waveGroupData, rewardDatas);
            waves.add(wave);
        }
        return waves;
    }
}
