package nro.models.map.phoban;

import nro.models.boss.khi_gas.DrLychee;
import nro.models.clan.Clan;
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

    public static final long POWER_CAN_GO_TO_KHI_GAS_HUY_DIET = 2000000000;
    public static final int AVAILABLE = 50;
    public static final int TIME_KHI_GAS_HUY_DIET = 1800000;
    public static final int N_PLAYER_CLAN = 0;
    private boolean callBoss;
    public boolean hatchiyatchDead;

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
                long bossDamage = (1000 * level);
                long bossMaxHealth = (15000000 * level);
                bossDamage = Math.min(bossDamage, 200000000L);
                bossMaxHealth = Math.min(bossMaxHealth, 2000000000L);
                bosses.add(new DrLychee(
                        getMapById(148),
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

        if (!kickout && (hatchiyatchDead || Util.canDoWithTime(lastTimeOpen, TIME_KHI_GAS_HUY_DIET - 60000))) {
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
        // Hồi sinh quái
        for (Zone zone : this.zones) {
            List<Mob> mobs = zone.mobs;
            for (int i = 0; i < mobs.size(); i++) {
                Mob mob = mobs.get(i);
                if (((i == 0) && zone.map.mapId == 147)
                        || ((i == 7) && zone.map.mapId == 149)
                        || ((i == 0) && zone.map.mapId == 151)
                        || ((i == 0) && zone.map.mapId == 152)
                        || ((i == 33) && zone.map.mapId == 152)) {

                    mob.lvMob = 1;
                    mob.point.dame = (int) Math.min((long) level * 31 * 5 * mob.tempId * 10, 2_147_483_647);
                    mob.point.maxHp = (int) Math.min((long) level * 3 * 6700 * mob.tempId * 10, 2_147_483_647);
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                } else {
                    mob.lvMob = mob.tempId == 76 ? 1 : 0;
                    mob.point.dame = (int) Math.min((long) level * 31 * 5 * mob.tempId, 2_147_483_647);
                    mob.point.maxHp = (int) Math.min((long) level * 5 * 45 * mob.tempId, 2_147_483_647);
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
