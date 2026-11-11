package nro.models.boss.tieu_doi_sat_thu;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.consts.BossStatus;
import nro.models.boss.BossesData;
import nro.models.player.Player;
import nro.models.services.EffectSkillService;
import nro.models.utils.Util;

public class TDT extends Boss {

    private long lastBodyChangeTime;

    public TDT() throws Exception {
        super(BossID.TIEU_DOI_TRUONG, false, true, BossesData.TIEU_DOI_TRUONG);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Chờ mãi không có ai đến khiêu chiến! Tiểu Đội Sát Thủ rút lui!";
    }

    @Override
    public void joinMap() {
        super.joinMap();
        this.changeStatus(BossStatus.AFK);
    }

    private void bodyChangePlayerInMap() {
        if (this.zone != null) {
            for (Player pl : this.zone.getPlayers()) {
                if (Util.isTrue(5, 10) && pl.effectSkill != null && !pl.effectSkill.isBodyChangeTechnique) {
                    EffectSkillService.gI().setIsBodyChangeTechnique(pl);
                }
            }
        }
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
    public void attack() {
        if (Util.canDoWithTime(lastBodyChangeTime, 10000)) {
            bodyChangePlayerInMap();
            this.chat("Úm ba la xì bùa");
            this.lastBodyChangeTime = System.currentTimeMillis();
        }
        super.attack();
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.bossStatus == BossStatus.AFK || this.bossStatus == BossStatus.CHAT_S) {
            return 0;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }
}
