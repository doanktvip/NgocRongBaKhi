package nro.models.boss.Frieza;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.ConstItem;
import nro.models.player.Player;

public class Fide extends Boss {

    public Fide() throws Exception {
        super(BossID.FIDE, false, true, BossesData.FIDE_DAI_CA_1, BossesData.FIDE_DAI_CA_2, BossesData.FIDE_DAI_CA_3);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Hừ, lũ hạ đẳng các ngươi không đáng để ta tốn thời gian! Ta đi đây!";
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        // Rơi ngẫu nhiên 1 trong 3 viên Ngọc Rồng (5 sao, 6 sao, 7 sao)
        this.drop(plKill).random(ConstItem.NGOC_RONG_5_SAO, ConstItem.NGOC_RONG_6_SAO, ConstItem.NGOC_RONG_7_SAO).rate(80).execute();
        this.rewardEventPoint(plKill, 1);
    }
}
