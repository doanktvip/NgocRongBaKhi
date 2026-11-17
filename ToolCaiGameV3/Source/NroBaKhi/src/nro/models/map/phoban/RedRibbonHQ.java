package nro.models.map.phoban;
import nro.models.utils.Functions;
import nro.models.boss.Boss;
import nro.models.boss.doanh_trai.NinjaAoTim;
import nro.models.boss.doanh_trai.RobotVeSi;
import nro.models.boss.doanh_trai.TrungUyThep;
import nro.models.boss.doanh_trai.TrungUyTrang;
import nro.models.boss.doanh_trai.TrungUyXanhLo;
import nro.models.clan.Clan;
import nro.models.consts.ConstItem;
import nro.models.consts.ConstItem;
import nro.models.map.Zone;
import nro.models.mob.Mob;
import nro.models.player.Player;
import nro.models.map.service.ChangeMapService;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import nro.models.map.ItemMap;
import nro.models.server.Maintenance;
import nro.models.map.service.ItemMapService;
import nro.models.services.ItemTimeService;
import nro.models.map.service.MapService;
import nro.models.services.Service;
import nro.models.utils.Util;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
public class RedRibbonHQ extends AbstractClanDungeon {

    // Cấu hình điều kiện vào cửa (tham chiếu từ RedRibbonHQService để đảm bảo Single Source of Truth & tương thích ngược)
    public static final int AVAILABLE = RedRibbonHQService.AVAILABLE;
    public static final int N_PLAYER_CLAN = RedRibbonHQService.N_PLAYER_CLAN;
    public static final int N_PLAYER_MAP = RedRibbonHQService.N_PLAYER_MAP;
    public static final int N_DAY_JOIN_CLAN = RedRibbonHQService.N_DAY_JOIN_CLAN;
    public static final int MIN_X_JOIN = RedRibbonHQService.MIN_X_JOIN;
    public static final int MAX_X_JOIN = RedRibbonHQService.MAX_X_JOIN;
    public static final long TIME_RESET_DOANH_TRAI = RedRibbonHQService.TIME_RESET_DOANH_TRAI;

    // Cấu hình nội tại phòng phó bản
    public static final int TIME_DOANH_TRAI = 1800000;
    public static final int TIME_PICK_DOANH_TRAI = 300000;
    public static final long MAX_HP_CAP = 2_000_000_000L; // Trần tối đa 2 tỷ HP
    public static final long MAX_DAME_CAP = 200_000_000L; // Trần tối đa 200 triệu Dame

    public boolean isTimePicking;
    public long lastTimePick;
    public boolean winDT;

    public RedRibbonHQ(int id) {
        this.id = id;
    }

    /**
     * Mở phòng Doanh Trại và khởi tạo thực thể
     *
     * @param clan Bang hội sở hữu
     * @param opener Người kích hoạt mở
     * @param participants Danh sách thành viên tham gia thực tế
     */
    public void openDoanhTrai(Clan clan, Player opener, List<Player> participants) {
        try {
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = clan;
            clan.doanhTrai = this;
            clan.playerOpenDoanhTrai = opener;
            clan.lastTimeOpenDoanhTrai = this.lastTimeOpen;
            clan.updateLastTimeOpenDoanhTrai();
            clan.haveGoneDoanhTrai = false;
            sendTextDoanhTrai();
            // Khởi tạo quái, boss theo đúng danh sách người tham gia thực tế
            this.isOpened = true;
            this.init(participants);
        } catch (Exception e) {
            e.printStackTrace();
            clan.lastTimeOpenDoanhTrai = 0;
            clan.updateLastTimeOpenDoanhTrai();
            clan.haveGoneDoanhTrai = false;
            this.dispose();
        }
    }

    /**
     * Overload tương thích ngược cho các lời gọi cũ
     */
    public void openDoanhTrai(Player player) {
        List<Player> participants = new ArrayList<>();
        participants.add(player);
        if (player.clan != null) {
            participants.addAll(RedRibbonHQService.gI().getNearbyClanMembers(player));
            openDoanhTrai(player.clan, player, participants);
            for (Player pl : participants) {
                if (pl != null && pl.zone != null && !pl.isDie()) {
                    ChangeMapService.gI().changeMapInYard(pl, 53, -1, 60);
                }
            }
        }
    }

