package nro.models.boss.Nappa;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.player.Player;

public class MapDauDinh extends Boss {

    public MapDauDinh() throws Exception {
        super(BossID.MAP_DAU_DINH, true, true, BossesData.MAP_DAU_DINH);
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
}
