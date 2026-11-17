package nro.models.boss.doanh_trai;

import nro.models.boss.Boss;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.boss.Boss_Manager.RedRibbonHQManager;
import static nro.models.consts.BossType.PHOBANDT;
import nro.models.consts.ConstItem;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.Util;

public class NinjaClone extends Boss {

    private Boss boss;

    public NinjaClone(Zone zone, Boss boss, int dame, int hp, int id) throws Exception {
        super(PHOBANDT, id, BossesData.NINJA_CLONE(dame, hp));
        this.zone = zone;
        this.boss = boss;
        this.respawn(); // Khởi tạo initBase: hp = hpMax = hp, dame = dame, skills, calPoint
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        this.drop(plKill)
                .rate(50)
                .random(
                        ConstItem.NGOC_RONG_4_SAO,
                        ConstItem.NGOC_RONG_5_SAO,
                        ConstItem.NGOC_RONG_6_SAO,
                        ConstItem.NGOC_RONG_7_SAO
                )
                .execute();
    }

    @Override
    public void joinMap() {
        int x = this.boss.location.x + Util.nextInt(-150, 150);
        if (this.zone != null && this.zone.map != null) {
            x = Math.max(100, Math.min(this.zone.map.mapWidth - 100, x));
        }
        int y = (this.zone != null && this.zone.map != null) ? this.zone.map.yPhysicInTop(x, this.boss.location.y) : this.boss.location.y;
        ChangeMapService.gI().changeMap(this, this.zone, x, y);
        this.changeToTypePK();
        this.changeStatus(BossStatus.ACTIVE);
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        if (this.bossStatus == BossStatus.REST) {
            return;
        }
        if (this.boss != null && this.boss.clan != null && this.boss.clan.doanhTrai != null) {
            this.boss.clan.doanhTrai.bosses.remove(this);
        }
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.boss = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        RedRibbonHQManager.gI().removeBoss(this);
        this.dispose();
    }
}
