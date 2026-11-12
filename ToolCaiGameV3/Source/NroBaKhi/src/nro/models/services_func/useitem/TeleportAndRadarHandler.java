package nro.models.services_func.useitem;

import nro.models.boss.Boss;
import nro.models.boss.Boss_mini.SoiHecQuyn;
import nro.models.services.shenron.SummonDragon;
import nro.models.combine.CombineService;
import nro.models.boss.BossID;
import nro.models.consts.ConstItem;
import nro.models.radar.Card;
import nro.models.services.RadarService;
import nro.models.radar.RadarCard;
import nro.models.consts.ConstMap;
import nro.models.item.Item;
import nro.models.consts.ConstNpc;
import nro.models.consts.ConstPlayer;
import nro.models.services.shenron.Shenron_Service;
import nro.models.item.Item.ItemOption;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import nro.models.consts.ConstTaskBadges;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Inventory;
import nro.models.map.service.NpcService;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.models.network.Message;
import nro.models.map.service.ChangeMapService;
import nro.models.utils.SkillUtil;
import nro.models.services.Service;
import nro.models.utils.Util;
import nro.models.network.MySession;
import nro.models.npc.MabuEgg;
import nro.models.services.ItemService;
import nro.models.services.ItemTimeService;
import nro.models.services.PetService;
import nro.models.services.RewardService;
import nro.models.services.PlayerService;
import nro.models.services.TaskService;
import nro.models.services.InventoryService;
import nro.models.map.service.MapService;
import nro.models.services_dungeon.NgocRongNamecService;
import nro.models.map.service.ItemMapService;
import nro.models.npc.DuaHauEgg;
import nro.models.server.Manager;
import static nro.models.server.Manager.isTopSukienChanged;
import nro.models.services.ChatGlobalService;
import nro.models.task.BadgesTaskService;
import nro.models.utils.Logger;

import nro.models.services_func.UseItem;
import nro.models.services_func.Input;
import nro.models.services_func.TransactionService;

/**
 * Handler tách từ UseItem.java: TeleportAndRadarHandler
 */
public class TeleportAndRadarHandler {

    private static TeleportAndRadarHandler instance;
    private static final Random rand = new Random();

    private TeleportAndRadarHandler() {
    }

    public static TeleportAndRadarHandler gI() {
        if (instance == null) {
            instance = new TeleportAndRadarHandler();
        }
        return instance;
    }

    public int randomInRange(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }


    public void RadaNgocRong(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item itemused = InventoryService.gI().findItemBag(player, 1822);

            if (itemused == null || itemused.quantity < 1) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần dùng!");
                return;
            }

            int[] itemIds = {1579, 1580, 1581, 1582, 1583, 1584, 1585};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            int dame = randomInRange(10, 17);
            int hp = randomInRange(10, 17);
            int ki = randomInRange(10, 17);
            int khongthegiaodich = randomInRange(0, 0);

            newItem.itemOptions.add(new ItemOption(50, dame));
            newItem.itemOptions.add(new ItemOption(77, hp));
            newItem.itemOptions.add(new ItemOption(103, ki));
            newItem.itemOptions.add(new ItemOption(30, khongthegiaodich));

            int randomOption = (int) (Math.random() * 100);
            newItem.itemOptions.add(new ItemOption(93, randomOption < 0.5 ? 0 : 15));

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);

            PlayerService.gI().sendInfoHpMpMoney(player);
            InventoryService.gI().sendItemBags(player);
        }
    }
    

    public void RadaNgocRongVip(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item itemused = InventoryService.gI().findItemBag(player, 1823);

            if (itemused == null || itemused.quantity < 1) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần dùng!");
                return;
            }

            int[] itemIds = {1586, 1809, 1810, 1811, 1812, 1813, 1814};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            int dame = randomInRange(10, 20);
            int hp = randomInRange(10, 20);
            int ki = randomInRange(10, 20);
            int khongthegiaodich = randomInRange(0, 0);

            newItem.itemOptions.add(new ItemOption(50, dame));
            newItem.itemOptions.add(new ItemOption(77, hp));
            newItem.itemOptions.add(new ItemOption(103, ki));
            newItem.itemOptions.add(new ItemOption(210, 3));
            newItem.itemOptions.add(new ItemOption(30, khongthegiaodich));

            int randomOption = (int) (Math.random() * 100);
            newItem.itemOptions.add(new ItemOption(93, randomOption < 0.5 ? 0 : 15));

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);

            PlayerService.gI().sendInfoHpMpMoney(player);
            InventoryService.gI().sendItemBags(player);
        }
    }
    

    public void KhoBauHaiTac(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item itemused = InventoryService.gI().findItemBag(player, 1569);

            if (itemused == null || itemused.quantity < 1) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần dùng!");
                return;
            }

            int[] itemIds = {618, 619, 620, 621, 622, 623, 624, 625,626};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            int dame = randomInRange(10, 15);
            int hp = randomInRange(10, 15);
            int ki = randomInRange(10, 15);
            int khongthegiaodich = randomInRange(0, 0);

            newItem.itemOptions.add(new ItemOption(50, dame));
            newItem.itemOptions.add(new ItemOption(77, hp));
            newItem.itemOptions.add(new ItemOption(103, ki));
            newItem.itemOptions.add(new ItemOption(210, 2));
            newItem.itemOptions.add(new ItemOption(30, khongthegiaodich));

            int randomOption = (int) (Math.random() * 100);
            newItem.itemOptions.add(new ItemOption(93, randomOption < 0.5 ? 0 : 15));

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);

            PlayerService.gI().sendInfoHpMpMoney(player);
            InventoryService.gI().sendItemBags(player);
        }
    }


    public void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        } else if (tempId >= Shenron_Service.NGOC_RONG_1_SAO && tempId <= Shenron_Service.NGOC_RONG_7_SAO) {
            Shenron_Service.gI().openMenuSummonShenron(pl, 0);
        }
    }


    public void openCapsuleUI(Player pl) {
        pl.idMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }


    public void choseMapCapsule(Player pl, int index) {

        if (pl.idNRNM != -1) {
            Service.gI().sendThongBao(pl, "Không thể mang ngọc rồng này lên Phi thuyền");
            Service.gI().hideWaitDialog(pl);
            return;
        }

        int zoneId = -1;
        if (index > pl.mapCapsule.size() - 1 || index < 0) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            Service.gI().hideWaitDialog(pl);
            return;
        }
        Zone zoneChose = pl.mapCapsule.get(index);
        //Kiểm tra số lượng người trong khu

        if (zoneChose.getNumOfPlayers() > 25
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu(zoneChose.map.mapId)
                || MapService.gI().isMapHuyDiet(zoneChose.map.mapId)) {
            Service.gI().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        pl.changeMapVIP = true;
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }


    public void MapRiengTu(Player pl, Item item) {
        ChangeMapService.gI().changeMapBySpaceShip(pl, 164, -1, 870);
    }


}