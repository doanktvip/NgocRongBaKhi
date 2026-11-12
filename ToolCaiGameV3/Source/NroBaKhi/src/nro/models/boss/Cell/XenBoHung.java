package nro.models.boss.Cell;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.ConstTaskBadges;
import nro.models.player.Player;
import nro.models.task.BadgesTaskService;

public class XenBoHung extends Boss {

    public XenBoHung() throws Exception {
        super(BossID.XEN_BO_HUNG, false, true, BossesData.XEN_BO_HUNG_1, BossesData.XEN_BO_HUNG_2, BossesData.XEN_BO_HUNG_3);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Chẳng có ai đủ tư cách đấu với ta! Ta đi tìm kẻ mạnh hơn đây!";
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        // BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        // Rơi 1 viên Ngọc Rồng 4sao
        this.drop(plKill).item(17, 1).execute();
        this.rewardEventPoint(plKill, 3);
    }
}
