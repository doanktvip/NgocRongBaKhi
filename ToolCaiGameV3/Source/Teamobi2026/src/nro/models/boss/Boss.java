package nro.models.boss;

import nro.models.boss.Boss_Manager.BrolyManager;
import nro.models.boss.Boss_Manager.LunarNewYearEventManager;
import nro.models.boss.Boss_Manager.GasDestroyManager;
import nro.models.boss.Boss_Manager.RedRibbonHQManager;
import nro.models.boss.Boss_Manager.SkillSummonedManager;
import nro.models.boss.Boss_Manager.TreasureUnderSeaManager;
import nro.models.boss.Boss_Manager.FinalBossManager;
import nro.models.boss.Boss_Manager.HalloweenEventManager;
import nro.models.boss.Boss_Manager.ChristmasEventManager;
import nro.models.boss.Boss_Manager.TrungThuEventManager;
import nro.models.boss.Boss_Manager.YardartManager;
import nro.models.boss.Boss_Manager.SnakeWayManager;
import nro.models.boss.Boss_Manager.HungVuongEventManager;
import nro.models.boss.Boss_Manager.OtherBossManager;
import nro.models.boss.Boss_Manager.BossManager;
import nro.models.consts.AppearType;
import nro.models.consts.BossStatus;
import nro.models.consts.BossType;
import nro.models.consts.ConstPlayer;
import static nro.models.consts.BossStatus.ACTIVE;
import static nro.models.consts.BossStatus.AFK;
import static nro.models.consts.BossStatus.CHAT_E;
import static nro.models.consts.BossStatus.CHAT_S;
import static nro.models.consts.BossStatus.DIE;
import static nro.models.consts.BossStatus.JOIN_MAP;
import static nro.models.consts.BossStatus.LEAVE_MAP;
import static nro.models.consts.BossStatus.RESPAWN;
import static nro.models.consts.BossStatus.REST;
import static nro.models.consts.BossType.BROLY;
import static nro.models.consts.BossType.CHRISTMAS_EVENT;
import static nro.models.consts.BossType.FINAL;
import static nro.models.consts.BossType.HALLOWEEN_EVENT;
import static nro.models.consts.BossType.HUNGVUONG_EVENT;
import static nro.models.consts.BossType.PHOBAN;
import static nro.models.consts.BossType.PHOBANBDKB;
import static nro.models.consts.BossType.PHOBANCDRD;
import static nro.models.consts.BossType.PHOBANDT;
import static nro.models.consts.BossType.PHOBANKGHD;
import static nro.models.consts.BossType.SKILLSUMMONED;
import static nro.models.consts.BossType.TET_EVENT;
import static nro.models.consts.BossType.TRUNGTHU_EVENT;
import static nro.models.consts.BossType.YARDART;
import nro.models.network.Message;
import nro.models.services.EffectSkillService;
import java.util.ArrayList;
import java.util.List;
import nro.models.map.Zone;
import nro.models.mob.Mob;
import nro.models.player.Pet;
import nro.models.player.Player;
import nro.models.player.DropItem;
import nro.models.skill.Skill;
import nro.models.server.ServerNotify;
import nro.models.map.service.MapService;
import nro.models.services.PlayerService;
import nro.models.services.Service;
import nro.models.services.SkillService;
import nro.models.services.TaskService;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.Logger;
import nro.models.utils.SkillUtil;
import nro.models.utils.Util;
import nro.models.interfaces.IBoss;
import nro.models.item.Item;
import nro.models.map.ItemMap;
import nro.models.services.ItemService;
import java.io.IOException;

public class Boss extends Player implements IBoss {

    public int currentLevel = -1;
    public final BossData[] data;

    public BossStatus bossStatus;

    protected Zone lastZone;

    protected long lastTimeRest;
    protected int secondsRest;

    protected long lastTimeChatS;
    protected int timeChatS;
    protected byte indexChatS;

    protected long lastTimeChatE;
    protected int timeChatE;
    protected byte indexChatE;

    protected long lastTimeChatM;
    protected int timeChatM;

    protected long lastTimeTargetPlayer;
    protected int timeTargetPlayer;
    protected Player playerTarger;

    protected Boss parentBoss;
    public Boss[][] bossAppearTogether;

    public Zone zoneFinal = null;

    public Player playerReward;

    public int lv;

    public int error;

    public boolean prepareBom;

    public boolean isNotifyDisabled;
    public boolean isZone01SpawnDisabled;

