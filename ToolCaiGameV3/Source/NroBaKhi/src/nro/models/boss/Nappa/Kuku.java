package nro.models.boss.Nappa;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.ConstItem;
import nro.models.player.Player;

public class Kuku extends Boss {

    public Kuku() throws Exception {
        super(BossID.KUKU, true, true, BossesData.KUKU);
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        this.drop(plKill).random(ConstItem.NGOC_RONG_5_SAO, ConstItem.NGOC_RONG_6_SAO, ConstItem.NGOC_RONG_7_SAO).rate(80).execute();
        this.rewardEventPoint(plKill, 1);
    }
}