    private long[] calculateAvgDameHp(List<Player> participants) {
        long totalDame = 0;
        long totalHp = 0;
        int count = 0;
        if (participants != null) {
            for (Player pl : participants) {
                if (pl != null && pl.nPoint != null) {
                    totalDame += pl.nPoint.dame;
                    totalHp += pl.nPoint.hpMax;
                    count++;
                }
            }
        }
        int n = Math.max(1, count);
        long avgDame = Math.max(1L, totalDame / n);
        long avgHp = Math.max(1L, totalHp / n);
        return new long[]{avgDame, avgHp, n};
    }

    private int getMobHp(int mapId, long avgDame) {
        double mult = switch (mapId) {
            case 53, 58 -> 3.0;
            case 59, 62 -> 4.0;
            case 55, 54 -> 5.0;
            case 57 -> 6.0;
            default -> 4.0;
        };
        long hp = (long) (avgDame * mult);
        return (int) Math.min(Math.max(1L, hp), MAX_HP_CAP);
    }

    private int getMobDame(long avgHp) {
        long dame = (avgHp * 2) / 100; // 2% HP trung bình của đội
        return (int) Math.min(Math.max(1L, dame), MAX_DAME_CAP);
    }

    private void init(List<Player> participants) {
        long[] stats = calculateAvgDameHp(participants);
        long avgDame = stats[0];
        long avgHp = stats[1];
        long n = stats[2];

        // Hồi sinh quái và khởi tạo Boss theo chỉ số thực tế
        for (Zone zone : this.zones) {
            int mapId = zone.map.mapId;
            int mobHp = getMobHp(mapId, avgDame);
            int mobDame = getMobDame(avgHp);

            for (Mob mob : zone.mobs) {
                mob.point.dame = mobDame;
                mob.point.maxHp = mobHp;
                mob.lvMob = 0;
                mob.hoiSinh();
                mob.hoiSinhMobPhoBan();
            }

            if (mapId == 59) {
                try {
                    long bHp = Math.min(avgDame * 60 * n, MAX_HP_CAP);
                    long bDame = Math.min((avgHp * 6) / 100, MAX_DAME_CAP);
                    bosses.add(new TrungUyTrang(zone, (int) Math.max(1L, bDame), (int) Math.max(1L, bHp)));
                } catch (Exception e) {
                    nro.models.utils.Logger.logException(RedRibbonHQ.class, e);
                }
            }
            if (mapId == 62) {
                try {
                    long bHp = Math.min(avgDame * 90 * n, MAX_HP_CAP);
                    long bDame = Math.min((avgHp * 8) / 100, MAX_DAME_CAP);
                    bosses.add(new TrungUyXanhLo(zone, (int) Math.max(1L, bDame), (int) Math.max(1L, bHp)));
                } catch (Exception e) {
                    nro.models.utils.Logger.logException(RedRibbonHQ.class, e);
                }
            }
            if (mapId == 55) {
                try {
                    long bHp = Math.min(avgDame * 120 * n, MAX_HP_CAP);
                    long bDame = Math.min((avgHp * 10) / 100, MAX_DAME_CAP);
                    bosses.add(new TrungUyThep(zone, (int) Math.max(1L, bDame), (int) Math.max(1L, bHp)));
                } catch (Exception e) {
                    nro.models.utils.Logger.logException(RedRibbonHQ.class, e);
                }
            }
            if (mapId == 54) {
                try {
                    long bHp = Math.min(avgDame * 150 * n, MAX_HP_CAP);
                    long bDame = Math.min((avgHp * 12) / 100, MAX_DAME_CAP);
                    bosses.add(new NinjaAoTim(zone, clan, (int) Math.max(1L, bDame), (int) Math.max(1L, bHp)));
                } catch (Exception e) {
                    nro.models.utils.Logger.logException(RedRibbonHQ.class, e);
                }
            }
            if (mapId == 57) {
                try {
                    long bHp = Math.min(avgDame * 50 * n, MAX_HP_CAP);
                    long bDame = Math.min((avgHp * 8) / 100, MAX_DAME_CAP);
                    for (int i = 0; i < 4; i++) {
                        bosses.add(new RobotVeSi(zone, i, (int) Math.max(1L, bDame), (int) Math.max(1L, bHp)));
                    }
                } catch (Exception e) {
                    nro.models.utils.Logger.logException(RedRibbonHQ.class, e);
                }
            }
        }
        startDungeonThread("Doanh Trại");
    }

