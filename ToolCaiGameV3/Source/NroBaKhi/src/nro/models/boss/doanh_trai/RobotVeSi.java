package nro.models.boss.doanh_trai;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.boss.Boss_Manager.RedRibbonHQManager;
import static nro.models.consts.BossType.PHOBANDT;
import nro.models.consts.ConstItem;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.Util;

public class RobotVeSi extends Boss {

    private final int robotId;

    public RobotVeSi(Zone zone, int id, int dame, int hp) throws Exception {
        super(PHOBANDT, BossID.ROBOT_VE_SI - id, BossesData.ROBOT_VE_SI(id, dame, hp));
        this.zone = zone;
        this.robotId = id;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        this.drop(plKill)
                .random(
                        ConstItem.NGOC_RONG_4_SAO,
                        ConstItem.NGOC_RONG_5_SAO,
                        ConstItem.NGOC_RONG_6_SAO,
                        ConstItem.NGOC_RONG_7_SAO
                )
                .execute();
    }

    @Override
    public void joinMap() {
        int spawnX = 220 + this.robotId * 50;
        ChangeMapService.gI().changeMap(this, this.zone, spawnX, 312);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.AFK);
        int spawnX = 220 + this.robotId * 50;
        Service.gI().setPos(this, spawnX, 312);
    }

    @Override
    public void afk() {
        Player pl = getPlayerAttack();
        if (pl == null || pl.isDie() || pl.location == null) {
            return;
        }
        Service.gI().setPos(this, pl.location.x + Util.nextInt(-100, 100), 0);
        this.changeStatus(BossStatus.ACTIVE);
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        RedRibbonHQManager.gI().removeBoss(this);
        this.dispose();
    }
}
