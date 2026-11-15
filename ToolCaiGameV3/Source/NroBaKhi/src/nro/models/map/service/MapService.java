package nro.models.map.service;

import nro.models.consts.ConstMap;
import nro.models.map.Map;
import nro.models.map.WayPoint;
import nro.models.map.Zone;
import nro.models.mob.Mob;
import nro.models.player.Player;
import nro.models.server.Manager;
import nro.models.network.Message;
import nro.models.services.Service;
import nro.models.utils.Logger;
import nro.models.utils.Util;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class MapService {

    private MapService() {
    }

    private static final class Holder {
        private static final MapService INSTANCE = new MapService();
    }

    public static MapService gI() {
        return Holder.INSTANCE;
    }

    public WayPoint getWaypointPlayerIn(Player player) {
        for (WayPoint wp : player.zone.map.wayPoints) {
            if (player.location.x >= wp.minX && player.location.x <= wp.maxX && player.location.y >= wp.minY && player.location.y <= wp.maxY) {
                return wp;
            }
        }
        return null;
    }

    /**
     * @param tileTypeFocus tile type: top, bot, left, right...
     * @return [tileMapId][tileType]
     */
    public int[][] readTileIndexTileType(int tileTypeFocus) {
        int[][] tileIndexTileType = null;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_set_info"));
            int numTileMap = dis.readByte();
            tileIndexTileType = new int[numTileMap][];
            for (int i = 0; i < numTileMap; i++) {
                int numTileOfMap = dis.readByte();
                for (int j = 0; j < numTileOfMap; j++) {
                    int tileType = dis.readInt();
                    int numIndex = dis.readByte();
                    if (tileType == tileTypeFocus) {
                        tileIndexTileType[i] = new int[numIndex];
                    }
                    for (int k = 0; k < numIndex; k++) {
                        int typeIndex = dis.readByte();
                        if (tileType == tileTypeFocus) {
                            tileIndexTileType[i][k] = typeIndex;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
        return tileIndexTileType;
    }

    //tilemap for paint
    public int[][] readTileMap(int mapId) {
        int[][] tileMap = null;
        java.io.File file = new java.io.File("data/map/tile_map_data/" + mapId);
        if (!file.exists()) {
            return null;
        }
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            int w = dis.readUnsignedByte();
            int h = dis.readUnsignedByte();
            tileMap = new int[h][w];
            for (int i = 0; i < tileMap.length; i++) {
                for (int j = 0; j < tileMap[i].length; j++) {
                    tileMap[i][j] = dis.readUnsignedByte();
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e, "Lỗi đọc tile_map_data: " + mapId);
        }
        return tileMap;
    }

    public Zone getMapCanJoin(Player player, int mapId, int zoneId) {
        if (isMapOffline(mapId)) {
            return getMapById(mapId).zones.get(0);
        }

        if (this.isMapDoanhTrai(mapId) && (player.zone == null || player.clan == null || player.clan.doanhTrai == null)) {
            Zone zone = getZone(21 + player.gender);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }

        if (this.isMapBanDoKhoBau(mapId)) {
            if (player.clan == null || player.clan.BanDoKhoBau == null || player.zone == null) {
                // Đưa về zone mặc định an toàn
                Zone zone = getZone(5);
                player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
                player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
                return zone;
            }
        }

        if (this.isMapKhiGasHuyDiet(mapId) && (player.zone == null || player.clan == null || player.clan.KhiGasHuyDiet == null)) {
            Zone zone = getZone(5);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }

        if (this.isMapConDuongRanDoc(mapId) && (player.zone == null || player.clan == null || player.clan.ConDuongRanDoc == null)) {
            Zone zone = getZone(48);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }

        if (this.isMapDoanhTrai(mapId) && player.clan != null && player.clan.doanhTrai != null) {
            return resolveDungeonTransition(player, player.clan.doanhTrai.getMapById(mapId), this.isMapDoanhTrai(player.zone.map.mapId));
        }

        if (this.isMapBanDoKhoBau(mapId) && player.clan != null && player.clan.BanDoKhoBau != null) {
            return resolveDungeonTransition(player, player.clan.BanDoKhoBau.getMapById(mapId), this.isMapBanDoKhoBau(player.zone.map.mapId));
        }

        if (this.isMapKhiGasHuyDiet(mapId) && player.clan != null && player.clan.KhiGasHuyDiet != null) {
            return resolveDungeonTransition(player, player.clan.KhiGasHuyDiet.getMapById(mapId), this.isMapKhiGasHuyDiet(player.zone.map.mapId));
        }

        if (this.isMapConDuongRanDoc(mapId) && player.clan != null && player.clan.ConDuongRanDoc != null) {
            return resolveDungeonTransition(player, player.clan.ConDuongRanDoc.getMapById(mapId), this.isMapConDuongRanDoc(player.zone.map.mapId));
        }

        //**********************************************************************
        if (zoneId == -1) { //vào khu bất kỳ
            return getZone(mapId);
        } else {
            return getZoneByMapIDAndZoneID(mapId, zoneId);
        }
    }

    private boolean isZoneCleared(Zone zone) {
        if (zone == null) {
            return false;
        }
        if (zone.mobs != null) {
            for (Mob mob : zone.mobs) {
                if (mob != null && !mob.isDie()) {
                    return false;
                }
            }
        }
        List<Player> bosses = zone.getBosses();
        if (bosses != null) {
            for (Player boss : bosses) {
                if (boss != null && !boss.isDie()) {
                    return false;
                }
            }
        }
        return true;
    }

    private Zone resolveDungeonTransition(Player player, Zone targetDungeonZone, boolean isCurrentMapInDungeon) {
        if (targetDungeonZone == null) {
            return null;
        }
        if (isCurrentMapInDungeon) {
            if (isZoneCleared(targetDungeonZone)) {
                return targetDungeonZone;
            }
            if (!isZoneCleared(player.zone)) {
                return null;
            }
        }
        return targetDungeonZone;
    }

    public List<Map> getAllMaps() {
        return Manager.MAPS;
    }

    public Zone getZone(int mapId) {
        Map map = getMapById(mapId);
        if (map == null || map.zones == null || map.zones.isEmpty()) {
            return null;
        }

        Zone leastLoadedZone = null;
        int minPlayers = Integer.MAX_VALUE;

        for (Zone zone : map.zones) {
            if (zone == null) {
                continue;
            }
            int count = zone.getNumOfPlayers();
            int threshold = Math.max(1, (zone.maxPlayer * 2 + 1) / 3); // Tương đương Math.round(maxPlayer * 0.66f)

            // BƯỚC 1: Ưu tiên rót từ Khu 0 trở đi nếu chưa chạm ngưỡng ~66% (Early Return ngay lập tức)
            if (count < threshold) {
                return zone;
            }

            // BƯỚC 2 & 3 (Gộp chung): Ghi nhận khu có số người ít nhất trong cùng 1 lần duyệt O(N)
            if (count < minPlayers) {
                minPlayers = count;
                leastLoadedZone = zone;
            }
        }

        return leastLoadedZone != null ? leastLoadedZone : map.zones.get(0);
    }

    public Zone getZoneByMapIDAndZoneID(int mapId, int zoneId) {
        Zone zoneJoin = null;
        try {
            Map map = getMapById(mapId);
            if (map != null) {
                zoneJoin = map.zones.get(zoneId);
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
        return zoneJoin;
    }

    public Map getMapById(int mapId) {
        for (Map map : Manager.MAPS) {
            if (map.mapId == mapId) {
                return map;
            }
        }
        return null;
    }

    public Map getMapForCalich() {
        int mapId = Util.nextInt(27, 29);
        return MapService.gI().getMapById(mapId);
    }

    /**
     * Trả về 1 map random cho boss
     *
     * @param mapId
     * @return
     */
    public Zone getMapWithRandZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;
        try {
            if (map != null) {
                zone = map.zones.get(Util.nextInt(map.zones.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zone;
    }

    public String getPlanetName(byte planetId) {
        switch (planetId) {
            case 0:
                return "Trái đất";
            case 1:
                return "Namếc";
            case 2:
                return "Xayda";
            default:
                return "";
        }
    }

    /**
     * lấy danh sách map cho capsule
     *
     * @param pl
     * @return
     */
    public List<Zone> getMapCapsule(Player pl) {
        List<Zone> list = new ArrayList<>();
        if (pl.mapBeforeCapsule != null
                && pl.mapBeforeCapsule.map.mapId != 21
                && pl.mapBeforeCapsule.map.mapId != 22
                && pl.mapBeforeCapsule.map.mapId != 23
                && !isMapTuongLai(pl.mapBeforeCapsule.map.mapId)) {
            addListMapCapsule(pl, list, pl.mapBeforeCapsule);
        }
        addListMapCapsule(pl, list, getMapCanJoin(pl, 21 + pl.gender, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 47, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 45, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 0, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 7, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 14, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 5, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 20, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 13, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 24 + pl.gender, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 27, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 19, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 79, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 84, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 154, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 52, 0));
        return list;
    }

    public List<Zone> getMapBlackBall() {
        List<Zone> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(getMapById(85 + i).zones.get(0));
        }
        return list;
    }

    public List<Zone> getMapMaBu() {
        List<Zone> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(getMapById(114 + i).zones.get(0));
        }
        return list;
    }

    private void addListMapCapsule(Player pl, List<Zone> list, Zone zone) {
        for (Zone z : list) {
            if (z != null && zone != null && z.map.mapId == zone.map.mapId) {
                return;
            }
        }
        if (zone != null && pl.zone.map.mapId != zone.map.mapId) {
            list.add(zone);
        }
    }

    public void sendPlayerMove(Player player) {
        Message msg;
        try {
            msg = new Message(-7);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.location.x);
            msg.writer().writeShort(player.location.y);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    public boolean isMapOffline(int mapId) {
        for (Map map : Manager.MAPS) {
            if (map.mapId == mapId) {
                return map.type == ConstMap.MAP_OFFLINE;
            }
        }
        return false;
    }

    public boolean isMapBlackBallWar(int mapId) {
        return mapId >= 85 && mapId <= 91;
    }

    public boolean isMapMaBu(int mapId) {
        return mapId >= 114 && mapId <= 120 && mapId != 116;
    }

    public boolean isHome(int mapId) {
        return mapId >= 21 && mapId <= 23;
    }

    public boolean isMapYardart(int mapId) {
        return mapId >= 131 && mapId <= 133;
    }
    
    public boolean isMapCadic(int mapId) {
        return mapId == 165;
    }

    public boolean isMapBossFinal(int mapId) {
        return mapId == 111;
    }

    public boolean isMapCold(Map map) {
        return map != null && isMapCold(map.mapId);
    }

    public boolean isMapTuongLai(Map map) {
        return map != null && isMapTuongLai(map.mapId);
    }

    public boolean isMapCold(int mapId) {
    return mapId >= 105 && mapId <= 110;
}

    public boolean isMapDoanhTrai(int mapId) {
        return mapId >= 53 && mapId <= 62;
    }

    public boolean isMapSieuThanhThuy(int mapId) {
        return mapId == 146;
    }

    public boolean isMapRiengTu(int mapId) {
        return mapId == 164;
    }

    public boolean isMapNgucTu(int mapId) {
        return mapId == 155;
    }

    public boolean isMapHuyDiet(int mapId) {
        return mapId >= 169 && mapId <= 171;
    }

    public boolean isMapBanDoKhoBau(int mapId) {
        return mapId >= 135 && mapId <= 138;
    }

    public boolean isMapConDuongRanDoc(int mapId) {
        return mapId >= 141 && mapId <= 144;
    }

    public boolean isMapKhiGasHuyDiet(int mapId) {
        return mapId >= 147 && mapId <= 152 && mapId != 150;
    }

    public boolean isMapHanhTinhThucVat(int mapId) {
        return mapId >= 160 && mapId <= 163;
    }

    public boolean isMapNguHanhSon(int mapId) {
        return mapId >= 122 && mapId <= 124;
    }

    public boolean AllMap(int mapId) {
        return mapId >= 0 && mapId <= 163;
    }

    public boolean shouldChangeMap(int currentMapId, int newMapId) {
        return MapService.gI().isMapKhiGasHuyDiet(currentMapId)
                && MapService.gI().isMapKhiGasHuyDiet(newMapId)
                && currentMapId != 148 && newMapId != 148;
    }

    public boolean isMapPhoBan(int mapId) {
        return isMapBanDoKhoBau(mapId) || isMapDoanhTrai(mapId) || isMapConDuongRanDoc(mapId) || isMapKhiGasHuyDiet(mapId);
    }

    public boolean isMapTuongLai(int mapId) {
        return (mapId >= 92 && mapId <= 94)
                || (mapId >= 96 && mapId <= 100)
                || mapId == 102 || mapId == 103;
    }
    // Chưa được dùng
    public boolean isMapEvent8_3(int mapId) {
        return mapId >= 5 && mapId <= 110;
    }
    // Chưa được dùng
    public boolean isMapEventHungVuong(int mapId) {
        return mapId >= 1 && mapId <= 163;
    }

    public boolean isMapUpSKH(int mapId) {
        return mapId == 1 || mapId == 2 || mapId == 3
                || mapId == 8 || mapId == 9 || mapId == 11
                || mapId == 15 || mapId == 16 || mapId == 17;
    }

    public boolean isMapMabu2H(int mapId) {
        return mapId == 127 || mapId == 128;
    }

    public boolean isMapUpPorata(int mapId) {
        return mapId >= 156 && mapId <= 159;
    }

    public boolean isMapLuyenTap(int mapId) {
        return mapId >= 45 && mapId <= 50 && mapId != 47;
    }

    public boolean isMapNappa(int mapId) {
        return mapId >= 63 && mapId <= 83 && mapId != 78;
    }
    //Chưa được dùng
    public boolean isMapEventHalloween(int mapId) {
        return mapId == 174 || mapId == 179 || mapId == 180 || mapId == 181 || mapId == 168;
    }
    //Chưa được dùng
    public boolean isMapLang(int mapId) {
        return mapId == 0 || mapId == 7 || mapId == 14;
    }

    public boolean isMap3Planets(int mapId) {
        return mapId <= 38 && !(mapId == 24 || mapId == 25 || mapId == 26
                || mapId == 0 || mapId == 7 || mapId == 14
                || mapId == 21 || mapId == 22 || mapId == 23);
    }

    public void goToMap(Player player, Zone zoneJoin) {
        Zone oldZone = player.zone;
        if (oldZone != null) {
            ChangeMapService.gI().exitMap(player);
            if (player.mobMe != null) {
                player.mobMe.goToMap(zoneJoin);
            }
        }
        player.zone = zoneJoin;
        player.zone.addPlayer(player);
    }

    public List<Player> getPlayersInMap(int mapId) {
        Map map = getMapById(mapId);
        if (map == null) {
            return new ArrayList<>();
        }
        List<Player> playersInMap = new ArrayList<>();
        for (Zone zone : map.zones) {
            playersInMap.addAll(zone.getPlayers());
        }
        return playersInMap;
    }

}
