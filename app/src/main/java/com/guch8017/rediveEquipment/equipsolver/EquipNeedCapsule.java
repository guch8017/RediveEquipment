package com.guch8017.rediveEquipment.equipsolver;

import android.content.Context;

import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBEquipmentData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EquipNeedCapsule implements Comparable<EquipNeedCapsule>{
    private int equipmentId;
    private int requirement;
    private String equipmentName;

    public EquipNeedCapsule(int eid, int req, String eqn){
        equipmentId = eid;
        equipmentName = eqn;
        requirement = req;
    }

    public static ArrayList<EquipNeedCapsule> forHashMap(Context context, HashMap<Integer, Integer> needMap){
        ArrayList<EquipNeedCapsule> capsules = new ArrayList<>();
        DatabaseReflector database = new DatabaseReflector(context);
        for(Map.Entry<Integer, Integer> entry: needMap.entrySet()){
            DBEquipmentData eqData = ((List<DBEquipmentData>)database.reflectClass(DBEquipmentData.class.getName(), DBEquipmentData.tableName, "equipment_id = "+entry.getKey())).get(0);
            capsules.add(new EquipNeedCapsule(entry.getKey(), entry.getValue(), eqData.equipment_name));
        }
        return capsules;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public int getRequirement(){
        return requirement;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    @Override
    public int compareTo(EquipNeedCapsule o2){
        if(this.requirement == o2.requirement) return this.equipmentId - o2.equipmentId;
        else return this.requirement - o2.requirement;
    }
}
