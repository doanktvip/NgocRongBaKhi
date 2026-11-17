package nro.models.boss.ban_do_kho_bau;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.consts.ConstItem;
import nro.models.consts.ConstPlayer;
import nro.models.boss.Boss_Manager.TreasureUnderSeaManager;
import static nro.models.consts.BossType.PHOBANBDKB;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.player.DropItem;
import nro.models.player.Player;
import nro.models.services.Service;
import nro.models.services.TaskService;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.Util;

public class TrungUyXanhLo extends Boss {

    private BanDoKhoBau bdkb;
    private int level;

    public TrungUyXanhLo(BanDoKhoBau bdkb, Zone zone, int level, int dame, int hp) throws Exception {
        super(PHOBANBDKB, BossID.TRUNG_UY_XANH_LO, BossesData.TRUNG_UY_XANH_LO_BDKB(dame, hp));
        this.bdkb = bdkb;
        this.zone = zone;
        this.level = level;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);

        int soThoiVang = Math.min(20, Math.max(1, (int) Math.round(this.level * 20.0 / 100.0)));
        DropItem.DropBuilder dropGold = this.drop(plKill);
        for (int i = 0; i < soThoiVang; i++) {
            dropGold.item(ConstItem.THOI_VANG, 1);
        }
        dropGold.executeAll();

        int rateThanLinh = Math.min(10, Math.max(1, (int) Math.round(this.level / 10.0)));
        this.drop(plKill).thanLinh().rate(rateThanLinh).execute();

        int w1s = Math.max(1, this.level * 2);
        int w2s = Math.max(2, this.level * 3);
        int w3s = Math.max(3, this.level * 4);
        int w4s = 150;
        int w5s = Math.max(10, 300 - this.level * 2);
        int w6s = Math.max(10, 400 - this.level * 3);
        int w7s = Math.max(10, 500 - this.level * 4);

        int[][] dragonBallWeights = new int[][]{
            {ConstItem.NGOC_RONG_1_SAO, w1s},
            {ConstItem.NGOC_RONG_2_SAO, w2s},
            {ConstItem.NGOC_RONG_3_SAO, w3s},
            {ConstItem.NGOC_RONG_4_SAO, w4s},
            {ConstItem.NGOC_RONG_5_SAO, w5s},
            {ConstItem.NGOC_RONG_6_SAO, w6s},
            {ConstItem.NGOC_RONG_7_SAO, w7s}
        };
        this.drop(plKill).weighted(dragonBallWeights).execute();
        }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 198, 456);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.AFK);
        Service.gI().setPos(this, 198, 456);
    }

    @Override
    public void afk() {
        Player pl = getPlayerAttack();
        if (pl == null || pl.isDie()) {
            return;
        }
        if (Util.getDistance(this, pl) <= 200) {
            this.changeStatus(BossStatus.ACTIVE);
        }
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        if (this.bdkb != null) {
            this.bdkb.isBossKilled = true;
        }
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        TreasureUnderSeaManager.gI().removeBoss(this);
        this.dispose();
    }
}