    public void update() {
        if (!isOpened) {
            return;
        }
        if ((!isTimePicking && Util.canDoWithTime(lastTimeOpen, TIME_DOANH_TRAI)) || (isTimePicking && Util.canDoWithTime(lastTimePick, TIME_PICK_DOANH_TRAI))) {
            finish();
            dispose();
            return;
        }

        boolean allCharactersDead = this.areAllCharactersDead();
        if (allCharactersDead && !winDT) {
            winDT = true;
            for (Zone zone : zones) {
                if (zone != null) {
                    List<Player> players = zone.getPlayers();
                    if (players != null) {
                        for (Player pl : players) {
                            if (pl != null) {
                                Service.gI().sendThongBao(pl, "Mau đi tìm Độc Nhãn");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Thả 1 mảnh đơn lẻ (quantity = 1, giống Ngọc Rồng) vào một Zone ngẫu nhiên trong Doanh Trại
     */
    private void dropSingleCardFragment(int tempId) {
        if (this.zones == null || this.zones.isEmpty()) {
            return;
        }
        Zone zone = this.zones.get(Util.nextInt(0, this.zones.size() - 1));
        if (zone != null && zone.map != null) {
            int x = Util.nextInt(100, zone.map.mapWidth - 100);
            int y = zone.map.yPhysicInTop(x, 100);
            ItemMap it = new ItemMap(zone, tempId, 1, x, y, -1);
            Service.gI().dropItemMap(zone, it);
        }
    }

    /**
     * Rải ngẫu nhiên các Mảnh Sưu Tầm
     * - Mảnh Lính Độc Nhãn (837): 2 - 5 mảnh
     * - Mảnh Lính Độc Nhãn 2 (838): 2 - 5 mảnh
     * - Mảnh Sói Xám (839): 2 - 5 mảnh
     * - Mảnh Trung Úy Trắng (840): 1 - 3 mảnh
     * - Mảnh Trung Úy Xanh Lơ (842): 1 - 3 mảnh
     * - Mảnh Ninja Áo Tím (841): 1 - 3 mảnh
     * - Mảnh Độc Nhãn (859): đúng 1 mảnh
     * - Mảnh Đội Trưởng Vàng (956): đúng 1 mảnh
     */
    public void randomCardFragments() {
        if (this.zones == null || this.zones.isEmpty()) {
            return;
        }
        int numLinh1 = Util.nextInt(2, 5);
        for (int i = 0; i < numLinh1; i++) {
            dropSingleCardFragment(ConstItem.MANH_LINH_DOC_NHAN);
        }

        int numLinh2 = Util.nextInt(2, 5);
        for (int i = 0; i < numLinh2; i++) {
            dropSingleCardFragment(ConstItem.MANH_LINH_DOC_NHAN_838);
        }

        int numSoi = Util.nextInt(2, 5);
        for (int i = 0; i < numSoi; i++) {
            dropSingleCardFragment(ConstItem.MANH_SOI_XAM);
        }

        int numTrang = Util.nextInt(1, 3);
        for (int i = 0; i < numTrang; i++) {
            dropSingleCardFragment(ConstItem.MANH_TRUNG_UY_TRANG);
        }

        int numXanhLo = Util.nextInt(1, 3);
        for (int i = 0; i < numXanhLo; i++) {
            dropSingleCardFragment(ConstItem.MANH_TRUNG_UY_XANH_LO);
        }

        int numNinja = Util.nextInt(1, 3);
        for (int i = 0; i < numNinja; i++) {
            dropSingleCardFragment(ConstItem.MANH_NINJA_AO_TIM);
        }

        dropSingleCardFragment(ConstItem.MANH_DOC_NHAN);
        dropSingleCardFragment(ConstItem.MANH_DOI_TRUONG_VANG);
    }

    /**
     * Tương thích ngược với các lời gọi cũ
     */
    public void randomNR() {
        randomCardFragments();
    }

    @Override
    protected void kickOut(Player player) {
        if (player != null && player.zone != null && player.zone.map != null) {
            if (MapService.gI().isMapDoanhTrai(player.zone.map.mapId)) {
                Service.gI().sendThongBao(player, "Đã hết thời gian, bạn sẽ được đưa về nhà");
                ChangeMapService.gI().changeMapBySpaceShip(player, 21 + player.gender, -1, -1);
            }
        }
    }

    @Override
    protected void sendTextTime() {
        if (this.clan != null && this.clan.membersInGame != null) {
            for (Player pl : this.clan.membersInGame) {
                if (pl != null) {
                    if (this.isTimePicking) {
                        ItemTimeService.gI().sendTextTimePickDoanhTrai(pl);
                    } else {
                        ItemTimeService.gI().sendTextDoanhTrai(pl);
                    }
                }
            }
        }
    }

    public void sendTextDoanhTrai() {
        sendTextTime();
    }

    public void sendTextTimePickDoanhTrai() {
        sendTextTime();
    }

    @Override
    protected void removeTextTime() {
        if (this.clan != null && this.clan.membersInGame != null) {
            for (Player pl : this.clan.membersInGame) {
                if (pl != null) {
                    ItemTimeService.gI().removeTextDoanhTrai(pl);
                }
            }
        }
    }

    @Override
    protected void unlinkClan() {
        if (this.clan != null) {
            this.clan.haveGoneDoanhTrai = true;
            this.clan.doanhTrai = null;
        }
    }

    @Override
    protected void onResetExtraState() {
        this.winDT = false;
        this.isTimePicking = false;
    }

    public void updateHPDame() {
        List<Player> participants = new ArrayList<>();
        for (Zone zone : this.zones) {
            if (zone != null) {
                List<Player> players = zone.getPlayers();
                if (players != null) {
                    for (Player pl : players) {
                        if (pl != null && !pl.isBoss && !participants.contains(pl)) {
                            participants.add(pl);
                        }
                    }
                }
            }
        }
        if (participants.isEmpty()) {
            return;
        }
        long[] stats = calculateAvgDameHp(participants);
        long avgDame = stats[0];
        long avgHp = stats[1];
        long n = stats[2];

        // Cập nhật quái còn sống
        for (Zone zone : this.zones) {
            int mapId = zone.map.mapId;
            int mobHp = getMobHp(mapId, avgDame);
            int mobDame = getMobDame(avgHp);

            for (Mob mob : zone.mobs) {
                if (mob.isDie()) {
                    continue;
                }
                mob.point.dame = mobDame;
                mob.point.maxHp = mobHp;
                mob.point.hp = Math.min(mob.point.hp, mob.point.maxHp);
                mob.setTiemNang();
            }
        }

        // Cập nhật Boss còn sống
        for (Boss boss : bosses) {
            if (boss == null || boss.isDie() || boss.zone == null || boss.zone.map == null) {
                continue;
            }
            int mapId = boss.zone.map.mapId;
            long bDame = 0;
            long bHp = 0;
            switch (mapId) {
                case 59 -> {
                    bHp = Math.min(avgDame * 60 * n, MAX_HP_CAP);
                    bDame = Math.min((avgHp * 6) / 100, MAX_DAME_CAP);
                }
                case 62 -> {
                    bHp = Math.min(avgDame * 90 * n, MAX_HP_CAP);
                    bDame = Math.min((avgHp * 8) / 100, MAX_DAME_CAP);
                }
                case 55 -> {
                    bHp = Math.min(avgDame * 120 * n, MAX_HP_CAP);
                    bDame = Math.min((avgHp * 10) / 100, MAX_DAME_CAP);
                }
                case 54 -> {
                    bHp = Math.min(avgDame * 150 * n, MAX_HP_CAP);
                    bDame = Math.min((avgHp * 12) / 100, MAX_DAME_CAP);
                    if (boss.id >= -14 && boss.id <= -9) {
                        bDame = Math.max(1, bDame / 10);
                        bHp = Math.max(1, bHp / 10);
                    }
                }
                case 57 -> {
                    bHp = Math.min(avgDame * 50 * n, MAX_HP_CAP);
                    bDame = Math.min((avgHp * 8) / 100, MAX_DAME_CAP);
                }
            }
            boss.nPoint.hpMax = (int) Math.max(1L, bHp);
            boss.nPoint.dame = (int) Math.max(1L, bDame);
            boss.nPoint.hp = Math.min(boss.nPoint.hp, boss.nPoint.hpMax);
        }
    }
}
