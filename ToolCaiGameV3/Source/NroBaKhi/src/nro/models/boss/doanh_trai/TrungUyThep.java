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
import nro.models.utils.Logger;
import nro.models.utils.SkillUtil;

/**
 * Boss Trung Úy Thép - Phó bản Doanh Trại Độc Nhãn (Tháp tầng 3 - Map 55)
 */
public class TrungUyThep extends Boss {

    public TrungUyThep(Zone zone, int dame, int hp) throws Exception {
        super(PHOBANDT, BossID.TRUNG_UY_THEP, BossesData.TRUNG_UY_THEP(dame, hp));
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
                .execute();
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (plAtt != null && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
                if (SkillUtil.isUseSkillDam(plAtt)) {
                    damage = damage / 10;
                }
            }
            return super.injured(plAtt, damage, piercing, isMobAttack);
        }
        return 0;
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 884, 312);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        Service.gI().setPos(this, 884, 312);
    }

    @Override
    public void attack() {
        try {
            Player playerAtt = getPlayerAttack();
            if (playerAtt == null || playerAtt.isDie() || playerAtt.location == null) {
                return;
            }
            if (playerAtt.location.x < 640 || playerAtt.location.x > 980) {
                this.playerTarger = null; // Hủy target ngoài khu vực để đổi mục tiêu sang người trong phòng
                return;
            }
            if (this.location.x < 640 || this.location.x > 980) {
                Service.gI().setPos(this, 884, 312);
            }
            super.attack();
        } catch (Exception ex) {
            Logger.logException(TrungUyThep.class, ex);
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
