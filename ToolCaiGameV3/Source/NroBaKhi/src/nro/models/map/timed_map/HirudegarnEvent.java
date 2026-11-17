package nro.models.map.timed_map;

import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.utils.TimeUtil;

/**
 * Quản lý Sự kiện Ác quỷ Hirudegarn (Map 126) - Mở lúc 22h00 - 23h00
 */
public class HirudegarnEvent extends AbstractTimedMapEvent {

    private static final int[] EVENT_MAPS = {126};

    private static final class Holder {
        private static final HirudegarnEvent INSTANCE = new HirudegarnEvent();
    }

    public static HirudegarnEvent gI() {
        return Holder.INSTANCE;
    }

    @Override
    public int[] getEventMapIds() {
        return EVENT_MAPS;
    }

    @Override
    public boolean isEventOpen() {
        return TimeUtil.isHirudegarnOpen();
    }

    @Override
    protected void updateActivePlayer(Zone zone, Player player) {
        // Trong giờ mở cửa: Trận chiến với Hirudegarn diễn ra bình thường
    }

    @Override
    protected void kickOutPlayer(Player player) {
        TimedMapManager.gI().checkAndKickIfClosed(player);
    }
}
