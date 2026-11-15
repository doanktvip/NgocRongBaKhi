package nro.models.map.timed_map;

import nro.models.map.Zone;
import nro.models.map.service.ChangeMapService;
import nro.models.map.service.MapService;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;

import java.util.List;

/**
 * Bộ quản lý tập trung toàn bộ 16 Bản Đồ Mở Theo Giờ (Timed Maps)
 * Đảm bảo cơ chế kiểm tra giờ mở cửa, thông báo chuẩn xác và trục xuất người chơi ngoài giờ.
 */
public class TimedMapManager {

    private static TimedMapManager instance;

    /**
     * Cờ cấu hình quyền Admin khi ở trong map mở theo giờ:
     * - false: Đuổi cả Admin giống như người chơi thường để test cơ chế đá map.
     * - true: Cho phép Admin ở lại để kiểm tra/soi map.
     */
    public static boolean ALLOW_ADMIN_STAY = false;

    public static TimedMapManager gI() {
        if (instance == null) {
            instance = new TimedMapManager();
        }
        return instance;
    }

    /**
     * Kiểm tra mapId có thuộc bất kỳ bản đồ mở theo giờ nào (16 Map) hay không.
     */
    public boolean isTimedMap(int mapId) {
        return MapService.gI().isMapMaBu(mapId)
                || MapService.gI().isMapMabu2H(mapId)
                || MapService.gI().isMapBlackBallWar(mapId)
                || mapId == 126;
    }

    /**
     * Kiểm tra bản đồ mở theo giờ tương ứng có đang trong khung giờ mở cửa hay không.
     */
    public boolean isTimedMapOpen(int mapId) {
        if (MapService.gI().isMapMaBu(mapId)) {
            return TimeUtil.isMabuOpen();
        }
        if (MapService.gI().isMapMabu2H(mapId)) {
            return TimeUtil.isMabu14HOpen();
        }
        if (MapService.gI().isMapBlackBallWar(mapId)) {
            return TimeUtil.isBlackBallWarOpen();
        }
        if (mapId == 126) {
            return TimeUtil.isHirudegarnOpen();
        }
        return true;
    }

    /**
     * Lấy thông báo đóng cửa tương ứng của từng sự kiện.
     */
    public String getClosedMessage(int mapId) {
        if (MapService.gI().isMapMaBu(mapId)) {
            return "Phi thuyền Mabư đã đóng cửa, hẹn gặp lại vào 12h trưa mai!";
        }
        if (MapService.gI().isMapMabu2H(mapId)) {
            return "Phi thuyền Mabư 14H đã đóng cửa, hẹn gặp lại vào 14h ngày mai!";
        }
        if (MapService.gI().isMapBlackBallWar(mapId)) {
            return "Trò chơi tìm ngọc hôm nay đã kết thúc, hẹn gặp lại vào 20h ngày mai!";
        }
        if (mapId == 126) {
            return "Ác quỷ Hirudegarn đã bị phong ấn, hẹn gặp lại vào 22h tối mai!";
        }
        return "Bản đồ này hiện đang đóng cửa!";
    }

    /**
     * Lấy Map ID an toàn để trục xuất người chơi về.
     */
    public int getKickDestinationMapId(Player player, int currentMapId) {
        if (MapService.gI().isMapBlackBallWar(currentMapId)) {
            return player.gender + 24; // Trạm tàu vũ trụ
        }
        if (currentMapId == 126) {
            return 19; // Thành phố Vegeta (nơi gặp Tapion)
        }
        // Mabu 12h & Mabu 14h: Trở về nhà của nhân vật
        return player.gender + 21;
    }

    /**
     * Kiểm tra và trục xuất người chơi ngay lập tức nếu đang đứng ở map ngoài giờ.
     * @return true nếu người chơi bị trục xuất, false nếu hợp lệ.
     */
    public boolean checkAndKickIfClosed(Player player) {
        if (player == null || player.zone == null) {
            return false;
        }

        int currentMapId = player.zone.map.mapId;
        if (!isTimedMap(currentMapId)) {
            return false;
        }

        // Kiểm tra quyền Admin
        if (ALLOW_ADMIN_STAY && player.isAdmin()) {
            return false;
        }

        // Nếu map đang mở thì cho phép ở lại
        if (isTimedMapOpen(currentMapId)) {
            return false;
        }

        // Dọn dẹp cờ hiệu sự kiện nếu có
        if (player.cFlag == 8) { // Cờ Sao Đen
            Service.gI().changeFlag(player, Util.nextInt(1, 7));
        } else if (player.cFlag == 9 || player.cFlag == 10) { // Cờ Mabu
            Service.gI().changeFlag(player, 0);
        }

        // Reset trạng thái Mabu
        player.goHome = false;
        player.timeGohome = 0;

        // Gửi thông báo và trục xuất ngay lập tức
        String msg = getClosedMessage(currentMapId);
        Service.gI().sendThongBao(player, msg);

        int targetMap = getKickDestinationMapId(player, currentMapId);
        ChangeMapService.gI().changeMapBySpaceShip(player, targetMap, -1, 250);
        return true;
    }

    /**
     * Quét và trục xuất toàn bộ người chơi trong một Zone nếu Zone đó thuộc map đang đóng cửa.
     */
    public void checkAndKickAllIfClosed(Zone zone) {
        if (zone == null || zone.map == null) {
            return;
        }
        int mapId = zone.map.mapId;
        if (!isTimedMap(mapId) || isTimedMapOpen(mapId)) {
            return;
        }

        List<Player> players = zone.getPlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            if (i < players.size()) {
                Player pl = players.get(i);
                checkAndKickIfClosed(pl);
            }
        }
    }
}
