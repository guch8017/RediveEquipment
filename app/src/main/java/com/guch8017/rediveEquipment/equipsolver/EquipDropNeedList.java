package com.guch8017.rediveEquipment.equipsolver;

import android.util.Log;
import android.util.LongSparseArray;

import java.util.List;

/**
 * 装备需求表, 由用户创建并填写后, 传入求解器
 * TODO: 最好建立输入正确性检验, 避免输入奇怪的装备id
 */
public class EquipDropNeedList {
    private static final String TAG = "EquipDropNeedList";
    // 建立装备与其所需数量的映射
    private LongSparseArray<Integer> idNumMap;

    public EquipDropNeedList(){
        idNumMap = new LongSparseArray<>();
    }

    /**
     * 由需求列表建立模型
     * @param list 需求列表
     * @return 实例
     */
    public static EquipDropNeedList forCapsule(List<EquipNeedCapsule> list){
        EquipDropNeedList ins = new EquipDropNeedList();
        for(EquipNeedCapsule capsule: list){
            Log.d(TAG, "forCapsule: EquipmentId: " + capsule.getEquipmentId() + " Requirement: " + capsule.getRequirement());
            ins.set((long)capsule.getEquipmentId(), capsule.getRequirement());
        }
        return ins;
    }

    public LongSparseArray<Integer> getIdNumMap(){
        return this.idNumMap;
    }

    /**
     * 增加或修改需求表
     * @param equipId 装备的id
     * @param num 装备需要的个数
     */
    public void set(long equipId, int num){
        this.idNumMap.put(equipId, num);
    }

    /**
     * 获取需求表中的某个需求
     * @param equipId 装备的id
     * @return 装备需要的个数
     */
    public int get(long equipId){
        Integer ans = idNumMap.get(equipId);
        return (ans == null)? -1 : ans;
    }

    /**
     * 删除某个需求
     * @param equipId 装备的id
     */
    public void del(long equipId){
        idNumMap.remove(equipId);
    }

    /**
     * 查询是否存在某个需求
     * @param equipId 装备的id
     * @return 存在则为true, 反之同理.
     */
    public boolean contains(long equipId){
        return idNumMap.indexOfKey(equipId) == -1;
    }
}
