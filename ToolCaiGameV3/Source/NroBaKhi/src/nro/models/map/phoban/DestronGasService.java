package nro.models.map.phoban;

import nro.models.clan.Clan;
import nro.models.clan.ClanMember;
import java.util.ArrayList;
import java.util.List;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.utils.Util;

/**
 * Service quản lý hoạt động Khí Gas Hủy Diệt (DestronGas)
 */
public class DestronGasService extends AbstractClanDungeonService<DestronGas> {

    private static final class Holder {
        private static final DestronGasService INSTANCE = new DestronGasService();
    }

    public static DestronGasService gI() {
        return Holder.INSTANCE;
    }

    public List<DestronGas> khiGasHuyDiets = this.rooms;

    private DestronGasService() {
        super(DestronGas.AVAILABLE, DestronGas::new);
    }

    public void addMapKhiGasHuyDiet(int id, Zone zone) {
        registerZone(id, zone);
    }

    public synchronized void openKhiGasHuyDiet(Player player, byte level) {
        Clan clan = player.clan;
        if (clan != null) {
            ClanMember cm = clan.getClanMember((int) player.id);
            if (cm != null) {
                if (player.clanMember.getNumDateFromJoinTimeToToday() < 2) {
                    return;
                }
                if (clan.members.size() < DestronGas.N_PLAYER_CLAN) {
                    return;
                }
                if (player.clan.KhiGasHuyDiet == null) {
                    if (level >= 1 && level <= 110) {
                        if (clan.isLeader(player)) {
                            DestronGas khiGasHuyDiet = findAvailableRoom();
                            if (khiGasHuyDiet != null) {
                                if (Util.isAfterMidnight(player.clan.lastTimeOpenKhiGasHuyDiet)) {
                                    player.clan.timesPerDayKGHD = 1;
                                } else {
                                    player.clan.timesPerDayKGHD++;
                                    if (player.clan.timesPerDayKGHD > 3) {
                                        Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                        return;
                                    }
                                }
                                khiGasHuyDiet.openKhiGasHuyDiet(player, player.clan, level);
                            } else {
                                Service.gI().sendThongBao(player, "Destron Gas đã đầy, hãy quay lại sau 30 phút");
                                return;
                            }
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                        return;
                    }
                }
                if (player.clan.KhiGasHuyDiet != null && !player.idMark.isGoToKGHD()) {
                    player.idMark.setLastTimeGoToKGHD(System.currentTimeMillis());
                    player.idMark.setGoToKGHD(true);
                    player.type = 4;
                    player.maxTime = 5;
                    Service.gI().Transport(player, 1);
                }
            }
        }
    }
}
