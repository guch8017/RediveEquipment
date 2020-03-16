package com.guch8017.rediveEquipment.equipsolver;

import android.util.LongSparseArray;

/**
 * 表示装备掉落表, 意义见文档
 * 采用单例模式
 */
public class EquipDropMatrix {
    private static EquipDropMatrix instance;
    // 建立装备id到其所有掉落图的映射
    private LongSparseArray<EquipDropCapsule[]> equipIdCapsMap;

    private EquipDropMatrix(){
        // TODO: qiao加油, 这里请你按文档实现
    }

    public static EquipDropMatrix getInstance() {
        if (instance == null){
            instance = new EquipDropMatrix();
        }
        return instance;
    }

    public EquipDropCapsule[] getMaps(long equipId){
        return equipIdCapsMap.get(equipId);
    }
}
