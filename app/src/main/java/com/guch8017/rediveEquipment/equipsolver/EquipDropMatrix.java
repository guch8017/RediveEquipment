package com.guch8017.rediveEquipment.equipsolver;

import android.content.Context;

import com.guch8017.rediveEquipment.util.Constant;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 表示装备掉落表, 意义见文档
 * 采用单例模式
 */
public class EquipDropMatrix {
    private static EquipDropMatrix instance;
    // 建立装备id到其所有掉落图的映射
    private HashMap<Long, ArrayList<EquipDropCapsule>> equipIdCapsMap;

    /**
     * 用于将文件反序列化到LongSparseArray中
     * @throws FileNotFoundException 反序列化失败
     */
    private EquipDropMatrix(Context context)throws FileNotFoundException {

        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Constant.compliedDropDataFilepath(context)));
            equipIdCapsMap = (HashMap<Long, ArrayList<EquipDropCapsule>>) ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
            throw new FileNotFoundException("无法反序列化文件: " + Constant.compliedDropDataFilepath(context));
        }
    }

    /**
     * 获取地图掉落矩阵
     * @return 地图掉落矩阵单例
     * @throws FileNotFoundException 反序列化文件未找到，即数据库不存在。数据库应在每次更新数据库后重新生成。
     */
    public static EquipDropMatrix getInstance(Context context) throws FileNotFoundException{
        if (instance == null){
            instance = new EquipDropMatrix(context);
        }
        return instance;
    }

    public EquipDropCapsule[] getMaps(long equipId){
        if(equipIdCapsMap.containsKey(equipId)){
            EquipDropCapsule[] capsules = new EquipDropCapsule[equipIdCapsMap.get(equipId).size()];
            equipIdCapsMap.get(equipId).toArray(capsules);
            return capsules;
        }else{
            return new EquipDropCapsule[]{};
        }
    }
}
