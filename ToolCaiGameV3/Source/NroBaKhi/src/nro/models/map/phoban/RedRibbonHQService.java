package nro.models.map.phoban;

import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.map.service.ChangeMapService;
import java.util.ArrayList;
import java.util.List;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;

/**
 * Service quản lý hoạt động Doanh Trại Độc Nhãn (RedRibbonHQ)
 */
public class RedRibbonHQService extends AbstractClanDungeonService<RedRibbonHQ> {

    // Cấu hình điều kiện tham gia & sức chứa Doanh Trại
    public static final int AVAILABLE = 50;
    public static final int N_PLAYER_CLAN = 1;//3
    public static final int N_PLAYER_MAP = 0;//2
    public static final int N_DAY_JOIN_CLAN = 0;//1
    public static final int MIN_X_JOIN = 1285;
    public static final int MAX_X_JOIN = 1645;
    public static final long TIME_RESET_DOANH_TRAI = 86400000L; // 24 giờ

    private static final class Holder {
        private static final RedRibbonHQService INSTANCE = new RedRibbonHQService();
    }

    public static RedRibbonHQService gI() {
        return Holder.INSTANCE;
    }

    public List<RedRibbonHQ> doanhTrais = this.rooms;

    private RedRibbonHQService() {
        super(AVAILABLE, RedRibbonHQ::new);
    }

    public void addMapDoanhTrai(int id, Zone zone) {
        registerZone(id, zone);
    }

    /**
     * Tìm danh sách thành viên cùng bang đang đứng gần NPC ở Map 27 đủ điều kiện vào Doanh Trại
     */
    public List<Player> getNearbyClanMembers(Player player) {
        List<Player> members = new ArrayList<>();
        if (player != null && player.zone != null && player.clan != null) {
            for (Player pl : player.zone.getPlayers()) {
                if (pl != null && !pl.equals(player) && pl.clan != null
                        && pl.clan.equals(player.clan) && pl.location != null
                        && pl.location.x >= MIN_X_JOIN && pl.location.x <= MAX_X_JOIN
                        && pl.clanMember != null && pl.clanMember.getNumDateFromJoinTimeToToday() >= N_DAY_JOIN_CLAN
                        && !pl.isDie()) {
                    members.add(pl);
                }
            }
        }
        return members;
    }

    public synchronized void joinDoanhTrai(Player pl) {
        if (pl.clan == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (pl.clanMember == null || pl.clanMember.getNumDateFromJoinTimeToToday() < N_DAY_JOIN_CLAN) {
            Service.gI().sendThongBao(pl, "Gia nhập bang hội trên " + N_DAY_JOIN_CLAN + " ngày mới được tham gia");
            return;
        }
        if (pl.clan.doanhTrai != null) {
            ChangeMapService.gI().changeMapInYard(pl, 53, -1, 60);
            return;
        }
        if (pl.clan.lastTimeOpenDoanhTrai > 0 && !Util.canDoWithTime(pl.clan.lastTimeOpenDoanhTrai, TIME_RESET_DOANH_TRAI)) {
            Service.gI().sendThongBao(pl, "Bang hội phải chờ " + TimeUtil.getDateLeft(pl.clan.lastTimeOpenDoanhTrai, 86400) + " nữa");
            return;
        }

        List<Player> participants = new ArrayList<>();
        participants.add(pl);
        participants.addAll(getNearbyClanMembers(pl));

        RedRibbonHQ doanhTrai = findAvailableRoom();
        if (doanhTrai == null) {
            Service.gI().sendThongBao(pl, "Doanh trại đã đầy, hãy quay lại vào lúc khác!");
            return;
        }

        doanhTrai.openDoanhTrai(pl.clan, pl, participants);

        for (Player participant : participants) {
            if (participant != null && participant.zone != null && !participant.isDie()) {
                ChangeMapService.gI().changeMapInYard(participant, 53, -1, 60);
            }
        }
    }
}
