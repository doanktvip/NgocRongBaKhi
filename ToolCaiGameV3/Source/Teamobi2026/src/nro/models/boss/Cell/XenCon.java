package nro.models.boss.Cell;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.consts.BossStatus;
import nro.models.player.Player;

public class XenCon extends Boss {
    
    public XenCon(int bossId, BossData data) throws Exception {
        super(bossId, data);
    }

    @Override
    public void initBase() {
        super.initBase();
        int index = Math.abs((int) this.id) - 101;
        this.name = "Xên con " + index;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        this.drop(plKill).random(17, 18, 19, 20).rate(80).execute();
        this.rewardEventPoint(plKill, 1);
    }

    @Override
    public void joinMap() {
        if (this.parentBoss != null) {
            this.zone = this.parentBoss.zone; // Gán đè thẳng khu mới nhất của mẹ
        }
        super.joinMap();
    }
    
    @Override
    public void doneChatE() {
        if (this.parentBoss == null || this.parentBoss.bossAppearTogether == null
                || this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss != this && !boss.isDie()) {
                return;
            }
        }
        this.parentBoss.changeToTypePK();
        this.parentBoss.changeStatus(BossStatus.ACTIVE);
        this.parentBoss.chat("|-1|Lũ vô dụng! Cuối cùng vẫn phải để tự tay ta giải quyết các ngươi!");
    }
}
