package nro.models.boss.Android;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.consts.BossStatus;
import nro.models.boss.BossesData;
import nro.models.player.Player;
import nro.models.utils.Util;

public class Pic extends Boss {

    public Pic() throws Exception {
        super(BossID.PIC, false, true, BossesData.PIC);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Chờ mãi chẳng thấy ai, đi chỗ khác chơi thôi!";
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        this.drop(plKill).random(18, 19, 20).rate(80).execute();
        this.rewardEventPoint(plKill, 1);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        this.changeStatus(BossStatus.AFK);
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
