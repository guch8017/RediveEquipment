package com.guch8017.myapplication.database.module;

import java.util.ArrayList;
import java.util.List;

public class DBWave {
    public DBWaveGroupData base;
    public List<Reward> rewards;


    public DBWave(DBWaveGroupData waveGroupData, List<DBEnemyRewardData> rewardsData){
        base = waveGroupData;
        rewards = new ArrayList<>();
        if(rewardsData != null){
            for(DBEnemyRewardData enemyRewardData : rewardsData){
                if(enemyRewardData.reward_id_1 != 0){
                    Reward reward = new Reward(enemyRewardData.odds_1, enemyRewardData.reward_id_1,
                            enemyRewardData.reward_type_1, enemyRewardData.reward_num_1);
                    rewards.add(reward);
                }
                if(enemyRewardData.reward_id_2 != 0){
                    Reward reward = new Reward(enemyRewardData.odds_2, enemyRewardData.reward_id_2,
                            enemyRewardData.reward_type_2, enemyRewardData.reward_num_2);
                    rewards.add(reward);
                }
                if(enemyRewardData.reward_id_3 != 0){
                    Reward reward = new Reward(enemyRewardData.odds_3, enemyRewardData.reward_id_3,
                            enemyRewardData.reward_type_3, enemyRewardData.reward_num_3);
                    rewards.add(reward);
                }
                if(enemyRewardData.reward_id_4 != 0){
                    Reward reward = new Reward(enemyRewardData.odds_4, enemyRewardData.reward_id_4,
                            enemyRewardData.reward_type_4, enemyRewardData.reward_num_4);
                    rewards.add(reward);
                }
                if(enemyRewardData.reward_id_5 != 0){
                    Reward reward = new Reward(enemyRewardData.odds_5, enemyRewardData.reward_id_5,
                            enemyRewardData.reward_type_5, enemyRewardData.reward_num_5);
                    rewards.add(reward);
                }
            }
        }
    }

    class Reward{
        public int odds;
        public int rewardID;
        public int rewardType;
        public int rewardNum;
        Reward(int odds, int rewardID, int rewardType, int rewardNum){
            this.odds = odds;
            this.rewardID = rewardID;
            this.rewardType = rewardType;
            this.rewardNum = rewardNum;
        }
    }
}
