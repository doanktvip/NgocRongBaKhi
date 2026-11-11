package nro.models.boss.Black_Goku;

import nro.models.boss.Boss;
import nro.models.boss.BossesData;
import nro.models.boss.BossID;
import nro.models.consts.ConstTaskBadges;
import nro.models.player.Player;
import nro.models.task.BadgesTaskService;
import nro.models.utils.Util;

public class BlackGoku extends Boss {


    public BlackGoku() throws Exception {
        super(BossID.BLACK_GOKU, false, true, BossesData.BLACK_GOKU, BossesData.SUPER_BLACK_GOKU);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Lũ sâu bọ các ngươi không đáng để ta tốn thời gian! Hẹn gặp lại sau!";
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);

        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        this.rewardEventPoint(plKill, 10);

        // Rơi duy nhất đúng 1 món theo bảng tỉ lệ phân tầng (1% Thần Linh, 5% Đồ Sao, 94% Ngọc Rồng)
        this.drop(plKill).pickOne()
                .rate(1).equipment(13)
                .rate(5).equipment(9, 11).randomStar(100)
                .otherwise().random(15, 16, 17, 18, 19, 20, 992)
                .execute();
    }
}
