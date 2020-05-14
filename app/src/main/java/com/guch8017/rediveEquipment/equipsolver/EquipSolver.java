package com.guch8017.rediveEquipment.equipsolver;

import android.util.ArrayMap;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseLongArray;

import androidx.annotation.Nullable;

//import org.apache.commons.math3.optim.PointValuePair;
//import org.apache.commons.math3.optim.linear.LinearConstraint;
//import org.apache.commons.math3.optim.linear.LinearConstraintSet;
//import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
//import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
//import org.apache.commons.math3.optim.linear.Relationship;
//import org.apache.commons.math3.optim.linear.SimplexSolver;
//import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import lpsolve.LpSolve;

/**
 * 装备求解器
 */
@SuppressWarnings("Convert2Diamond")
public class EquipSolver {
    private static final String TAG = "EquipSolver";
    /**
     * 解决基础掉率下的刷地图次数的线性规划
     * @param dropMatrix 装备掉落表
     * @param needList 装备需求表
     * @return 所需刷的图及次数, 按次数降序排列. 若过程有误, 返回null
     */
    @Nullable
    public static ArrayList<EquipDropMapCapsule> SingleSolve(EquipDropMatrix dropMatrix,
                                                             EquipDropNeedList needList){

        ArrayList<EquipDropMapCapsule> ans = new ArrayList<EquipDropMapCapsule>();
        // 首先, 建立 mapId到xIndex的映射 以及 xIndex到mapId的映射
        // xIndex即为方程中x的下标, 从0开始
        // mapCount为已经建立映射的地图的数量
        int mapCount = 0;
        LongSparseArray<Integer> mapId2xIndex = new LongSparseArray<>();
        SparseLongArray xIndex2mapId = new SparseLongArray();
        LongSparseArray<Integer> needListIdNumMap = needList.getIdNumMap();
        for (int i = 0;i < needListIdNumMap.size(); ++i){
            long nowEquipId = needListIdNumMap.keyAt(i);
            // 遍历这件装备所出现的所有地图
            EquipDropCapsule[] maps = dropMatrix.getMaps(nowEquipId);
            if (maps == null) continue;
            for (EquipDropCapsule map : maps) {
                long nowMapId = map.getMapQuestId();
                //TODO : TEST
                if (mapId2xIndex.indexOfKey(nowMapId) < 0) {
                    // 地图未建立映射
                    xIndex2mapId.put(mapCount, nowMapId);
                    mapId2xIndex.put(nowMapId, mapCount);
                    ++mapCount;
                }
            }
        }

        try {
            // 建立线性规划目标函数实体
            LpSolve lp;
            int retN = 0;

            lp = LpSolve.makeLp(0, mapCount);
            if (lp.getLp() == 0)
                retN = 1; /* couldn't construct a new model... */

            if (retN == 0){
                int[] colno = new int[mapCount];
                for (int i = 1; i <= mapCount;++i){
                    colno[i - 1] = i;
                }
                // row变量存储每一行的系数
                double[] row = new double[mapCount];
                lp.setAddRowmode(true);  /* makes building the model faster if it is done rows by row */
                // 对每一件装备,建立一行新的约束
                for (int i = 0;i < needListIdNumMap.size();++i){
                    Arrays.fill(row, 0);
                    long nowEquipId = needListIdNumMap.keyAt(i);
                    // 遍历这件装备所出现的所有地图
                    EquipDropCapsule[] maps = dropMatrix.getMaps(nowEquipId);
                    if (maps == null) continue;
                    for (int j = 0;j < maps.length;++j) {
                        EquipDropCapsule map = maps[j];
                        long nowMapId = map.getMapQuestId();
                        if (mapId2xIndex.indexOfKey(nowMapId) >= 0) {

                            int xIndex = mapId2xIndex.get(nowMapId);
                            row[xIndex] = map.getDropProb();
                        }
                    }
                    lp.addConstraintex(mapCount, row, colno, LpSolve.GE, needListIdNumMap.valueAt(i));
                }
            }

            if (retN == 0){
                lp.setAddRowmode(false); /* rowmode should be turned off again when done building the model */

                int[] colno = new int[mapCount];
                // row变量存储每一行的系数
                double[] row = new double[mapCount];

                for (int i = 1;i <= mapCount;++i){
                    colno[i - 1] = i;
                }
                Arrays.fill(row,1);

                /* set the objective in lp_solve */
                lp.setObjFnex(mapCount, row, colno);
                for (int i = 0;i < mapCount;++i){
                    lp.setLowbo(i + 1, 0);
                }
                lp.setEpslevel(0);  // EPS_TIGHT (0)
            }

            if (retN == 0){
                /* set the object direction to minimize */
                lp.setMinim();
                /* Now let lp_solve calculate a solution */
                retN = lp.solve();
                if(retN == LpSolve.OPTIMAL || retN == LpSolve.INFEASIBLE)
                    retN = 0;
                else
                    retN = 5;
            }

            if (retN == 0){
                // result变量存储结果
                double[] result = new double[mapCount];
                /* variable values */
                lp.getVariables(result);
                for (int i = 0;i < mapCount;++i){
                    if (result[i] != 0){
                        if(result[i] == 0) continue; // 跳过为0的项
                        EquipDropMapCapsule temp = new EquipDropMapCapsule(xIndex2mapId.get(i), result[i]);
                        ans.add(temp);
                    }
                }
            }

            /* clean up such that all used memory by lp_solve is freeed */
            if(lp.getLp() != 0)
                lp.deleteLp();

        } catch (Exception e){
            e.printStackTrace();
        }

        ans.sort(new EquipDropMapCapsuleComp());
        return ans;
    }

    private static class EquipDropMapCapsuleComp implements Comparator<EquipDropMapCapsule>{
        @Override
        public int compare(EquipDropMapCapsule t1, EquipDropMapCapsule t2) {
            if (t1.getTime() == t2.getTime()) return 0;
            return (t1.getTime() < t2.getTime())? 1 : -1;
        }
    }
}
