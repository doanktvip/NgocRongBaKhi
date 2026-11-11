package nro.models.boss.Android;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.models.services.PlayerService;
import nro.models.utils.Util;

public class Android19 extends Boss {

    public Android19() throws Exception {
        super(BossID.ANDROID_19, false, true, BossesData.ANDROID_19);
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
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.bossStatus == BossStatus.CHAT_S) {
            return 0;
        }
        if (plAtt != null && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
            switch (plAtt.playerSkill.skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                    return 0;
            }
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
