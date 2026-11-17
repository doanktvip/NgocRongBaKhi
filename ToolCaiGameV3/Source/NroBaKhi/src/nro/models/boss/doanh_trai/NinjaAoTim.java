package nro.models.boss.doanh_trai;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.consts.BossStatus;
import nro.models.boss.Boss_Manager.RedRibbonHQManager;
import static nro.models.consts.BossType.PHOBANDT;
import nro.models.clan.Clan;
import nro.models.consts.ConstItem;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.services.PlayerService;
import nro.models.services.Service;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.Util;
import nro.models.network.Message;

public class NinjaAoTim extends Boss {

    private boolean calledNinja;
    private final List<NinjaClone> clones = new CopyOnWriteArrayList<>();

    public NinjaAoTim(Zone zone, Clan clan, int dame, int hp) throws Exception {
        super(PHOBANDT, BossID.NINJA_AO_TIM, BossesData.NINJA_AO_TIM(dame, hp));
        this.zone = zone;
        this.clan = clan;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        this.drop(plKill)
                .random(
                        ConstItem.NGOC_RONG_4_SAO,
                        ConstItem.NGOC_RONG_5_SAO,
                        ConstItem.NGOC_RONG_6_SAO,
                        ConstItem.NGOC_RONG_7_SAO
                )
                .item(ConstItem.MANH_NINJA_AO_TIM, 1)
                .execute();
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 190, 312);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        Service.gI().setPos(this, 190, 312);
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            int halfHp = this.nPoint.hpMax / 2;
            if (!this.calledNinja) {
                if (this.nPoint.hp <= halfHp || this.nPoint.hp - damage <= halfHp) {
                    callNinjaClones();
                    return 0;
                }
            }

            NinjaClone aliveClone = getRandomAliveClone();
            if (aliveClone != null && Util.isTrue(50, 100)) {
                this.chat("Đó chỉ là tàn ảnh thôi!");
                swapPosition(aliveClone);
                return aliveClone.injured(plAtt, damage, piercing, isMobAttack);
            }

            return super.injured(plAtt, damage, piercing, isMobAttack);
        }
        return 0;
    }

    private void sendExitMap(Player player) {
        if (player == null || player.zone == null) {
            return;
        }
        Message msg = null;
        try {
            msg = new Message(-6);
            msg.writer().writeInt((int) player.id);
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
        } catch (Exception e) {
            nro.models.utils.Logger.logException(NinjaAoTim.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void callNinjaClones() {
        this.calledNinja = true;
        this.nPoint.hp = this.nPoint.hpMax;
        PlayerService.gI().sendInfoHp(this);
        this.chat("Phân thân chi thuật!");

        if (this.zone == null) {
            return;
        }

        int origX = this.location.x;
        int origY = this.location.y;

        sendExitMap(this);

        int count = Util.nextInt(4, 6);
        int hpClone = Math.max(1, this.nPoint.hpMax / 10);
        int dameClone = Math.max(1, this.nPoint.dame / 10);
        int[] cloneIds = {
            BossID.NINJA_AO_TIM1,
            BossID.NINJA_AO_TIM2,
            BossID.NINJA_AO_TIM3,
            BossID.NINJA_AO_TIM4,
            BossID.NINJA_AO_TIM5,
            BossID.NINJA_AO_TIM6
        };

        if (this.clan != null && this.clan.doanhTrai != null) {
            for (int i = 0; i < count; i++) {
                try {
                    NinjaClone clone = new NinjaClone(this.zone, this, dameClone, hpClone, cloneIds[i]);
                    if (i == 0) {
                        ChangeMapService.gI().changeMap(clone, this.zone, origX, origY);
                        clone.changeToTypePK();
                        clone.changeStatus(BossStatus.ACTIVE);
                    } else {
                        clone.joinMap();
                    }
                    this.clones.add(clone);
                    clan.doanhTrai.bosses.add(clone);
                } catch (Exception ex) {
                    nro.models.utils.Logger.logException(NinjaAoTim.class, ex);
                }
            }
        }

        int newX = origX + Util.getOne(-1, 1) * Util.nextInt(120, 220);
        if (this.zone.map != null) {
            newX = Math.max(100, Math.min(this.zone.map.mapWidth - 100, newX));
            this.location.y = this.zone.map.yPhysicInTop(newX, origY);
        }
        this.location.x = newX;
        this.zone.load_Me_To_Another(this);
    }

    private NinjaClone getRandomAliveClone() {
        if (this.clones.isEmpty()) {
            return null;
        }
        int countAlive = 0;
        for (int i = 0; i < this.clones.size(); i++) {
            NinjaClone c = this.clones.get(i);
            if (c != null && !c.isDie() && c.zone != null && c.zone.equals(this.zone) && c.location.x > 0) {
                countAlive++;
            }
        }
        if (countAlive == 0) {
            return null;
        }
        int target = Util.nextInt(0, countAlive - 1);
        int cur = 0;
        for (int i = 0; i < this.clones.size(); i++) {
            NinjaClone c = this.clones.get(i);
            if (c != null && !c.isDie() && c.zone != null && c.zone.equals(this.zone) && c.location.x > 0) {
                if (cur == target) {
                    return c;
                }
                cur++;
            }
        }
        return null;
    }

    private void swapPosition(NinjaClone clone) {
        if (clone == null || this.zone == null || clone.zone == null) {
            return;
        }
        if (this.location.x <= 0 || clone.location.x <= 0) {
            return;
        }
        int realX = this.location.x;
        int realY = this.location.y;
        int cloneX = clone.location.x;
        int cloneY = clone.location.y;

        sendExitMap(this);
        Service.gI().setPos(clone, realX, realY);
        this.location.x = cloneX;
        this.location.y = cloneY;
        this.zone.load_Me_To_Another(this);
    }

    @Override
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }
        for (NinjaClone clone : this.clones) {
            if (clone != null && !clone.isDie()) {
                clone.leaveMap();
            }
        }
        this.clones.clear();
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        for (NinjaClone clone : this.clones) {
            if (clone != null && !clone.isDie()) {
                clone.leaveMap();
            }
        }
        this.clones.clear();
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        RedRibbonHQManager.gI().removeBoss(this);
        this.dispose();
    }

}
