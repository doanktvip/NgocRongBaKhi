package nro.models.map.timed_map;

import java.util.List;
import nro.models.map.Map;
import nro.models.map.Zone;
import nro.models.map.service.MapService;
import nro.models.player.Player;
import nro.models.utils.Logger;

/**
 * Lớp cha trừu tượng chuẩn hóa vòng đời cho các Sự kiện Bản đồ Mở Theo Giờ
 * (Sao Đen 20h, Ma Bư 12h, Ma Bư 14h, Hirudegarn 22h)
 */
public abstract class AbstractTimedMapEvent {

    private boolean wasOpenLastTick = false;

    /**
     * Danh sách các mapId thuộc sự kiện này
     */
    public abstract int[] getEventMapIds();

    /**
     * Kiểm tra hiện tại có đang trong khung giờ mở sự kiện hay không
     */
    public abstract boolean isEventOpen();

    /**
     * Hàm Template Method được TimedMapManager gọi định kỳ mỗi nhịp (Tick)
     */
    public final void tick() {
        boolean currentlyOpen = isEventOpen();

        // 1. Chuyển trạng thái từ Đóng -> Mở hoặc từ Mở -> Đóng
        if (currentlyOpen && !wasOpenLastTick) {
            onEventStart();
        } else if (!currentlyOpen && wasOpenLastTick) {
            onEventEnd();
        }
        wasOpenLastTick = currentlyOpen;

        // 2. Duyệt qua toàn bộ các Map và Zone thuộc sự kiện
        for (int mapId : getEventMapIds()) {
            Map map = MapService.gI().getMapById(mapId);
            if (map == null || map.zones == null) {
                continue;
            }
            for (Zone zone : map.zones) {
                if (zone == null) {
                    continue;
                }
                try {
                    if (currentlyOpen) {
                        updateActiveZone(zone);
                    } else {
                        evictPlayersWhenClosed(zone);
                    }
                } catch (Exception e) {
                    Logger.logException(this.getClass(), e, "Lỗi tick sự kiện tại map " + mapId + " khu " + zone.zoneId);
                }
            }
        }
    }

    /**
     * Khi sự kiện đang mở: Cập nhật Zone và từng Player đang có mặt trong Zone
     */
    protected void updateActiveZone(Zone zone) {
        List<Player> players = zone.getPlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            if (i < players.size()) {
                Player pl = players.get(i);
                if (pl != null && pl.isPl()) {
                    updateActivePlayer(zone, pl);
                }
            }
        }
    }

    /**
     * Khi sự kiện đã đóng: Tự động trục xuất toàn bộ người chơi (trừ Admin nếu cấu hình) ra khỏi map sự kiện
     */
    protected void evictPlayersWhenClosed(Zone zone) {
        onClosedZoneReset(zone);
        List<Player> players = zone.getPlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            if (i < players.size()) {
                Player pl = players.get(i);
                if (pl != null && pl.isPl()) {
                    if (TimedMapManager.ALLOW_ADMIN_STAY && pl.isAdmin()) {
                        continue;
                    }
                    kickOutPlayer(pl);
                }
            }
        }
    }

    // ==================== CÁC HOOK CHO LỚP CON GHI ĐÈ (@Override) ====================

    /**
     * Xử lý logic cho từng người chơi khi sự kiện đang mở
     */
    protected abstract void updateActivePlayer(Zone zone, Player player);

    /**
     * Đưa người chơi về map an toàn khi hết giờ sự kiện
     */
    protected abstract void kickOutPlayer(Player player);

    /**
     * Hook chạy 1 lần khi sự kiện bắt đầu mở
     */
    protected void onEventStart() {
    }

    /**
     * Hook chạy 1 lần khi sự kiện vừa kết thúc
     */
    protected void onEventEnd() {
    }

    /**
     * Hook reset cờ hoặc trạng thái của Zone khi ngoài giờ sự kiện
     */
    protected void onClosedZoneReset(Zone zone) {
    }
}
