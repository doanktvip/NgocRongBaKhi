package nro.models.services_func.useitem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import nro.models.player.Player;
import nro.models.services.InventoryService;
import nro.models.services.PetService;
import nro.models.services.Service;

/**
 * Handler chuyĂªn trĂ¡ch trang bá»‹ 69 loáº¡i thĂº cÆ°ng Ä‘eo lÆ°ng (MiniPet),
 * thay tháº¿ 345 dĂ²ng switch-case láº·p láº¡i trong UseItem.java.
 */
public final class MiniPetHandler {

    private static final Map<Integer, int[]> MINI_PET_DATA;

    static {
        Map<Integer, int[]> map = new HashMap<>(96);
        map.put(892, new int[]{882, 883, 884});
        map.put(893, new int[]{885, 886, 887});
        map.put(908, new int[]{891, 892, 893});
        map.put(909, new int[]{894, 895, 896});
        map.put(910, new int[]{897, 898, 899});
        map.put(916, new int[]{925, 926, 927});
        map.put(917, new int[]{928, 929, 930});
        map.put(918, new int[]{931, 932, 933});
        map.put(919, new int[]{934, 935, 936});
        map.put(936, new int[]{718, 719, 720});
        map.put(942, new int[]{966, 967, 968});
        map.put(943, new int[]{969, 970, 971});
        map.put(944, new int[]{972, 973, 974});
        map.put(967, new int[]{1050, 1051, 1052});
        map.put(1008, new int[]{1074, 1075, 1076});
        map.put(1039, new int[]{1089, 1090, 1091});
        map.put(1040, new int[]{1092, 1093, 1094});
        map.put(1046, new int[]{-1, -1, -1});
        map.put(1107, new int[]{1155, 1156, 1157});
        map.put(1114, new int[]{1158, 1159, 1160});
        map.put(1188, new int[]{1183, 1184, 1185});
        map.put(1202, new int[]{1201, 1202, 1203});
        map.put(1203, new int[]{1201, 1202, 1203});
        map.put(1207, new int[]{1077, 1078, 1079});
        map.put(1224, new int[]{1227, 1228, 1229});
        map.put(1225, new int[]{1233, 1234, 1235});
        map.put(1226, new int[]{1230, 1231, 1232});
        map.put(1243, new int[]{1245, 1246, 1247});
        map.put(1244, new int[]{1248, 1249, 1250});
        map.put(1256, new int[]{1267, 1268, 1269});
        map.put(1318, new int[]{1299, 1300, 1301});
        map.put(1347, new int[]{1302, 1303, 1304});
        map.put(1414, new int[]{1341, 1342, 1343});
        map.put(1435, new int[]{1347, 1348, 1349});
        map.put(1452, new int[]{1365, 1366, 1367});
        map.put(1458, new int[]{1368, 1369, 1370});
        map.put(1482, new int[]{1398, 1399, 1400});
        map.put(1497, new int[]{1401, 1402, 1403});
        map.put(1550, new int[]{1428, 1429, 1430});
        map.put(1551, new int[]{1425, 1426, 1427});
        map.put(1564, new int[]{1437, 1438, 1439});
        map.put(1568, new int[]{1443, 1444, 1445});
        map.put(1573, new int[]{1446, 1447, 1448});
        map.put(1596, new int[]{1473, 1474, 1475});
        map.put(1597, new int[]{1473, 1474, 1475});
        map.put(1611, new int[]{1488, 1494, 1495});
        map.put(1620, new int[]{1496, 1497, 1498});
        map.put(1621, new int[]{1496, 1497, 1498});
        map.put(1622, new int[]{1488, 1489, 1490});
        map.put(1629, new int[]{1505, 1506, 1507});
        map.put(1630, new int[]{1508, 1509, 1510});
        map.put(1631, new int[]{1513, 1516, 1517});
        map.put(1633, new int[]{1523, 1524, 1525});
        map.put(1654, new int[]{1526, 1529, 1530});
        map.put(1668, new int[]{1550, 1551, 1552});
        map.put(1682, new int[]{1558, 1559, 1560});
        map.put(1683, new int[]{1561, 1562, 1563});
        map.put(1686, new int[]{1572, 1573, 1574});
        map.put(1750, new int[]{1464, 1465, 1466});
        map.put(1765, new int[]{1662, 1663, 1664});
        map.put(1789, new int[]{1724, 1725, 1726});
        map.put(1727, new int[]{1616, 1617, 1618});
        map.put(1729, new int[]{1621, 1622, 1623});
        map.put(1766, new int[]{1665, 1666, 1667});
        map.put(1767, new int[]{1668, 1669, 1670});
        map.put(1768, new int[]{1671, 1672, 1673});
        map.put(1769, new int[]{1674, 1675, 1676});
        map.put(1770, new int[]{1677, 1678, 1679});
        map.put(1771, new int[]{1680, 1681, 1682});
        MINI_PET_DATA = Collections.unmodifiableMap(map);
    }

    private MiniPetHandler() {
    }

    public static boolean tryEquipMiniPet(Player pl, int itemTemplateId, int indexBag) {
        int[] parts = MINI_PET_DATA.get(itemTemplateId);
        if (parts == null) {
            return false;
        }
        InventoryService.gI().itemBagToBody(pl, indexBag);
        PetService.Pet2(pl, parts[0], parts[1], parts[2]);
        Service.gI().point(pl);
        return true;
    }
}
