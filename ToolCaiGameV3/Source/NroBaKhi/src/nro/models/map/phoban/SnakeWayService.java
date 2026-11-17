package nro.models.map.phoban;

import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import java.util.ArrayList;
import java.util.List;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;

/**
 * Service quản lý hoạt động Con Đường Rắn Độc (SnakeWay)
 */
public class SnakeWayService extends AbstractClanDungeonService<SnakeWay> {

    private static final class Holder {
        private static final SnakeWayService INSTANCE = new SnakeWayService();
    }

    public static SnakeWayService gI() {
        return Holder.INSTANCE;
    }

    public List<SnakeWay> conDuongRanDocs = this.rooms;

    private SnakeWayService() {
        super(SnakeWay.AVAILABLE, SnakeWay::new);
    }

    public void addMapConDuongRanDoc(int id, Zone zone) {
        registerZone(id, zone);
    }

    public synchronized void openConDuongRanDoc(Player player, byte level) {
        Clan clan = player.clan;
        if (clan != null) {
            ClanMember cm = clan.getClanMember((int) player.id);
            if (cm != null) {
                if (player.clanMember.getNumDateFromJoinTimeToToday() < 2) {
                    return;
                }
                if (!player.joinCDRD && !Util.isTimeDifferenceGreaterThanNDays(player.lastTimeJoinCDRD, 7)) {
                    Service.gI().sendThongBao(player, "Vui lòng đợi " + TimeUtil.getDateLeft(player.lastTimeJoinCDRD, 7 * 24 * 60 * 60) + " nữa");
                    return;
                } else if (!player.joinCDRD) {
                    player.talkToThuongDe = false;
                    player.talkToThanMeo = false;
                }
                if (player.clan.ConDuongRanDoc == null) {
                    if (level >= 1 && level <= 110) {
                        SnakeWay conDuongRanDoc = findAvailableRoom();
                        if (conDuongRanDoc != null) {
                            conDuongRanDoc.openConDuongRanDoc(player, player.clan, level);
                        } else {
                            Service.gI().sendThongBao(player, "Con đường rắn độc đã đầy, hãy quay lại sau 30 phút");
                            return;
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                        return;
                    }
                }
                if (player.clan.ConDuongRanDoc != null) {
                    player.joinCDRD = true;
                    player.lastTimeJoinCDRD = player.clan.lastTimeOpenConDuongRanDoc;
                    ChangeMapService.gI().changeMap(player, 143, -1, 1055 + (Util.nextInt(-10, 10)), 0);
                }
            }
        }
    }
}
