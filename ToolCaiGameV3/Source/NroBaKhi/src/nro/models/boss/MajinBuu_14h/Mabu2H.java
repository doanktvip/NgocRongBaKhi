package nro.models.boss.MajinBuu_14h;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.boss.Boss_Manager.FinalBossManager;
import nro.models.consts.BossStatus;
import nro.models.consts.ConstTaskBadges;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.models.task.BadgesTaskService;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;
import java.util.List;
import java.util.ArrayList;
import nro.models.map.service.ChangeMapService;

public class Mabu2H extends Boss {

    public java.util.List<Player> maBuEat = new java.util.ArrayList<>();

    public Mabu2H() throws Exception {
        super(BossID.MABU, false, true, BossesData.MABU, BossesData.SUPER_BU, BossesData.BU_TENK, BossesData.BU_HAN, BossesData.KID_BU);
        this.isAutoResetTurn = true;
        this.textRetreat = "|-1|Hừ, lũ sâu bọ các ngươi không đáng để ta tốn thời gian! Bư đi ngủ đây!";
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }
        super.reward(plKill);
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        this.rewardEventPoint(plKill, 5);

        // Rơi duy nhất đúng 1 món theo bảng tỉ lệ phân tầng (1% Thần Linh, 5% Đồ Sao, 94% Ngọc Rồng)
        this.drop(plKill).pickOne()
                .rate(80).equipment(13)
                .rate(5).equipment(9, 11).randomStar(100)
                .otherwise().random(15, 16, 17, 18, 19, 20)
                .execute();
    }

    public void releasePlayersInMabu() {
        if (this.maBuEat != null && !this.maBuEat.isEmpty()) {
            List<Player> pls = new ArrayList<>(this.maBuEat);
            for (Player pl : pls) {
                if (pl != null && pl.zone != null && pl.zone.map.mapId == 128) {
                    ChangeMapService.gI().changeMap(pl, 127, this.zone != null ? this.zone.zoneId : 0, -1, 312);
                }
            }
            this.maBuEat.clear();
        }
    }

    private int countHitNormal;
    private int maxHitToChatNormal = 30;
    private int countHitSpecial;

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            return 0;
        }

        // Ở Dạng cuối cùng (Kid Bư)
        if (this.currentLevel >= this.data.length - 1) {
            boolean isSpecialSkill = false;
            if (plAtt != null && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
                int skillId = plAtt.playerSkill.skillSelect.template.id;
                if (skillId == Skill.QUA_CAU_KENH_KHI || skillId == Skill.TU_SAT || skillId == Skill.MAKANKOSAPPO) {
                    isSpecialSkill = true;
                }
            }

            // Giai đoạn 1: Máu Bư > 1 triệu -> CHẶN Bom, QCKK, Laze (chỉ cho đòn thường đánh rút máu)
            if (this.nPoint.hp > 1_000_000) {
                if (isSpecialSkill) {
                    countHitSpecial++;
                    if (countHitSpecial == 1 || countHitSpecial % 5 == 0) {
                        this.chat("Hahaha! Muốn kết liễu ta sớm thế sao? Đừng mơ!");
                    }
                    return 0;
                }

                // Đòn thường chỉ được rút máu tối đa về mốc 1 triệu HP, không bao giờ giết được Bư
                long maxDamage = this.nPoint.hp - 1_000_000;
                boolean shouldSetTo1M = (damage >= maxDamage);
                if (shouldSetTo1M) {
                    damage = maxDamage;
                }

                int dameInjured = super.injured(plAtt, damage, piercing, isMobAttack);

                if (shouldSetTo1M || this.nPoint.hp < 1_000_000) {
                    this.nPoint.hp = 1_000_000;
                }
                return dameInjured;
            }

            // Giai đoạn 2: Máu Bư <= 1 triệu -> CHẶN đòn thường, BẮT BUỘC dùng Bom, QCKK, Laze để kết liễu
            if (!isSpecialSkill) {
                countHitNormal++;
                if (countHitNormal >= maxHitToChatNormal) {
                    countHitNormal = 0;
                    maxHitToChatNormal = Util.nextInt(25, 40);
                    this.chat("Gãi ngứa à lũ sâu bọ? Đòn thường chả xi nhê gì đâu!");
                }
                return 0;
            }
        }

        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    @Override
    public void die(Player plKill) {
        // Nếu là dạng cuối cùng hoặc boss bị tiêu diệt, giải thoát người chơi trong bụng về map 127
        if (this.currentLevel >= this.data.length - 1) {
            if (this.zone != null) {
                Boss boss = FinalBossManager.gI().getBossById(BossID.SUPERBU, 128, this.zone.zoneId);
                if (boss != null) {
                    boss.changeStatus(BossStatus.DIE);
                }
            }
            releasePlayersInMabu();
        }
        super.die(plKill);
    }

    @Override
    public void leaveMap() {
        if (this.currentLevel >= this.data.length - 1) {
            if (this.zone != null) {
                Boss boss = FinalBossManager.gI().getBossById(BossID.SUPERBU, 128, this.zone.zoneId);
                if (boss != null) {
                    boss.changeStatus(BossStatus.DIE);
                }
            }
            releasePlayersInMabu();
        }
        super.leaveMap();
    }

    @Override
    public void respawn() {
        super.respawn();
        this.countHitNormal = 0;
        this.countHitSpecial = 0;
    }

    @Override
    public void rest() {
        if (!TimeUtil.isMabu14HOpen()) {
            return;
        }
        super.rest();
    }
}
