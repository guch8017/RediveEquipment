# EquipSolver使用说明

### 基本数据类型说明

#### 1.  EquipDropCapsule类

```java
public class EquipDropCapsule {    
    private long mapQuestId;    
    private long equipmentId;    
    private double dropProb;
    ...
}
```

EquipDropCapsule类用于存储每个地图中，对于某件特定装备的掉率。

例如，若equip_id为101011的装备，于map_id为11001001的地图上，掉率为36%，则可按照以下方式构造：

```java
EquipDropCapsule e = new EquipDropCapsule(11001001, 101011, 0.36);
```



#### 2. EquipDropMatrix类

```java
public class EquipDropMatrix {
    // 单例模式
    private static EquipDropMatrix instance;
    // 建立装备id到其所有掉落图的映射
    private LongSparseArray<EquipDropCapsule[]> equipIdCapsMap; 
    ...
}
```

EquipDropMatrix类用于存储装备掉落与地图之间映射的关系。

考虑到装备掉落表为长期不变量，故选择单例模式。

此类中唯一的关键信息为LongSparseArray<EquipDropCapsule[]>类型的equipIdCapsMap成员变量。用于建立一件装备到其所有掉落地图的信息。

**==TODO==：对每一件装备，以装备的id作为key，其所有掉落地图的信息作为value。只需要将其关联的所有掉落地图数据，封装为上面的EquipDropCapsule类型，并传入此成员变量，即可完成初始化构造。交给你了，qiao，，，**

#### 3. EquipDropMapCapsule类

```java
public class EquipDropMapCapsule {
    // 地图的编号
    private long mapQuestId;
    // 地图需要刷的次数
    private double time;
    ...
}
```

EquipDropMapCapsule类用于表示结果，方法可见源码。

#### 4. EquipDropNeedList类

```java
public class EquipDropNeedList {
    public void set(long equipId, int num);
    public int get(long equipId);
    public void del(long equipId);
    public boolean contains(long equipId);
}
```

EquipDropNeedList类表示用户的需求，详情见源码中的注释说明。

### API说明

```java
public class EquipSolver {
    public static ArrayList<EquipDropMapCapsule> SingleSolve(EquipDropMatrix dropMatrix,
                                                             EquipDropNeedList needList);
}
```

#### 1. SingleSolve(...)

参数说明:

* dropMatrix: 装备掉落表，由EquipDropMatrix中的静态方法生成，采用单例模式。
* needList: 装备需求表，由用户自定义并输入。

返回值：

* 返回一个含有结果的List，建议详见EquipDropMapCapsule的代码实现。