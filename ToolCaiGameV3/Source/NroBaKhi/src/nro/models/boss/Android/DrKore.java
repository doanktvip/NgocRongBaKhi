package nro.models.boss.Android;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.models.services.PlayerService;
import nro.models.utils.Util;

public class DrKore extends Boss {

    public DrKore() throws Exception {
        super(BossID.DR_KORE, false, true, BossesData.DR_KORE);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Không có đối thủ để thí nghiệm sao? Thật lãng phí thời gian, rút lui!";
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
        this.drop(plKill).random(18, 19, 20).rate(80).execute();
        this.rewardEventPoint(plKill, 1);
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.bossStatus == BossStatus.AFK || this.bossStatus == BossStatus.CHAT_S) {
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
}
