package nro.models.boss.Cell;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.player.Player;
import nro.models.services.PlayerService;
import nro.models.utils.Util;

public class SieuBoHung extends Boss {

    public boolean callCellCon;

    public SieuBoHung() throws Exception {
        super(BossID.SIEU_BO_HUNG, false, true, BossesData.SIEU_BO_HUNG_1, BossesData.SIEU_BO_HUNG_2);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Không có ai dám đấu tiếp sao? Thật tẻ nhạt, ta đi đây!";
    }

    @Override
    protected void resetBase() {
        super.resetBase();
        this.callCellCon = false;
    }

    public void callCellCon() {
        this.callCellCon = true;
        this.changeStatus(BossStatus.AFK);
        this.changeToTypeNonPK();
        this.recoverHP();
        if (this.bossAppearTogether != null && this.bossAppearTogether[this.currentLevel] != null) {
            for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                if (boss != null) {
                    boss.changeStatus(BossStatus.RESPAWN);
                }
            }
        }
    }

    public void recoverHP() {
        this.nPoint.hp = this.nPoint.hpMax;
        PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, 0);
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
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.prepareBom) {
            return 0;
        }

        // Đang ở trạng thái AFK (lũ Xên con đang đánh) thì miễn nhiễm sát thương
        if (this.bossStatus == BossStatus.AFK) {
            if (Util.isTrue(1, 10)) {
                this.chat("|-1|Hãy tiêu diệt 7 đứa con của ta trước đi!");
            }
            return 0;
        }

        // Kế thừa toàn bộ tính toán né đòn, trừ giáp, trừ khiên, trừ máu của Boss.java
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    @Override
    public void die(Player plKill) {
        if (this.currentLevel == 0) {
            // Khi ở Dạng 1 và hết máu lần 1 -> Triệu hồi 7 Xên Con
            if (!this.callCellCon) {
                this.callCellCon();
                return;
            }
        }
        // Cả Dạng 1 (lần 2) và Dạng 2 khi hết máu đều kích hoạt Nổ bom tự sát trước khi chết
        if (!this.prepareBom) {
            this.setBom(plKill);
            return;
        }
        super.die(plKill);
    }

    @Override
    public void leaveMap() {
        if (this.bossAppearTogether != null && this.currentLevel < this.bossAppearTogether.length
                && this.bossAppearTogether[this.currentLevel] != null) {
            for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                if (boss != null && boss.zone != null && !boss.isDie()) {
                    boss.leaveMap();
                }
            }
        }
        super.leaveMap();
    }

    @Override
    public void autoLeaveMap() {
        if (this.currentLevel > 0 && this.bossStatus == BossStatus.AFK) {
            this.changeStatus(BossStatus.ACTIVE);
        }
        super.autoLeaveMap();
    }
}
