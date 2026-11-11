package nro.models.boss.tieu_doi_sat_thu_namek;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.player.DropItem.DropBuilder;
import nro.models.player.Player;
import nro.models.utils.Util;

public class SO2_NM extends Boss {

    public SO2_NM() throws Exception {
        super(BossID.SO_2_NM, false, true, BossesData.SO_2_NM);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Chờ mãi không có ai đến khiêu chiến! Tiểu Đội Sát Thủ Namek rút lui!";
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

        // Rơi các cọc Ngọc Hồng (ID 861) và ngẫu nhiên 1 viên Ngọc Rồng tỏa cánh sen
        DropBuilder drop = this.drop(plKill);
        int piles = Util.nextInt(5, 10);
        for (int i = 0; i < piles; i++) {
            drop.item(861, Util.nextInt(1, 3));
        }
        drop.random(18, 19, 20).execute();

        // Cộng 5 điểm sự kiện và thông báo
        this.rewardEventPoint(plKill, 5);
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
        if (this.parentBoss == null || this.parentBoss.bossAppearTogether == null
                || this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss.id == BossID.SO_1_NM && !boss.isDie()) {
                boss.wakeUp();
                break;
            }
        }
    }
}
