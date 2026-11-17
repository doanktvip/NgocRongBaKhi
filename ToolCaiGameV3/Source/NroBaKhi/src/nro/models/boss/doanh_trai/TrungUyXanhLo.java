package nro.models.boss.doanh_trai;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.consts.ConstItem;
import nro.models.boss.Boss_Manager.RedRibbonHQManager;
import static nro.models.consts.BossType.PHOBANDT;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.Util;

public class TrungUyXanhLo extends Boss {

    public TrungUyXanhLo(Zone zone, int dame, int hp) throws Exception {
        super(PHOBANDT, BossID.TRUNG_UY_XANH_LO, BossesData.TRUNG_UY_XANH_LO(dame, hp));
        this.zone = zone;
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
                .item(ConstItem.MANH_TRUNG_UY_XANH_LO, 1)
                .item(ConstItem.BAN_DO_KHO_BAU, 1)
                .execute();
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 1210, 384);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.AFK);
        Service.gI().setPos(this, 1210, 384);
    }

    @Override
    public void afk() {
        Player pl = getPlayerAttack();
        if (pl == null || pl.isDie() || pl.location == null) {
            return;
        }
        if (Util.getDistance(this, pl) <= 500) {
            this.changeStatus(BossStatus.ACTIVE);
        }
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
