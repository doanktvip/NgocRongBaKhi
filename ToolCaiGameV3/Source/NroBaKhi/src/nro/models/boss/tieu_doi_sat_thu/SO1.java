package nro.models.boss.tieu_doi_sat_thu;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.consts.BossStatus;
import nro.models.boss.BossesData;
import nro.models.player.Player;

public class SO1 extends Boss {

    public SO1() throws Exception {
        super(BossID.SO_1, false, true, BossesData.SO_1);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Chờ mãi không có ai đến khiêu chiến! Tiểu Đội Sát Thủ rút lui!";
    }

    @Override
    public void joinMap() {
        super.joinMap();
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        // Rơi ngẫu nhiên 1 trong 3 viên Ngọc Rồng (5 sao, 6 sao, 7 sao)
        this.drop(plKill).random(18, 19, 20).rate(80).execute();
        this.rewardEventPoint(plKill, 1);
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.bossStatus == BossStatus.AFK || this.bossStatus == BossStatus.CHAT_S) {
            return 0;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    @Override
    public void doneChatE() {
        if (this.parentBoss != null && !this.parentBoss.isDie()) {
            this.parentBoss.wakeUp();
        }
    }
}
