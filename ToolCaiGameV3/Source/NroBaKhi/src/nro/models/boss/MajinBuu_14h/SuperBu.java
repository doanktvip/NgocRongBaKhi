package nro.models.boss.MajinBuu_14h;


import nro.models.boss.Boss;
import nro.models.boss.Boss_Manager.FinalBossManager;
import nro.models.boss.BossID;
import nro.models.consts.BossStatus;
import nro.models.boss.BossesData;
import static nro.models.consts.BossType.FINAL;
import java.util.ArrayList;
import java.util.List;
import nro.models.player.Player;
import nro.models.services.EffectSkillService;
import nro.models.services.Service;
import nro.models.utils.Util;
import nro.models.server.ServerNotify;
import nro.models.services.SkillService;
import nro.models.services.TaskService;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.SkillUtil;

public class SuperBu extends Boss {

    private long lastTimeUseSkill;
    private long timeUseSkill;

    public SuperBu() throws Exception {
        super(FINAL, BossID.SUPERBU, BossesData.SUPER_BU_BUNG);
        this.currentLevel = 0;
        this.initBase();
    }

    @Override
    public void joinMap() {
        if (this.currentLevel < 0) {
            this.currentLevel = 0;
            this.initBase();
        }
        if (zoneFinal != null) {
            this.zone = zoneFinal;
        }
        if (this.zone != null) {
            int x = this.zone.map.mapWidth > 100 ? Util.nextInt(100, this.zone.map.mapWidth - 100) : Util.nextInt(100);
            int y = this.zone.map.yPhysicInTop(x, 100);
            ChangeMapService.gI().changeMap(this, this.zone, x, y);
            this.changeStatus(BossStatus.ACTIVE);
            this.changeToTypePK();
            this.lastTimeAttack = System.currentTimeMillis();
        }
    }

    @Override
    public void joinMapByZone(nro.models.map.Zone zone) {
        this.zoneFinal = zone;
        this.joinMap();
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                if (Util.canDoWithTime(lastTimeUseSkill, timeUseSkill)) {
                    Service.gI().sendMabuAttackSkill(this);
                    lastTimeUseSkill = System.currentTimeMillis();
                    timeUseSkill = Util.nextInt(5000, 10000);
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)), pl.location.y);
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)), pl.location.y);
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void reward(Player plKill) {
        int diem = 5;
        plKill.event.addEventPoint(diem);
        Service.gI().sendThongBao(plKill, "+5 Point");
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        Boss boss = FinalBossManager.gI().getBossById(BossID.MABU, 127, this.zone.zoneId);
        if (boss != null) {
            boss.injured(plAtt, damage, piercing, isMobAttack);
        }
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(10, 100)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (damage >= 30000000) {
                damage = 30000000 + Util.nextInt(-10000, 10000);
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return (int) damage;
        } else {
            return 0;
        }
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
            ServerNotify.gI().notify(plKill.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
        }
        if (this.zone != null) {
            List<Player> playersInZone = new ArrayList<>(this.zone.getPlayers());
            for (Player pl : playersInZone) {
                if (pl != null && pl.zone != null && pl.zone.map.mapId == 128) {
                    ChangeMapService.gI().changeMap(pl, 127, this.zone.zoneId, -1, 312);
                }
            }
            Boss boss = FinalBossManager.gI().getBossById(BossID.MABU, 127, this.zone.zoneId);
            if (boss instanceof Mabu2H) {
                ((Mabu2H) boss).maBuEat.clear();
            }
        }
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void rest() {
        // Không tự động hồi sinh, chỉ sinh ra khi có người bị nuốt vào bụng
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.zone = null;
        FinalBossManager.gI().removeBoss(this);
    }

}
