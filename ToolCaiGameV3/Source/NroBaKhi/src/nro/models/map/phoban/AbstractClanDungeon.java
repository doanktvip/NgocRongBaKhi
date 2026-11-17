package nro.models.map.phoban;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import nro.models.boss.Boss;
import nro.models.clan.Clan;
import nro.models.map.Zone;
import nro.models.mob.Mob;
import nro.models.player.Player;
import nro.models.map.service.ItemMapService;
import nro.models.server.Maintenance;
import nro.models.utils.Functions;
import nro.models.utils.Logger;
import nro.models.utils.Util;

@Data
public abstract class AbstractClanDungeon implements Runnable {

    public int id;
    public byte level;
    public final List<Zone> zones = new ArrayList<>();
    public final List<Boss> bosses = new ArrayList<>();
    public Clan clan;
    public boolean isOpened;
    public long lastTimeOpen;

    protected boolean kickout;
    protected long timeKickOut;
    protected long lastTimeSendNotify;

    public AbstractClanDungeon() {
    }

    public AbstractClanDungeon(int id) {
        this.id = id;
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone != null && zone.map != null && zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public boolean areAllMobsDead() {
        for (Zone zone : this.zones) {
            if (zone != null && zone.mobs != null) {
                for (Mob mob : zone.mobs) {
                    if (mob != null && !mob.isDie()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean areAllBossesDead() {
        for (Zone zone : this.zones) {
            if (zone != null) {
                List<Player> zoneBosses = zone.getBosses();
                if (zoneBosses != null) {
                    for (Player b : zoneBosses) {
                        if (b != null && !b.isDie()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean areAllCharactersDead() {
        return areAllMobsDead() && areAllBossesDead();
    }

    public void clearAllItems() {
        for (Zone zone : this.zones) {
            if (zone != null && zone.items != null) {
                for (int i = zone.items.size() - 1; i >= 0; i--) {
                    if (i < zone.items.size()) {
                        ItemMapService.gI().removeItemMap(zone.items.get(i));
                    }
                }
            }
        }
    }

    /**
     * Khởi chạy Virtual Thread cho phiên phó bản khi mở
     */
    protected void startDungeonThread(String dungeonTitle) {
        String clanName = (this.clan != null) ? this.clan.name : ("Room-" + this.id);
        Thread.ofVirtual().name(dungeonTitle + ": " + clanName).start(this);
    }

    /**
     * Vòng lặp 150ms chuẩn (Cài đặt 1 lần duy nhất ở lớp cha)
     */
    @Override
    public final void run() {
        while (!Maintenance.isRunning && this.isOpened) {
            try {
                long startTime = System.currentTimeMillis();
                update();
                Functions.sleep(Math.max(150 - (System.currentTimeMillis() - startTime), 10));
            } catch (Exception e) {
                Logger.logException(this.getClass(), e, "Lỗi vòng lặp phó bản ID: " + this.id);
                Functions.sleep(1000);
            }
        }
    }

    /**
     * Kiểm tra hết thời gian phó bản hoặc hết 60 giây đếm ngược
     */
    protected boolean isTimeExpired(int maxDurationMs) {
        return Util.canDoWithTime(this.lastTimeOpen, maxDurationMs)
                || (this.kickout && Util.canDoWithTime(this.timeKickOut, 60_000));
    }

    /**
     * Hành vi kết thúc phó bản: Lưu thành tích & trục xuất toàn bộ người chơi
     */
    public void finish() {
        for (Zone zone : this.zones) {
            if (zone == null || zone.getPlayers() == null) {
                continue;
            }
            List<Player> playersSnapshot = new ArrayList<>(zone.getPlayers());
            for (Player pl : playersSnapshot) {
                if (pl != null) {
                    onPlayerFinish(pl);
                    kickOut(pl);
                }
            }
        }
    }

    /**
     * Hành vi đóng & giải phóng tài nguyên phó bản an toàn tuyệt đối
     */
    public void dispose() {
        this.isOpened = false;
        try {
            this.removeTextTime();
            for (Boss boss : this.bosses) {
                if (boss != null && boss.zone != null) {
                    boss.leaveMap();
                }
            }
            this.bosses.clear();
            this.clearAllItems();
            this.onResetExtraState();
        } catch (Exception e) {
            Logger.logException(this.getClass(), e, "Lỗi giải phóng phó bản ID: " + this.id);
        } finally {
            if (this.clan != null) {
                this.unlinkClan();
                this.clan = null;
            }
            this.kickout = false;
        }
    }

    // ==================== CÁC PHƯƠNG THỨC TRỪU TƯỢNG LỚP CON CÀI ĐẶT ====================

    public abstract void update();

    protected abstract void kickOut(Player player);

    protected abstract void sendTextTime();

    protected abstract void removeTextTime();

    protected abstract void unlinkClan();

    protected void onPlayerFinish(Player player) {
    }

    protected void onResetExtraState() {
    }
}
