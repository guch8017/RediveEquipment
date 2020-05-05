package com.guch8017.rediveEquipment.equipsolver;

import android.util.ArrayMap;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseLongArray;

import androidx.annotation.Nullable;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

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
                if (mapId2xIndex.indexOfKey(nowMapId) < 0) {
                    // 地图未建立映射
                    xIndex2mapId.put(mapCount, nowMapId);
                    mapId2xIndex.put(nowMapId, mapCount);
                    ++mapCount;
                }
            }
        }

        // 建立线性规划目标函数
        double[] linearFuncTarget = new double[mapCount];
        for (int i = 0;i < linearFuncTarget.length;++i)
            linearFuncTarget[i] = 1.0;
        LinearObjectiveFunction linearFunc = new LinearObjectiveFunction(linearFuncTarget, 0);


        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        // 建立基本的大于0的平凡约束
/* 自带非负约束，故删除此项
        for (int i = 0;i < mapCount;++i){
            double[] cons = new double[mapCount];
            cons[i] = 1.0;
            constraints.add(new LinearConstraint(cons, Relationship.GEQ, 0));
        }
*/
        // 根据needList, 建立线性规划的约束并求解
        for (int i = 0;i < needListIdNumMap.size(); ++i){
            long nowEquipId = needListIdNumMap.keyAt(i);
            // 遍历这件装备所出现的所有地图
            if(nowEquipId == 115616){
                Log.i(TAG, "SingleSolve: DBG PAUSE");
            }
            EquipDropCapsule[] maps = dropMatrix.getMaps(nowEquipId);
            if (maps == null){
                Log.w(TAG, "SingleSolve: Warning: map for " + nowEquipId + " is null");
                continue;
            }
            // 建立约束方程组
            double[] cons = new double[mapCount];
            for (EquipDropCapsule map : maps) {
                long nowMapId = map.getMapQuestId();
                if (mapId2xIndex.indexOfKey(nowMapId) >= 0) {

                    int xIndex = mapId2xIndex.get(nowMapId);
                    cons[xIndex] = map.getDropProb();
                }else{
                    Log.w(TAG, "SingleSolve: Warning: can't get index for mapId: " + nowMapId);
                }
            }
            constraints.add(new LinearConstraint(cons, Relationship.GEQ, needListIdNumMap.valueAt(i)));
        }

        long stTime = System.currentTimeMillis();
        // 求解
        PointValuePair solution;
        // 改为自带非负约束
        NonNegativeConstraint negativeConstraint = new NonNegativeConstraint(true);
        solution = (new SimplexSolver(0.1)).optimize(linearFunc, new LinearConstraintSet(constraints), GoalType.MINIMIZE, negativeConstraint);
        if (solution == null)
            return null;
        double[] solAns = solution.getPoint();
        long edTime = System.currentTimeMillis();
        Log.i(TAG, "SingleSolve: Solve Finish. Time cost " + (edTime-stTime) + "ms");
        // 包装结果
        for (int i = 0;i < solAns.length;++i){
            if (solAns[i] != 0){
                if(solAns[i] == 0) continue; // 跳过为0的项
                EquipDropMapCapsule temp = new EquipDropMapCapsule(xIndex2mapId.get(i), solAns[i]);
                ans.add(temp);
            }
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
