package nro.models.boss.Android;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.consts.BossStatus;
import nro.models.boss.BossesData;
import nro.models.player.Player;
import nro.models.utils.Util;

public class Poc extends Boss {

    public Poc() throws Exception {
        super(BossID.POC, false, true, BossesData.POC);
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
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.bossStatus == BossStatus.CHAT_S) {
            return 0;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    @Override
    public void doneChatE() {
        if (this.parentBoss == null || this.parentBoss.bossAppearTogether == null
                || this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss.id == BossID.PIC && !boss.isDie()) {
                boss.wakeUp();
                break;
            }
        }
    }
}
