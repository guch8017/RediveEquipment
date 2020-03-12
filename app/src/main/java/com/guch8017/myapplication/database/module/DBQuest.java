package com.guch8017.myapplication.database.module;

import java.util.List;

public class DBQuest {
    public List<DBWave> waves;
    public int quest_id;
    public int area_id;
    public int wave_group_id_1;
    public int wave_group_id_2;
    public int wave_group_id_3;

    public DBQuest(List<DBWave> waves, DBQuestData data){
        this.waves = waves;
        quest_id = data.quest_id;
        area_id = data.area_id;
        wave_group_id_1 = data.wave_group_id_1;
        wave_group_id_2 = data.wave_group_id_2;
        wave_group_id_3 = data.wave_group_id_3;
    }

    public boolean containsEquip(int equipmentId){
        if(waves == null){
            return false;
        }
        for(DBWave wave : waves){
            for(DBWave.Reward reward : wave.rewards){
                if(reward.rewardID == equipmentId){
                    return true;
                }
            }
        }
        return false;
    }

    public AreaType getAreaType(){
        if(area_id >= 11000 && area_id < 12000){
            return AreaType.normal;
        }
        else if(area_id >= 12000 && area_id < 13000){
            return AreaType.hard;
        }
        else if(area_id >= 13000 && area_id < 14000){
            return AreaType.veryhard;
        }
        else if(area_id >= 18000 && area_id < 19000){
            return AreaType.shrine;
        }
        else if(area_id >= 19000 && area_id < 20000){
            return AreaType.temple;
        }
        else if(area_id >= 21000 && area_id < 22000){
            return AreaType.exploration;
        }else{
            return AreaType.unknown;
        }
    }

    enum AreaType{
        normal,
        hard,
        veryhard,
        shrine,
        temple,
        exploration,
        unknown
    }
}
