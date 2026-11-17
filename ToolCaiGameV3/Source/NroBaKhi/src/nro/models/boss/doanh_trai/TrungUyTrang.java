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
import nro.models.utils.Logger;

public class TrungUyTrang extends Boss {

    public TrungUyTrang(Zone zone, int dame, int hp) throws Exception {
        super(PHOBANDT, BossID.TRUNG_UY_TRANG, BossesData.TRUNG_UY_TRANG(dame, hp));
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
                .item(ConstItem.MANH_TRUNG_UY_TRANG, 1)
                .execute();
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 198, 456);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        Service.gI().setPos(this, 198, 456);
        if (this.zone != null) {
            this.zone.isTUTAlive = true;
        }
    }

    @Override
    public void attack() {
        try {
            Player playerAtt = getPlayerAttack();
            if (playerAtt == null || playerAtt.isDie() || playerAtt.location == null) {
                return;
            }
            if (playerAtt.location.x < 755 || playerAtt.location.x > 1060) {
                this.playerTarger = null; // Hủy target ngoài khu vực để tìm người chơi trong phòng
                return;
            }
            if (this.location.x < 775) {
                Service.gI().setPos(this, playerAtt.location.x, playerAtt.location.y);
            }
            super.attack();
        } catch (Exception ex) {
            Logger.logException(TrungUyTrang.class, ex);
        }
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.zone != null && (this.zone.isbulon1Alive || this.zone.isbulon2Alive)) {
            return 0;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
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
        if (this.zone != null) {
            this.zone.isTUTAlive = false;
        }
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        RedRibbonHQManager.gI().removeBoss(this);
        this.dispose();
    }
}
