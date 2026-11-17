package nro.models.map;
import nro.models.player.Player;
import nro.models.services.PlayerService;
import nro.models.map.service.EffectMapService;
import nro.models.utils.Util;

public class TrapMap {

    public int x;
    public int y;
    public int w;
    public int h;
    public int effectId;
    public long dame;

    public void doPlayer(Player player) {
        if (this.effectId == 49) {
            if (player != null && !player.isDie() && player.nPoint != null && Util.canDoWithTime(player.idMark.getLastTimeAnXienTrapBDKB(), 1000)) {
                long damage = Math.max(1L, (long) player.nPoint.hpMax * 10 / 100);
                player.injured(null, damage, true, false);
                if (player.isDie()) {
                    player.setDie();
                }
                PlayerService.gI().sendInfoHp(player);
                EffectMapService.gI().sendEffectMapToAllInMap(player.zone, effectId, 2, 1, player.location.x - 32, 1040, 1);
                player.idMark.setLastTimeAnXienTrapBDKB(System.currentTimeMillis());
            }
        }
    }

}