    // Quản lý tự hủy & làm mới turn boss khi bị bỏ rơi
    protected long timeWakeUp;
    protected long lastTimeAttacked;
    protected boolean isAutoResetTurn;
    protected String textRetreat = "|-1|Chờ mãi không có ai đến khiêu chiến! Ta rút lui!";

    protected static final long IDLE_TIMEOUT = 300_000;       // 5 phút không ai đánh -> tự hủy
    protected static final long MAX_LIFE_TIME = 900_000;      // 15 phút tổng thời gian sống
    protected static final long COMBAT_GRACE_TIME = 60_000;   // 1 phút gia hạn giao tranh (đang đánh)

    public Boss(int id, boolean isNotifyDisabled, boolean isZone01SpawnDisabled, BossData... data) throws Exception {
        this(id, data);
        this.isNotifyDisabled = isNotifyDisabled;
        this.isZone01SpawnDisabled = isZone01SpawnDisabled;
    }

    public Boss(BossType bossType, int id, boolean isNotifyDisabled, boolean isZone01SpawnDisabled, BossData... data) throws Exception {
        this(bossType, id, data);
        this.isNotifyDisabled = isNotifyDisabled;
        this.isZone01SpawnDisabled = isZone01SpawnDisabled;
    }

    public Boss(int id, BossData... data) throws Exception {
        this.id = id;
        this.isBoss = true;
        if (data == null || data.length == 0) {
            throw new Exception("Dữ liệu boss không hợp lệ");
        }
        this.data = data;
        this.secondsRest = this.data[0].getSecondsRest();
        this.bossStatus = BossStatus.REST;
        BossManager.gI().addBoss(this);

        this.bossAppearTogether = new Boss[this.data.length][];
        for (int i = 0; i < this.bossAppearTogether.length; i++) {
            if (this.data[i].getBossesAppearTogether() != null) {
                this.bossAppearTogether[i] = new Boss[this.data[i].getBossesAppearTogether().length];
                for (int j = 0; j < this.data[i].getBossesAppearTogether().length; j++) {
                    Boss boss = BossManager.gI().createBoss(this.data[i].getBossesAppearTogether()[j]);
                    if (boss != null) {
                        boss.parentBoss = this;
                        boss.lv = j;
                        this.bossAppearTogether[i][j] = boss;
                    }
                }
            }
        }
    }

    public Boss(BossType bossType, int id, BossData... data) throws Exception {
        this.id = id;
        this.isBoss = true;
        if (data == null || data.length == 0) {
            throw new Exception("Dữ liệu boss không hợp lệ");
        }
        this.data = data;
        this.secondsRest = this.data[0].getSecondsRest();
        this.bossStatus = BossStatus.REST;
        switch (bossType) {
            case YARDART ->
                YardartManager.gI().addBoss(this);
            case FINAL ->
                FinalBossManager.gI().addBoss(this);
            case SKILLSUMMONED ->
                SkillSummonedManager.gI().addBoss(this);
            case BROLY ->
                BrolyManager.gI().addBoss(this);
            case PHOBAN ->
                OtherBossManager.gI().addBoss(this);
            case PHOBANDT ->
                RedRibbonHQManager.gI().addBoss(this);
            case PHOBANBDKB ->
                TreasureUnderSeaManager.gI().addBoss(this);
            case PHOBANCDRD ->
                SnakeWayManager.gI().addBoss(this);
            case PHOBANKGHD ->
                GasDestroyManager.gI().addBoss(this);
            case TRUNGTHU_EVENT ->
                TrungThuEventManager.gI().addBoss(this);
            case HALLOWEEN_EVENT ->
                HalloweenEventManager.gI().addBoss(this);
            case CHRISTMAS_EVENT ->
                ChristmasEventManager.gI().addBoss(this);
            case HUNGVUONG_EVENT ->
                HungVuongEventManager.gI().addBoss(this);
            case TET_EVENT ->
                LunarNewYearEventManager.gI().addBoss(this);
        }

        this.bossAppearTogether = new Boss[this.data.length][];
        for (int i = 0; i < this.bossAppearTogether.length; i++) {
            if (this.data[i].getBossesAppearTogether() != null) {
                this.bossAppearTogether[i] = new Boss[this.data[i].getBossesAppearTogether().length];
                for (int j = 0; j < this.data[i].getBossesAppearTogether().length; j++) {
                    Boss boss = BossManager.gI().createBoss(this.data[i].getBossesAppearTogether()[j]);
                    if (boss != null) {
                        boss.parentBoss = this;
                        this.bossAppearTogether[i][j] = boss;
                    }
                }
            }
        }
    }

