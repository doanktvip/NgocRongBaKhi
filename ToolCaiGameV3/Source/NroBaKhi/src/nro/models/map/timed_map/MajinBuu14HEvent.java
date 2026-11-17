package nro.models.map.timed_map;

import nro.models.map.MaBuHold;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.utils.TimeUtil;

/**
 * Quản lý Sự kiện Ma Bư 14h (Map 127, 128) - Mở lúc 14h00 - 15h00
 */
public class MajinBuu14HEvent extends AbstractTimedMapEvent {

    private static final int[] EVENT_MAPS = {127, 128};

    private static final class Holder {
        private static final MajinBuu14HEvent INSTANCE = new MajinBuu14HEvent();
    }

    public static MajinBuu14HEvent gI() {
        return Holder.INSTANCE;
    }

    @Override
    public int[] getEventMapIds() {
        return EVENT_MAPS;
    }

    @Override
    public boolean isEventOpen() {
        return TimeUtil.isMabu14HOpen();
    }

    @Override
    protected void updateActiveZone(Zone zone) {
        if (zone.map != null && zone.map.mapId == 128 && zone.maBuHolds != null) {
            for (MaBuHold hold : zone.maBuHolds) {
                if (hold.player != null && hold.player.maBuHold == null && hold.player.zone != null) {
                    hold.player = null;
                }
            }
        }
        super.updateActiveZone(zone);
    }

    @Override
    protected void updateActivePlayer(Zone zone, Player player) {
        // Cập nhật người chơi trong Mabu 14h nếu cần
    }

    @Override
    protected void kickOutPlayer(Player player) {
        TimedMapManager.gI().checkAndKickIfClosed(player);
    }
}
