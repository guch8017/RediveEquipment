package com.guch8017.rediveEquipment.equipsolver;

/**
 * 用于包装: 地图编号(quest_id) 和 掉落装备的id(equipment_id) 和 基础掉落装备概率
 */
public class EquipDropCapsule {
    private long mapQuestId;
    private long equipmentId;
    private double dropProb;

    public EquipDropCapsule(){
        mapQuestId = -1;
        equipmentId = -1;
        dropProb = 0.0;
    }

    public EquipDropCapsule(long mapQuestId, long equipmentId, long dropProb){
        this.mapQuestId = mapQuestId;
        this.equipmentId = equipmentId;
        this.dropProb = dropProb;
    }

    public long getMapQuestId() {
        return mapQuestId;
    }

    public void setMapQuestId(long mapQuestId) {
        this.mapQuestId = mapQuestId;
    }

    public long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public double getDropProb() {
        return dropProb;
    }

    public void setDropProb(double dropProb) {
        this.dropProb = dropProb;
    }
}