    public BossData getCurrentBossData() {
        int level = this.currentLevel;
        if (level < 0) {
            level = 0;
        } else if (this.data != null && level >= this.data.length) {
            level = this.data.length - 1;
        }
        return this.data[level];
    }

    @Override
    public void initBase() {
        BossData data = getCurrentBossData();
        this.name = String.format(data.getName(), Util.nextInt(0, 100));
        this.gender = data.getGender();
        this.nPoint.mpg = 31_07_2002;
        this.nPoint.dameg = data.getDame();
        this.nPoint.hpg = data.getHp()[Util.nextInt(0, data.getHp().length - 1)];
        this.nPoint.hp = nPoint.hpg;
        this.nPoint.calPoint();
        this.initSkill();
        this.resetBase();
    }

    protected void initSkill() {
        for (Skill skill : this.playerSkill.skills) {
            skill.dispose();
        }
        this.playerSkill.skills.clear();
        this.playerSkill.skillSelect = null;
        int[][] skillTemps = getCurrentBossData().getSkillTemp();
        for (int[] skillTemp : skillTemps) {
            Skill skill = SkillUtil.createSkill(skillTemp[0], skillTemp[1]);
            if (skill != null) {
                if (skillTemp.length > 2) {
                    skill.coolDown = skillTemp[2];
                }
                this.playerSkill.skills.add(skill);
            }
        }
    }

    protected void resetBase() {
        this.lastTimeChatS = 0;
        this.lastTimeChatE = 0;
        this.timeChatS = 0;
        this.timeChatE = 0;
        this.indexChatS = 0;
        this.indexChatE = 0;
    }

