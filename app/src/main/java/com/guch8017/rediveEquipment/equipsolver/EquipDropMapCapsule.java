package com.guch8017.rediveEquipment.equipsolver;

/**
 * 用于包装: 求解结果中, 刷的地图编号 及 刷的次数
 */
public class EquipDropMapCapsule {
    // 地图的编号
    private long mapQuestId;
    // 地图需要刷的次数
    private double time;

    public EquipDropMapCapsule(long mapQuestId, double time){
        this.mapQuestId = mapQuestId;
        this.time = time;
    }

    public long getMapQuestId() {
        return mapQuestId;
    }

    public void setMapQuestId(long mapQuestId) {
        this.mapQuestId = mapQuestId;
    }

    public double getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
