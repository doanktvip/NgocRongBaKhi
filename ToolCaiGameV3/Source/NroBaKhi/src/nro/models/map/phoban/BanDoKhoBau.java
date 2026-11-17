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
    public static final int MAX_TIMES_PER_DAY = 3;
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 100;
    public static final long TIME_RESET_BDKB = 86400000L; // 24 giờ kể từ lần mở đầu tiên
    public Boss boss;
    public boolean allCharactersDead;

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
        }
        allCharactersDead = this.areAllCharactersDead();

        if (!kickout && (allCharactersDead || Util.canDoWithTime(lastTimeOpen, TIME_BAN_DO_KHO_BAU - 60000))) {
            kickout = true;
            timeKickOut = System.currentTimeMillis();
        }

        if (kickout && Util.canDoWithTime(lastTimeSendNotify, 10000)) {
            for (Zone zone : zones) {
                if (zone != null) {
                    List<Player> players = zone.getPlayers();
                    if (players != null) {
                        for (Player pl : players) {
                            if (pl != null) {
                                Service.gI().sendThongBao(pl, "Cái hang này sắp sập rồi, chúng ta phải rời khỏi đây ngay " + TimeUtil.getTimeLeft(timeKickOut, 60) + " nữa");
                            }
                        }
                    }
                }
            }
            lastTimeSendNotify = System.currentTimeMillis();
        }
    }

    public void openBanDoKhoBau(Player plOpen, Clan clan, byte level) {
        try {
            this.level = level;
            this.lastTimeOpen = plOpen.lastTimeJoinBDKB;
            this.clan = clan;
            this.clan.lastTimeOpenBanDoKhoBau = this.lastTimeOpen;
            this.clan.playerOpenBanDoKhoBau = plOpen;
            this.clan.BanDoKhoBau = this;
            this.kickout = false;
            this.isOpened = true;
            this.allCharactersDead = false;
            this.init();
            ChangeMapService.gI().goToDBKB(plOpen);
            sendTextTime();
        } catch (Exception e) {
            nro.models.utils.Logger.logException(BanDoKhoBau.class, e);
            if (plOpen != null && plOpen.clan != null) {
                plOpen.clan.lastTimeOpenBanDoKhoBau = 0;
            }
            this.dispose();
        }
    }

    public void sendThanhTichBanDoKhoBau(Player pl) {
        if (pl == null || pl.clan == null || pl.clan.BanDoKhoBau != this) {
            return;
        }

        long timeDone = System.currentTimeMillis() - pl.clan.lastTimeOpenBanDoKhoBau;
        int levelDone = pl.clan.BanDoKhoBau.level;
        if (levelDone > pl.clan.levelDoneBanDoKhoBau) {
            pl.clan.levelDoneBanDoKhoBau = levelDone;
            pl.clan.thoiGianHoanThanhBDKB = timeDone;
        } else if (levelDone == pl.clan.levelDoneBanDoKhoBau
                && timeDone < pl.clan.thoiGianHoanThanhBDKB) {
            pl.clan.thoiGianHoanThanhBDKB = timeDone;
        }

        pl.clan.updatethanhTichBDKB(pl.clan.id);
        pl.clan.updatethanhTichBDKBForLeader();
        pl.clan.updateThongTinLeader(pl.clan.id);
    }

    private void init() {
        // Hồi sinh quái và khởi tạo độ khó Hardcore theo cấp độ 1-100
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

            if (zone.map.mapId == 138) {
                try {
                    long bossDamage = (long) (20_000_000L * scale);
                    long bossMaxHealth = (long) (2_000_000_000L * scale);
                    bossDamage = Math.max(1_000L, Math.min(bossDamage, 20_000_000L));
                    bossMaxHealth = Math.max(10_000L, Math.min(bossMaxHealth, 2_000_000_000L));
                    boss = new TrungUyXanhLo(
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
            if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1038);
            }
        }
    }

    @Override
    protected void sendTextTime() {
        if (this.clan != null && this.clan.membersInGame != null) {
            for (Player pl : this.clan.membersInGame) {
                if (pl != null) {
                    ItemTimeService.gI().sendTextBanDoKhoBau(pl);
                }
            }
        }
    }

    @Override
    protected void removeTextTime() {
        if (this.clan != null && this.clan.membersInGame != null) {
            for (Player pl : this.clan.membersInGame) {
                if (pl != null) {
                    ItemTimeService.gI().removeTextBanDoKhoBau(pl);
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
            sendThanhTichBanDoKhoBau(player);
        }
    }

    @Override
    protected void onResetExtraState() {
        this.allCharactersDead = false;
        this.boss = null;
    }
}