    //.outfit.
    @Override
    public short getHead() {
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][0];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        }
        return getCurrentBossData().getOutfit()[0];
    }

    @Override
    public short getBody() {
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][1];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        }
        return getCurrentBossData().getOutfit()[1];
    }

    @Override
    public short getLeg() {
        if (effectSkill != null && effectSkill.isBinh) {
            return idOutfitMafuba[effectSkill.typeBinh][2];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        }
        return getCurrentBossData().getOutfit()[2];

    }

    @Override
    public short getFlagBag() {
        return getCurrentBossData().getOutfit()[3];
    }

    @Override
    public byte getAura() {
        return (byte) getCurrentBossData().getOutfit()[4];
    }

    @Override
    public byte getEffFront() {
        return (byte) getCurrentBossData().getOutfit()[5];
    }

    public Zone getMapJoin() {
        BossData bData = getCurrentBossData();
        int mapId = bData.getMapJoin()[Util.nextInt(0, bData.getMapJoin().length - 1)];
        Zone map = MapService.gI().getMapWithRandZone(mapId);
        return map;
    }

    @Override
    public void changeStatus(BossStatus status) {
        this.bossStatus = status;
    }

    @Override
    public Player getPlayerAttack() {
        if (this.zone == null) {
            return null;
        }
        if (this.playerTarger != null && (this.playerTarger.isDie() || !this.zone.equals(this.playerTarger.zone))) {
            this.playerTarger = null;
        }
        if (this.playerTarger == null || Util.canDoWithTime(this.lastTimeTargetPlayer, this.timeTargetPlayer)) {
            this.playerTarger = this.zone.getRandomPlayerInMap();
            this.lastTimeTargetPlayer = System.currentTimeMillis();
            this.timeTargetPlayer = Util.nextInt(5000, 7000);
        }
        if (this.playerTarger != null && this.playerTarger.isPet && ((Pet) this.playerTarger).master != null && ((Pet) this.playerTarger).master.equals(this)) {
            this.playerTarger = null;
        }
        return this.playerTarger;
    }

    @Override
    public void changeToTypePK() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
    }

    @Override
    public void changeToTypeNonPK() {
        PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
    }

    @Override
    public void updateInfo() {
        super.update();
    }

    @Override
    public void update() {
        if (prepareBom) {
            return;
        }
        super.update();
        this.nPoint.mp = this.nPoint.mpg;
        if (this.effectSkill == null || this.effectSkill.isHaveEffectSkill() || (this.newSkill != null && this.newSkill.isStartSkillSpecial)) {
            return;
        }
        switch (this.bossStatus) {
            case CHAT_S, AFK, ACTIVE ->
                this.autoLeaveMap();
        }
        switch (this.bossStatus) {
            case REST ->
                this.rest();
            case RESPAWN -> {
                this.respawn();
                this.changeStatus(BossStatus.JOIN_MAP);
            }
            case JOIN_MAP ->
                this.joinMap();
            case CHAT_S -> {
                if (chatS()) {
                    this.doneChatS();
                    this.lastTimeChatM = System.currentTimeMillis();
                    this.timeChatM = 5000;
                    if (this.bossStatus != BossStatus.AFK) {
                        this.changeStatus(BossStatus.ACTIVE);
                    }
                }
            }
            case AFK ->
                this.afk();
            case ACTIVE -> {
                this.chatM();
                if (this.effectSkill.isCharging && !Util.isTrue(1, 20) || this.effectSkill.useTroi) {
                    return;
                }
                this.active();
            }
            case DIE ->
                this.changeStatus(BossStatus.CHAT_E);
            case CHAT_E -> {
                if (chatE()) {
                    this.doneChatE();
                    this.changeStatus(BossStatus.LEAVE_MAP);
                }
            }
            case LEAVE_MAP ->
                this.leaveMap();
        }
    }

    @Override
    public void rest() {
        int nextLevel = this.currentLevel + 1;
        if (nextLevel >= this.data.length) {
            nextLevel = 0;
        }
        if (this.data[nextLevel].getTypeAppear() == AppearType.DEFAULT_APPEAR
                && Util.canDoWithTime(lastTimeRest, secondsRest * 1000)) {
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    @Override
    public void afk() {

    }

    @Override
    public void respawn() {
        this.currentLevel++;
        if (this.currentLevel >= this.data.length) {
            this.currentLevel = 0;
        }
        this.initBase();
        this.changeToTypeNonPK();
    }

    @Override
    public void joinMap() {
        long now = System.currentTimeMillis();
        this.timeWakeUp = now;
        this.lastTimeAttacked = now;
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            this.notifyJoinMap();
            this.changeStatus(BossStatus.CHAT_S);
            this.wakeupAnotherBossWhenAppear();
            return;
        }
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }
        if (this.zone == null) {
            this.zone = getMapJoin();
        }
        if (this.zone != null) {
            try {
                if (this.currentLevel == 0) {
                    if (this.parentBoss == null) {
                        int zoneid = 0;
                        //this.zone.map.mapId == 80 || this.zone.map.mapId == 103 || this.zone.map.mapId == 97 || this.zone.map.mapId == 102
                        // Chỉ cho boss xuất hiện từ khu 2 trở lên ở map thường
                        if (this.isZone01SpawnDisabled && this.zone.map.zones.size() > 2) {
                            zoneid = Util.nextInt(2, this.zone.map.zones.size() - 1);
                            while (zoneid < this.zone.map.zones.size() && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                                zoneid++;
                            }

                            if (zoneid < this.zone.map.zones.size()) {
                                this.zone = this.zone.map.zones.get(zoneid);
                            } else {
                                this.changeStatus(BossStatus.REST);
                                this.zone = null;
                                this.lastZone = null;
                                return;
                            }
                        } else {
                            // Check trong khu lớn hơn 10 người chuyển sang khu n + 1
                            while (zoneid < this.zone.map.zones.size() && this.zone.map.zones.get(zoneid).getNumOfPlayers() > 10) {
                                zoneid++;
                            }
                            // Check trong khu có boss sẽ chuyển sang khu n + 1
                            while (zoneid < this.zone.map.zones.size() && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                                zoneid++;
                            }
                            if (zoneid < this.zone.map.zones.size()) {
                                this.zone = this.zone.map.zones.get(zoneid);
                            } else {
                                this.zone = this.zone.map.zones.get(0);
                            }
                        }
                        int x = this.zone.map.mapWidth > 100 ? Util.nextInt(100, this.zone.map.mapWidth - 100) : Util.nextInt(100);
                        int y = this.zone.map.yPhysicInTop(x, 100);
                        ChangeMapService.gI().changeMap(this, this.zone, x, y);
                    } else {
                        int x = this.parentBoss.location.x - (this.lv + 1) * 30;
                        int y = this.zone.map.yPhysicInTop(x, 100);
                        ChangeMapService.gI().changeMap(this, this.zone, x, y);
                    }
                    this.wakeupAnotherBossWhenAppear();
                } else {
                    ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
                }
                Service.gI().sendFlagBag(this);
                this.notifyJoinMap();
                this.changeStatus(BossStatus.CHAT_S);
            } catch (Exception e) {
                this.changeStatus(BossStatus.REST);
                if (error < 5) {
                    Logger.error("Lỗi : " + e + "\n");
                    error++;
                }
            }
        } else {
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    public void joinMapByZone(Zone zone) {
        if (zone != null) {
            this.zone = zone;
            int x = this.zone.map.mapWidth > 100 ? Util.nextInt(100, this.zone.map.mapWidth - 100) : Util.nextInt(100);
            int y = this.zone.map.yPhysicInTop(x, 100);
            ChangeMapService.gI().changeMap(this, this.zone, x, y);
        }
    }

    protected void notifyJoinMap() {
        if (canSendNotify()) {
            ServerNotify.gI().notify("BOSS " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
        }
    }

    private boolean canSendNotify() {
        return !(this.isNotifyDisabled || this.zone.map.mapId == 140
                || this.zone.map.mapId == 111
                || MapService.gI().isMapPhoBan(this.zone.map.mapId)
                || MapService.gI().isMapMaBu(this.zone.map.mapId)
                || MapService.gI().isMapBlackBallWar(this.zone.map.mapId));
    }

    @Override
    public boolean chatS() {
        if (Util.canDoWithTime(lastTimeChatS, timeChatS)) {
            BossData bData = getCurrentBossData();
            if (this.indexChatS == bData.getTextS().length) {
                return true;
            }
            String textChat = bData.getTextS()[this.indexChatS];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.lastTimeChatS = System.currentTimeMillis();
            this.timeChatS = textChat.length() * 100;
            if (this.timeChatS > 2000) {
                this.timeChatS = 2000;
            }
            this.indexChatS++;
        }
        return false;
    }

    @Override
    public void doneChatS() {

    }

    @Override
    public void chatM() {
        if (this.typePk == ConstPlayer.NON_PK) {
            return;
        }
        BossData bData = getCurrentBossData();
        if (bData.getTextM().length == 0) {
            return;
        }
        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }
        String textChat = bData.getTextM()[Util.nextInt(0, bData.getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 20000);
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
    }

    protected long lastTimeAttack;

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = getSkillAttack(pl);
                if (this.playerSkill.skillSelect == null) {
                    return;
                }
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 100)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 30)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 30));
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    this.moveToPlayer(pl);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    protected Skill getSkillAttack(Player pl) {
        if (this.playerSkill == null || this.playerSkill.skills.isEmpty()) {
            return null;
        }
        int distance = Util.getDistance(this, pl);

        List<Skill> readySpecialSkills = new ArrayList<>();
        List<Skill> readyRangedSkills = new ArrayList<>();
        List<Skill> readyMeleeSkills = new ArrayList<>();
        Skill defaultSkill = null;

        for (Skill s : this.playerSkill.skills) {
            this.playerSkill.skillSelect = s;
            boolean canUse = SkillService.gI().canUseSkillWithCooldown(this);
            int sId = s.template.id;

            if (sId == Skill.DRAGON || sId == Skill.DEMON || sId == Skill.GALICK || sId == Skill.KAIOKEN || sId == Skill.LIEN_HOAN) {
                if (defaultSkill == null) {
                    defaultSkill = s;
                }
            }

            if (canUse) {
                if (sId == Skill.TAI_TAO_NANG_LUONG) {
                    if (this.nPoint.hp < this.nPoint.hpMax) {
                        readySpecialSkills.add(s);
                    }
                } else if (sId == Skill.THAI_DUONG_HA_SAN || sId == Skill.THOI_MIEN || sId == Skill.TROI
                        || sId == Skill.KHIEN_NANG_LUONG || sId == Skill.BIEN_KHI || sId == Skill.SOCOLA
                        || sId == Skill.NUOT_CHUNG || sId == Skill.DA_DON_THO) {
                    readySpecialSkills.add(s);
                } else if (sId == Skill.KAMEJOKO || sId == Skill.MASENKO || sId == Skill.ANTOMIC
                        || sId == Skill.QUA_CAU_KENH_KHI || sId == Skill.MAKANKOSAPPO) {
                    readyRangedSkills.add(s);
                } else {
                    readyMeleeSkills.add(s);
                }
            }
        }

        // 1. Chiêu đặc biệt/khống chế: ưu tiên dùng ngay khi hồi xong và đối thủ trong tầm (<= 500)
        if (!readySpecialSkills.isEmpty() && distance <= 500) {
            return readySpecialSkills.get(Util.nextInt(0, readySpecialSkills.size() - 1));
        }

        // 2. Nếu người chơi ở xa (> tầm đấm) và có chiêu chưởng sẵn sàng -> Bắn chưởng tầm xa
        if (distance > Skill.RANGE_ATTACK_CHIEU_DAM && !readyRangedSkills.isEmpty()) {
            return readyRangedSkills.get(Util.nextInt(0, readyRangedSkills.size() - 1));
        }

        // 3. Nếu ở gần (<= tầm đấm), 30% cơ hội bắn chưởng bất ngờ thay vì chỉ đấm
        if (distance <= Skill.RANGE_ATTACK_CHIEU_DAM && !readyRangedSkills.isEmpty() && Util.isTrue(3, 10)) {
            return readyRangedSkills.get(Util.nextInt(0, readyRangedSkills.size() - 1));
        }

        // 4. Chiêu cận chiến / đấm sẵn sàng
        if (!readyMeleeSkills.isEmpty()) {
            return readyMeleeSkills.get(Util.nextInt(0, readyMeleeSkills.size() - 1));
        }

        // 5. Nếu không có chiêu nào sẵn sàng: trả về chiêu đấm cơ bản (hoặc chiêu đầu tiên)
        return defaultSkill != null ? defaultSkill : this.playerSkill.skills.get(0);
    }

    @Override
    public void checkPlayerDie(Player player) {
        if (player.isDie()) {

        }
    }

    protected int getRangeCanAttackWithSkillSelect() {
        int skillId = this.playerSkill.skillSelect.template.id;
        if (skillId == Skill.KAMEJOKO || skillId == Skill.MASENKO || skillId == Skill.ANTOMIC) {
            return Skill.RANGE_ATTACK_CHIEU_CHUONG;
        } else if (skillId == Skill.DRAGON || skillId == Skill.DEMON || skillId == Skill.GALICK || skillId == Skill.LIEN_HOAN || skillId == Skill.KAIOKEN) {
            return Skill.RANGE_ATTACK_CHIEU_DAM;
        }
        return 500;
    }

    @Override
    public void die(Player plKill) {

        if (plKill != null
                && (this.zone.map.mapId != 140 || !MapService.gI().isMapMaBu(this.zone.map.mapId)
                || !MapService.gI().isMapDoanhTrai(this.zone.map.mapId)
                || !MapService.gI().isMapBanDoKhoBau(this.zone.map.mapId))) {
            if (!plKill.isBot) {
                reward(plKill);
            }
            ServerNotify.gI().notify(plKill.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
            this.changeStatus(BossStatus.DIE);
        } else {
            if (plKill != null && !plKill.isBot) {
                reward(plKill);
            }
            this.changeStatus(BossStatus.DIE);
        }
    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    // =========================================================================
    // FLUENT DROP BUILDER (HỆ THỐNG KIẾN TẠO RƠI ĐỒ LINH HOẠT TỰ DO)
    // =========================================================================

    /**
     * KHỞI TẠO BỘ KIẾN TẠO RƠI ĐỒ (Fluent Drop Builder) CHO BOSS:
     * Tự động liên kết với Zone và Location hiện tại của Boss.
     * Mặc định allowAutoPick = false để đồ Boss nổ bung ngập sàn, tạo cảm giác săn Boss đỉnh cao.
     * 
     * Ví dụ sử dụng:
     * - this.drop(plKill).equipment(13).rate(1, 5000).execute(); // 1/5000 rơi đồ Thần Linh
     * - this.drop(plKill).random(18, 19, 20).rate(80).execute(); // 80% rơi ngọc rồng
     * - this.drop(plKill).burst(listItems).executeAll();         // Nổ cụm đồ hình cánh sen
     * 
     * @param plKill Người chơi kết liễu Boss
     * @return DropBuilder để tự do cấu hình rơi đồ
     */
    public DropItem.DropBuilder drop(Player plKill) {
        return DropItem.to(plKill, this.zone, this.location).autoPick(false);
    }

    /**
     * Khởi tạo rơi đồ tự do cho toàn bộ map cùng tranh nhau nhặt (Free-for-all).
     */
    public DropItem.DropBuilder dropFree() {
        return DropItem.free(this.zone, this.location).autoPick(false);
    }

    /**
     * Thưởng điểm sự kiện cho người chơi kết liễu Boss.
     * Tự động cộng điểm vào tài khoản và PlayerEvent đã tự gửi thông báo "+[point] Điểm săn Boss".
     *
     * @param plKill Người chơi kết liễu Boss
     * @param point Số điểm sự kiện (ví dụ: 1 hoặc 5)
     */
    public void rewardEventPoint(Player plKill, int point) {
        if (plKill != null && plKill.event != null && point > 0) {
            plKill.event.addEventPoint(point);
        }
    }

    @Override
    public boolean chatE() {
        if (Util.canDoWithTime(lastTimeChatE, timeChatE)) {
            BossData bData = getCurrentBossData();
            if (this.indexChatE == bData.getTextE().length) {
                return true;
            }
            String textChat = bData.getTextE()[this.indexChatE];
            int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
            textChat = textChat.substring(textChat.lastIndexOf("|") + 1);
            if (!this.chat(prefix, textChat)) {
                return false;
            }
            this.lastTimeChatE = System.currentTimeMillis();
            this.timeChatE = textChat.length() * 100;
            if (this.timeChatE > 2000) {
                this.timeChatE = 2000;
            }
            this.indexChatE++;
        }
        return false;
    }

    @Override
    public void doneChatE() {

    }

    @Override
    public void leaveMap() {
        if (this.currentLevel < this.data.length - 1) {
            this.lastZone = this.zone;
            this.changeStatus(BossStatus.RESPAWN);
        } else {
            ChangeMapService.gI().exitMap(this);
            this.lastZone = null;
            this.lastTimeRest = System.currentTimeMillis();
            this.changeStatus(BossStatus.REST);
        }
        this.wakeupAnotherBossWhenDisappear();
    }

    @Override
    public synchronized int injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (this.isUntargetable) {
                this.chat("Vô ích thôi, ta đang ở dưới lòng đất!");
                return 0;
            }
            if (plAtt != null) {
                this.lastTimeAttacked = System.currentTimeMillis();
            }
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (plAtt != null && plAtt.idNRNM != -1) {
                return 1;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && this.effectSkill != null && this.effectSkill.isShielding) {
                if (this.idMark != null) {
                    this.idMark.setDamePST((int) Math.min(damage, 2_147_483_647L));
                }
                if (damage > this.nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return (int) damage;
        } else {
            return 0;
        }
    }

    @Override
    public void moveToPlayer(Player pl) {
        if (pl.location != null) {
            moveTo(pl.location.x, pl.location.y);
        }
    }

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        int distance = Math.abs(this.location.x - x);
        int move;
        if (distance > 300) {
            move = Util.nextInt(100, 150); // Khoảng cách xa: áp sát nhanh
        } else if (distance > 100) {
            move = Util.nextInt(60, 90);   // Khoảng cách trung bình
        } else {
            move = Math.min(distance, Util.nextInt(30, 50)); // Khoảng cách gần: tiếp cận nhẹ nhàng
        }
        int newX = this.location.x + (dir == 1 ? move : -move);
        int newY = y + (Util.isTrue(2, 10) ? -40 : 0);
        PlayerService.gI().playerMove(this, newX, newY);
    }

    public void chat(String text) {
        Service.gI().chat(this, text);
    }

    protected boolean chat(int prefix, String textChat) {
        if (prefix == -1) {
            this.chat(textChat);
        } else if (prefix == -2) {
            if (this.zone != null) {
                Player plMap = this.zone.getRandomPlayerInMap();
                if (plMap != null && !plMap.isDie() && Util.getDistance(this, plMap) <= 600) {
                    Service.gI().chat(plMap, textChat);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (prefix == -3) {
            if (this.parentBoss != null && !this.parentBoss.isDie()) {
                this.parentBoss.chat(textChat);
            }
        } else if (prefix >= 0) {
            if (this.bossAppearTogether != null && this.bossAppearTogether[this.currentLevel] != null) {
                Boss boss = this.bossAppearTogether[this.currentLevel][prefix];
                if (!boss.isDie()) {
                    boss.chat(textChat);
                }
            } else if (this.parentBoss != null && this.parentBoss.bossAppearTogether != null
                    && this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] != null) {
                Boss boss = this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel][prefix];
                if (!boss.isDie()) {
                    boss.chat(textChat);
                }
            }
        }
        return true;
    }

    @Override
    public void wakeupAnotherBossWhenAppear() {
        if (this.bossAppearTogether == null || this.bossAppearTogether[this.currentLevel] == null) {
            return;
        }
        for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
            int nextLevelBoss = boss.currentLevel + 1;
            if (nextLevelBoss >= boss.data.length) {
                nextLevelBoss = 0;
            }
            if (boss.data[nextLevelBoss].getTypeAppear() == AppearType.CALL_BY_ANOTHER) {
                if (boss.zone != null) {
                    boss.leaveMap();
                }
            }
            if (boss.data[nextLevelBoss].getTypeAppear() == AppearType.APPEAR_WITH_ANOTHER) {
                if (boss.zone != null) {
                    boss.leaveMap();
                }
                boss.changeStatus(BossStatus.RESPAWN);
            }
        }
    }

    @Override
    public void wakeupAnotherBossWhenDisappear() {
    }

    public void wakeUp() {
        if (!this.isDie()) {
            long now = System.currentTimeMillis();
            this.timeWakeUp = now;
            this.lastTimeAttacked = now;
            this.indexChatS = 0;
            this.lastTimeChatS = 0;
            this.timeChatS = 0;
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public void autoLeaveMap() {
        this.checkAutoResetTurn();
    }

    public void checkAutoResetTurn() {
        if (!this.isAutoResetTurn || this.isDie()) {
            return;
        }

        // Nếu là Boss nhiều dạng (như Fide, Xen), Dạng 1 (currentLevel == 0) không tự hủy
        if (this.data != null && this.data.length > 1 && this.currentLevel == 0) {
            return;
        }

        // Chỉ kiểm tra khi boss đang thức tỉnh (CHAT_S hoặc ACTIVE)
        if (this.bossStatus != BossStatus.CHAT_S && this.bossStatus != BossStatus.ACTIVE) {
            return;
        }

        boolean isAbandoned = Util.canDoWithTime(this.lastTimeAttacked, IDLE_TIMEOUT);
        boolean isExpired = Util.canDoWithTime(this.timeWakeUp, MAX_LIFE_TIME)
                && Util.canDoWithTime(this.lastTimeAttacked, COMBAT_GRACE_TIME);

        if (isAbandoned || isExpired) {
            this.chat(this.textRetreat);
            if (this.data != null && this.data.length > 1) {
                this.leaveMapNew();
            } else {
                this.resetBossGroup();
            }
        }
    }

    public void leaveMapNew() {
        if (this.data != null) {
            this.currentLevel = this.data.length;
        }
        this.changeStatus(BossStatus.LEAVE_MAP);
    }

    public void resetBossGroup() {
        Boss root = (this.parentBoss != null) ? this.parentBoss : this;
        if (root.bossAppearTogether != null && root.currentLevel < root.bossAppearTogether.length
                && root.bossAppearTogether[root.currentLevel] != null) {
            for (Boss b : root.bossAppearTogether[root.currentLevel]) {
                if (b != null) {
                    if (b.zone != null) {
                        ChangeMapService.gI().exitMap(b);
                    }
                    b.zone = null;
                    b.lastZone = null;
                    b.changeToTypeNonPK();
                    b.changeStatus(BossStatus.REST);
                    b.lastTimeRest = System.currentTimeMillis();
                }
            }
        }
        if (root.zone != null) {
            ChangeMapService.gI().exitMap(root);
        }
        root.zone = null;
        root.lastZone = null;
        root.changeToTypeNonPK();
        root.changeStatus(BossStatus.REST);
        root.lastTimeRest = System.currentTimeMillis();
    }

    @Override
    public void setBom(Player plAtt) {
        try {
            if (!prepareBom) {
                prepareBom = true;
                this.nPoint.hp = 1;
                long lastTime = System.currentTimeMillis();
                //gồng tự sát
                Service.gI().chat(Boss.this, "Rồi, rồi, mày xong rồi!");
                Message msg;
                try {
                    msg = new Message(-45);
                    msg.writer().writeByte(7);
                    msg.writer().writeInt((int) Boss.this.id);
                    msg.writer().writeShort(104);
                    msg.writer().writeShort(2000);
                    Service.gI().sendMessAllPlayerInMap(Boss.this, msg);
                    msg.cleanup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (prepareBom) {
                    if (Util.canDoWithTime(lastTime, 2500)) {
                        setDie(this);
                        die(plAtt);
                        long dame = Boss.this.nPoint.hpMax;
                        for (Mob mob : Boss.this.zone.mobs) {
                            mob.injured(Boss.this, dame, true);
                        }
                        List<Player> playersMap = Boss.this.zone.getNotBosses();
                        if (!MapService.gI().isMapOffline(Boss.this.zone.map.mapId)) {
                            //Sử dụng vòng for lặp ngược để hạn chế lỗi đồng bộ
                            for (int i = playersMap.size() - 1; i >= 0; i--) {
                                Player pl = playersMap.get(i);
                                if (!Boss.this.equals(pl)) {
                                    pl.injured(Boss.this, dame, false, false);
                                    PlayerService.gI().sendInfoHpMpMoney(pl);
                                    Service.gI().Send_Info_NV(pl);
                                }
                            }
                        }
                        prepareBom = false;
                    }
                }
            }
        } catch (Exception e) {
            if (prepareBom) {
                prepareBom = false;
            }
            setDie(this);
            die(plAtt);
        }
    }

}
