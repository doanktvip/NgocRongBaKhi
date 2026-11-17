package nro.models.map.timed_map;

import nro.models.map.Zone;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;

/**
 * Quản lý Sự kiện Ma Bư 12h (Map 114, 115, 117, 118, 119, 120) - Mở lúc 12h00 - 13h00
 */
public class MajinBuu12HEvent extends AbstractTimedMapEvent {

    private static final int[] EVENT_MAPS = {114, 115, 117, 118, 119, 120};

    private static final class Holder {
        private static final MajinBuu12HEvent INSTANCE = new MajinBuu12HEvent();
    }

    public static MajinBuu12HEvent gI() {
        return Holder.INSTANCE;
    }

    @Override
    public int[] getEventMapIds() {
        return EVENT_MAPS;
    }

    @Override
    public boolean isEventOpen() {
        return TimeUtil.isMabuOpen();
    }

    @Override
    protected void updateActivePlayer(Zone zone, Player player) {
        if (player.isPl() && player.cFlag == 9 && Util.isTrue(1, 100)) {
            Npc babiday = MajinBuuService.gI().getNpcBabiday(player);
            if (babiday != null) {
                babiday.npcChat(player.zone, "Úm bala xì bùa " + player.name);
                Service.gI().sendThongBao(player, "Bạn đã bị Babiđây thôi miên");
                Service.gI().changeFlag(player, 10);
            }
        } else if (player.isPl() && player.cFlag == 10 && Util.isTrue(1, 50)) {
            Npc osin = MajinBuuService.gI().getNpcOsin(player);
            if (osin != null) {
                osin.npcChat(player.zone, "Úm bala xì bùa hóa giải cho " + player.name);
                Service.gI().sendThongBao(player, "Bạn đã được Ôsin giải bùa mê");
                Service.gI().changeFlag(player, 9);
            }
        }
    }

    @Override
    protected void kickOutPlayer(Player player) {
        TimedMapManager.gI().checkAndKickIfClosed(player);
    }
}
