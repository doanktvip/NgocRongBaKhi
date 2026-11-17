package nro.models.map.timed_map;

import java.util.ArrayList;
import java.util.List;
import nro.models.database.MrBlue;
import nro.models.database.PlayerDAO;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.server.Client;
import nro.models.services.Service;
import nro.models.utils.Logger;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;

/**
 * Quản lý Sự kiện Ngọc Rồng Sao Đen (Map 85..91) - Mở lúc 20h00 - 21h00
 */
public class BlackBallWarEvent extends AbstractTimedMapEvent {

    private static final int[] EVENT_MAPS = {85, 86, 87, 88, 89, 90, 91};
    private static final int TIME_WIN = 300000;

    private static final class Holder {
        private static final BlackBallWarEvent INSTANCE = new BlackBallWarEvent();
    }

    public static BlackBallWarEvent gI() {
        return Holder.INSTANCE;
    }

    @Override
    public int[] getEventMapIds() {
        return EVENT_MAPS;
    }

    @Override
    public boolean isEventOpen() {
        return TimeUtil.isBlackBallWarOpen();
    }

    @Override
    protected void onClosedZoneReset(Zone zone) {
        zone.finishBlackBallWar = false;
    }

    @Override
    protected void updateActivePlayer(Zone zone, Player player) {
        if (player.idMark != null && player.idMark.isHoldBlackBall()) {
            if (Util.canDoWithTime(player.idMark.getLastTimeHoldBlackBall(), TIME_WIN)) {
                win(player);
            } else if (Util.canDoWithTime(player.idMark.getLastTimeNotifyTimeHoldBlackBall(), 10000)) {
                Service.gI().sendThongBao(player, "Cố giữ ngọc thêm "
                        + TimeUtil.getSecondLeft(player.idMark.getLastTimeHoldBlackBall(), TIME_WIN / 1000)
                        + " giây nữa sẽ thắng");
                player.idMark.setLastTimeNotifyTimeHoldBlackBall(System.currentTimeMillis());
            }
        }
    }

    private synchronized void win(Player player) {
        if (player.zone == null) {
            return;
        }
        player.zone.finishBlackBallWar = true;
        int star = player.idMark.getTempIdBlackBallHold() - 371;
        player.rewardBlackBall.reward((byte) star);
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã dành được Ngọc rồng " + star + " sao đen cho bang");

        if (player.clan != null) {
            List<Integer> offlineMemberIds = new ArrayList<>();
            for (var m : player.clan.members) {
                Player p = Client.gI().getPlayer(m.id);
                if (p != null) {
                    p.rewardBlackBall.reward((byte) star);
                } else {
                    offlineMemberIds.add(m.id);
                }
            }
            if (!offlineMemberIds.isEmpty()) {
                Thread.ofVirtual().name("BlackBall-OfflineReward-Clan-" + player.clan.id).start(() -> {
                    for (int memberId : offlineMemberIds) {
                        try {
                            Player pFromDb = MrBlue.loadById(memberId);
                            if (pFromDb != null) {
                                pFromDb.rewardBlackBall.reward((byte) star);
                                PlayerDAO.updatePlayer(pFromDb);
                            }
                        } catch (Exception e) {
                            Logger.logException(BlackBallWarEvent.class, e, "Lỗi trao thưởng NRĐ offline ID: " + memberId);
                        }
                    }
                });
            }
        }

        // Trục xuất toàn bộ người chơi trong khu vực thắng
        List<Player> players = player.zone.getPlayers();
        for (int i = players.size() - 1; i >= 0; i--) {
            if (i < players.size()) {
                Player pl = players.get(i);
                if (pl != null) {
                    kickOutPlayer(pl);
                }
            }
        }
    }

    @Override
    protected void kickOutPlayer(Player player) {
        TimedMapManager.gI().checkAndKickIfClosed(player);
    }
}
