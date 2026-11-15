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

@Data
public abstract class AbstractClanDungeon implements Runnable {

    public int id;
    public byte level;
    public final List<Zone> zones = new ArrayList<>();
    public Clan clan;
    public boolean isOpened;
    public long lastTimeOpen;

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
                List<Player> bosses = zone.getBosses();
                if (bosses != null) {
                    for (Player boss : bosses) {
                        if (boss != null && !boss.isDie()) {
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
}
