package nro.models.map.phoban;

import nro.models.boss.Boss;
import nro.models.boss.ban_do_kho_bau.TrungUyXanhLo;
import nro.models.clan.Clan;
import nro.models.map.TrapMap;
import nro.models.map.Zone;
import nro.models.mob.Mob;
import nro.models.player.Player;
import nro.models.services.ItemTimeService;
import nro.models.map.service.MapService;
import nro.models.services.Service;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BanDoKhoBau extends AbstractClanDungeon {

    public static final long POWER_CAN_GO_TO_DBKB = 2000000000;
    public static final int AVAILABLE = 50;
    public static final int TIME_BAN_DO_KHO_BAU = 1800000;
    public static final int TIME_KICKOUT_BDKB = 60000;
    public static final int MAX_TIMES_PER_DAY = 3;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 100;
    public static final long TIME_RESET_BDKB = 86400000L; // 24 giờ kể từ lần mở đầu tiên
    public Boss boss;
    public boolean allCharactersDead;
    public boolean isBossKilled;
    private long lastTimeSendTextTime;

    public BanDoKhoBau(int id) {
        super(id);
    }

    @Override
    public void update() {
        if (!isOpened) {
            return;
        }
        if (isTimeExpired(TIME_BAN_DO_KHO_BAU)) {
            finish();
            dispose();
            return;
        }

        boolean allMobsDead = true;
        for (Zone zone : zones) {
            if (zone != null && zone.map != null && zone.map.mapId == 135) {
                for (Player pl : zone.getNotBosses()) {
                    if (pl != null) {
                        TrapMap trap = zone.isInTrap(pl);
                        if (trap != null) {
                            trap.doPlayer(pl);
                        }
                    }
                }
            }

            if (allMobsDead && zone != null && zone.mobs != null) {
                for (Mob mob : zone.mobs) {
                    if (mob != null && !mob.isDie()) {
                        allMobsDead = false;
                        break;
                    }
                }
            }
        }

        boolean allCleared = allMobsDead && this.isBossKilled;

        if (!kickout && (allCleared || Util.canDoWithTime(lastTimeOpen, TIME_BAN_DO_KHO_BAU - 60000))) {
            triggerKickout();
        }

        if (Util.canDoWithTime(lastTimeSendTextTime, 15000)) {
            sendTextTime();
            lastTimeSendTextTime = System.currentTimeMillis();
        }
    }

    public boolean isBossDead() {
        return this.isBossKilled;
    }

    public void triggerKickout() {
        if (!this.kickout) {
            this.kickout = true;
            this.isBossKilled = true;
            this.timeKickOut = System.currentTimeMillis();
            sendTextTime();
        }
    }

    /**
     * Khởi động trạng thái nội bộ của phòng phó bản
     */
    public void startRoom(Clan clan, byte level) {
        try {
            this.level = level;
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = clan;
            this.clan.BanDoKhoBau = this;
            this.clan.timeOpenBanDoKhoBau = this.lastTimeOpen;
            this.kickout = false;
            this.isOpened = true;
            this.allCharactersDead = false;
            this.isBossKilled = false;
            this.init();
            sendTextTime();
        } catch (Exception e) {
            nro.models.utils.Logger.logException(BanDoKhoBau.class, e);
            this.dispose();
        }
    }

    @Deprecated
    public void openBanDoKhoBau(Player plOpen, Clan clan, byte level) {
        startRoom(clan, level);
        if (plOpen != null) {
            ChangeMapService.gI().goToDBKB(plOpen);
        }
    }

    @Deprecated
    public void sendThanhTichBanDoKhoBau(Player pl) {
        TreasureUnderSeaService.gI().updateThanhTich(pl, this);
    }

    private void init() {
        double scale = (double) this.level / (double) MAX_LEVEL;
        for (Zone zone : this.zones) {
            for (TrapMap trap : zone.trapMaps) {
                trap.dame = (int) Math.min(Math.max(10_000L, (long) (10_000_000L * scale)), 10_000_000L);
            }

            if (zone.map.mapId == 135 || zone.map.mapId == 136 || zone.map.mapId == 137 || zone.map.mapId == 138) {
                List<Mob> mobs = zone.mobs;
                for (int i = 0; i < mobs.size(); i++) {
                    Mob mob = mobs.get(i);
                    boolean isBigMob = (mob.tempId == 71 || mob.tempId == 72); // 71: Vua Bạch Tuộc, 72: Rôbốt bảo vệ
                    boolean isSuperMob = (((i == 5 || i == 10) && zone.map.mapId == 135) || (i == 5 && zone.map.mapId == 136) || (i == 5 && zone.map.mapId == 137));

                    if (isBigMob) {
                        mob.lvMob = 1;
                        mob.point.dame = (int) Math.min(Math.max(100L, (long) (20_000_000L * scale)), 20_000_000L);
                        mob.point.maxHp = (int) Math.min(Math.max(1_000L, (long) (2_000_000_000L * scale)), 2_000_000_000L);
                    } else if (isSuperMob) {
                        mob.lvMob = 1;
                        mob.point.dame = (int) Math.min(Math.max(50L, (long) (10_000_000L * scale)), 10_000_000L);
                        mob.point.maxHp = (int) Math.min(Math.max(500L, (long) (1_000_000_000L * scale)), 1_000_000_000L);
                    } else {
                        mob.lvMob = 0;
                        mob.point.dame = (int) Math.min(Math.max(20L, (long) (5_000_000L * scale)), 5_000_000L);
                        mob.point.maxHp = (int) Math.min(Math.max(200L, (long) (500_000_000L * scale)), 500_000_000L);
                    }
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                }
            }

            if (zone.map.mapId == 137) {
                try {
                    long bossDamage = (long) (20_000_000L * scale);
                    long bossMaxHealth = (long) (2_000_000_000L * scale);
                    bossDamage = Math.max(1_000L, Math.min(bossDamage, 20_000_000L));
                    bossMaxHealth = Math.max(10_000L, Math.min(bossMaxHealth, 2_000_000_000L));
                    boss = new TrungUyXanhLo(
                            this,
                            zone,
                            level,
                            (int) bossDamage,
                            (int) bossMaxHealth
                    );
                    this.bosses.add(boss);
                } catch (Exception exception) {
                    nro.models.utils.Logger.logException(BanDoKhoBau.class, exception);
                }
            }
        }
        startDungeonThread("Bản Đồ Kho Báu");
    }

    @Override
    protected void kickOut(Player player) {
        if (player != null && player.zone != null && player.zone.map != null) {
            ItemTimeService.gI().removeTextBanDoKhoBau(player);
            if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1038);
            }
        }
    }

    @Override
    protected void sendTextTime() {
        for (Zone zone : this.zones) {
            if (zone != null) {
                List<Player> players = zone.getPlayers();
                if (players != null) {
                    for (Player pl : players) {
                        if (pl != null) {
                            if (this.kickout) {
                                ItemTimeService.gI().sendTextTimeSapBanDoKhoBau(pl);
                            } else {
                                ItemTimeService.gI().sendTextBanDoKhoBau(pl);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void removeTextTime() {
        for (Zone zone : this.zones) {
            if (zone != null) {
                List<Player> players = zone.getPlayers();
                if (players != null) {
                    for (Player pl : players) {
                        if (pl != null) {
                            ItemTimeService.gI().removeTextBanDoKhoBau(pl);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void unlinkClan() {
        if (this.clan != null) {
            this.clan.BanDoKhoBau = null;
        }
    }

    @Override
    protected void onPlayerFinish(Player player) {
        if (player != null && player.clan != null && player.clan.BanDoKhoBau == this) {
            TreasureUnderSeaService.gI().updateThanhTich(player, this);
        }
    }

    @Override
    protected void onResetExtraState() {
        this.allCharactersDead = false;
        this.isBossKilled = false;
        this.boss = null;
    }
}
