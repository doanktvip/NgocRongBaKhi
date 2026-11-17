package nro.models.map.phoban;

import nro.models.boss.khi_gas.DrLychee;
import nro.models.clan.Clan;
import nro.models.consts.ConstMob;
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
public class DestronGas extends AbstractClanDungeon {

    // ==========================================
    // CẤU HÌNH CẤP ĐỘ PHÓ BẢN
    // ==========================================
    public static final int MIN_LEVEL = 1;
    public static final int MAX_LEVEL = 100;

    // ==========================================
    // CẤU HÌNH THỜI GIAN & GIỚI HẠN
    // ==========================================
    public static final long POWER_CAN_GO_TO_KHI_GAS_HUY_DIET = 2_000_000_000L;
    public static final int AVAILABLE = 50;
    public static final int TIME_KHI_GAS_HUY_DIET = 1_800_000; // 30 phút
    public static final int TIME_KICKOUT_KGHD = 60_000; // 60 giây
    public static final int MAX_TIMES_PER_DAY = 3;
    public static final int N_PLAYER_CLAN = 0;
    public static final int N_DAY_JOIN_CLAN = 0;
    public static final int MAP_BOSS = 148;

    private boolean callBoss;
    public boolean hatchiyatchDead;
    private long lastTimeSendTextTime;

    public DestronGas(int id) {
        super(id);
    }

    @Override
    public void update() {
        if (!isOpened) {
            return;
        }
        if (isTimeExpired(TIME_KHI_GAS_HUY_DIET)) {
            finish();
            dispose();
            return;
        }

        boolean allMobsDead = this.areAllMobsDead();

        if (allMobsDead && !callBoss) {
            try {
                double scale = (double) this.level / (double) MAX_LEVEL;
                long bossDamage = (long) (20_000_000L * scale);
                long bossMaxHealth = (long) (2_000_000_000L * scale);
                bossDamage = Math.max(1_000L, Math.min(bossDamage, 20_000_000L));
                bossMaxHealth = Math.max(10_000L, Math.min(bossMaxHealth, 2_000_000_000L));
                bosses.add(new DrLychee(
                        getMapById(MAP_BOSS),
                        clan,
                        level,
                        (int) bossDamage,
                        (int) bossMaxHealth
                ));
                callBoss = true;
            } catch (Exception exception) {
                nro.models.utils.Logger.logException(DestronGas.class, exception);
            }
        }

        if (!kickout && (hatchiyatchDead || Util.canDoWithTime(lastTimeOpen, TIME_KHI_GAS_HUY_DIET - TIME_KICKOUT_KGHD))) {
            kickout = true;
            timeKickOut = System.currentTimeMillis();
            for (Zone zone : zones) {
                if (zone != null) {
                    List<Player> players = zone.getPlayers();
                    if (players != null) {
                        for (Player pl : players) {
                            if (pl != null) {
                                Service.gI().sendThongBao(pl, "Nơi này sắp nổ tung mau chạy đi");
                            }
                        }
                    }
                }
            }
            sendTextTime();
        }
        if (kickout && Util.canDoWithTime(lastTimeSendNotify, 10000)) {
            lastTimeSendNotify = System.currentTimeMillis();
            for (Zone zone : zones) {
                if (zone != null) {
                    List<Player> players = zone.getPlayers();
                    if (players != null) {
                        for (Player pl : players) {
                            if (pl != null) {
                                Service.gI().sendThongBao(pl, "Về làng Aru sau " + TimeUtil.getTimeLeft(timeKickOut, 60) + " nữa");
                            }
                        }
                    }
                }
            }
        }

        if (Util.canDoWithTime(lastTimeSendTextTime, 15000)) {
            sendTextTime();
            lastTimeSendTextTime = System.currentTimeMillis();
        }
    }

    public void openKhiGasHuyDiet(Player plOpen, Clan clan, byte level) {
        try {
            this.level = level;
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = clan;
            this.clan.lastTimeOpenKhiGasHuyDiet = this.lastTimeOpen;
            this.clan.playerOpenKhiGasHuyDiet = plOpen;
            this.clan.KhiGasHuyDiet = this;
            this.callBoss = false;
            this.kickout = false;
            this.isOpened = true;
            this.init();
            sendTextTime();
        } catch (Exception e) {
            nro.models.utils.Logger.logException(DestronGas.class, e);
            if (plOpen != null && plOpen.clan != null) {
                plOpen.clan.lastTimeOpenKhiGasHuyDiet = 0;
            }
            this.dispose();
        }
    }

    public void sendThanhTichKhiGas(Player pl) {
        if (pl == null || pl.clan == null || pl.clan.KhiGasHuyDiet != this) {
            return;
        }
        long timeDoneKhiGas = System.currentTimeMillis() - pl.clan.lastTimeOpenKhiGasHuyDiet;
        int levelDoneKhiGas = pl.clan.KhiGasHuyDiet.level;

        if (levelDoneKhiGas > pl.clan.levelDoneKhiGas) {
            pl.clan.levelDoneKhiGas = levelDoneKhiGas;
            pl.clan.thoiGianHoanThanhKhiGas = timeDoneKhiGas;
        } else if (levelDoneKhiGas == pl.clan.levelDoneKhiGas) {
            if (timeDoneKhiGas < pl.clan.thoiGianHoanThanhKhiGas) {
                pl.clan.thoiGianHoanThanhKhiGas = timeDoneKhiGas;
            }
        }

        if (levelDoneKhiGas >= 70) {
            pl.destronGas70CompletionCount++;
        }

        pl.clan.updatethanhTichKhiGasForLeader();
    }

    private void init() {
        double scale = (double) this.level / (double) MAX_LEVEL;
        for (Zone zone : this.zones) {
            if (MapService.gI().isMapKhiGasHuyDiet(zone.map.mapId)) {
                List<Mob> mobs = zone.mobs;
                for (int i = 0; i < mobs.size(); i++) {
                    Mob mob = mobs.get(i);
                    boolean isBigMob = (mob.tempId == ConstMob.CO_MAY_HUY_DIET);
                    boolean isSuperMob = ((i == 0 && zone.map.mapId == 147)
                            || (i == 7 && zone.map.mapId == 149)
                            || (i == 0 && zone.map.mapId == 151)
                            || (i == 0 && zone.map.mapId == 152)
                            || (i == 33 && zone.map.mapId == 152));

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
        }
        startDungeonThread("Khí Gas Hủy Diệt");
    }

    @Override
    protected void kickOut(Player player) {
        if (player != null && player.zone != null && player.zone.map != null) {
            if (MapService.gI().isMapKhiGasHuyDiet(player.zone.map.mapId)) {
                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, -1);
            }
        }
    }

    @Override
    protected void sendTextTime() {
        if (this.clan != null && this.clan.membersInGame != null) {
            for (Player pl : this.clan.membersInGame) {
                if (pl != null) {
                    ItemTimeService.gI().sendTextKhiGasHuyDiet(pl);
                }
            }
        }
    }

    @Override
    protected void removeTextTime() {
        if (this.clan != null && this.clan.membersInGame != null) {
            for (Player pl : this.clan.membersInGame) {
                if (pl != null) {
                    ItemTimeService.gI().removeTextKhiGasHuyDiet(pl);
                }
            }
        }
    }

    @Override
    protected void unlinkClan() {
        if (this.clan != null) {
            this.clan.KhiGasHuyDiet = null;
        }
    }

    @Override
    protected void onPlayerFinish(Player player) {
        sendThanhTichKhiGas(player);
    }

    @Override
    protected void onResetExtraState() {
        this.hatchiyatchDead = false;
        this.callBoss = false;
    }
}
