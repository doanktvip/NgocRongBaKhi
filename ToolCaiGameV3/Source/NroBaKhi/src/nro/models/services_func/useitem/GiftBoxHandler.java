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
 * Handler tách từ UseItem.java: GiftBoxHandler
 */
public class GiftBoxHandler {

    private static GiftBoxHandler instance;
    private static final Random rand = new Random();

    private GiftBoxHandler() {
    }

    public static GiftBoxHandler gI() {
        if (instance == null) {
            instance = new GiftBoxHandler();
        }
        return instance;
    }

    public void openRuongGo(Player player) {
        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.GO_DAU_TRE, 1);
        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.GO_DAU_TRE1, 1);
        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.GO_DAU_TRE2, 1);
        Item ruongGo = InventoryService.gI().findItemBag(player, 570);
        if (ruongGo != null) {
            int level = InventoryService.gI().getParam(player, 72, 570);
            int requiredSlots = calculateRequiredEmptySlots(level);
            if (InventoryService.gI().getCountEmptyBag(player) < requiredSlots) {
                Service.gI().sendThongBao(player, "Cần ít nhất " + (requiredSlots - InventoryService.gI().getCountEmptyBag(player)) + " ô trống trong hành trang");
            } else {
                player.itemsWoodChest.clear();
                if (level == 0) {
                    InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
                    InventoryService.gI().sendItemBags(player);
                    Item item = ItemService.gI().createNewItem((short) 190);
                    item.quantity = 1;
                    InventoryService.gI().addItemBag(player, item);
                    InventoryService.gI().sendItemBags(player);

                    Service.gI().sendThongBao(player, "reward");
                    return;
                }
                int baseGoldAmount = 100 * level;
                int randomFactor = Util.nextInt(-15, 15);
                int goldAmount = baseGoldAmount + (baseGoldAmount * randomFactor / 100);

                Item itemGold = ItemService.gI().createNewItem((short) 190);
                itemGold.quantity = goldAmount * 1000;
                player.itemsWoodChest.add(itemGold);
                if (level >= 9) {
                    int quantity = 100 + (level - 9) * 20;
                    Item item77 = ItemService.gI().createNewItem((short) 77);
                    item77.quantity = quantity;
                    player.itemsWoodChest.add(item77);
                }

                // Phần thưởng đồ tại rương
                int clothesCount = 1;
                if (level >= 5 && level <= 8) {
                    clothesCount = 2;  // Nếu cấp độ từ 5 đến 8, thưởng 2 món đồ
                } else if (level >= 10 && level <= 12) {
                    clothesCount = 3;  // Nếu cấp độ từ 10 đến 12, thưởng 3 món đồ
                }

                // Tạo đồ thưởng (clothes) và thêm vào phần thưởng
                for (int i = 0; i < clothesCount; i++) {
                    int randItemId = randClothes(level);  // Lấy ID ngẫu nhiên của món đồ
                    Item rewardItem = ItemService.gI().createNewItem((short) randItemId);
                    List<Item.ItemOption> ops = ItemService.gI().getListOptionItemShop((short) randItemId);
                    if (ops != null && !ops.isEmpty()) {
                        rewardItem.itemOptions.addAll(ops);  // Thêm thuộc tính item
                    }
                    rewardItem.quantity = 1;  // Số lượng món đồ là 1
                    player.itemsWoodChest.add(rewardItem);  // Thêm món đồ vào phần thưởng
                }

                // Phần thưởng item ngẫu nhiên (từ rewardItems)
                int[] rewardItems = {17, 18, 19, 20, 380, 381, 382, 383, 384, 385, 1229};
                int rewardCount = 2;  // Số lượng item mặc định

                // Thay đổi số lượng phần thưởng tùy theo cấp độ
                if (level >= 5 && level <= 8) {
                    rewardCount = 3;  // Nếu cấp độ từ 5 đến 8, thưởng 3 item ngẫu nhiên
                } else if (level >= 10 && level <= 12) {
                    rewardCount = 4;  // Nếu cấp độ từ 10 đến 12, thưởng 4 item ngẫu nhiên
                }

                // Thêm item ngẫu nhiên vào phần thưởng
                Set<Integer> selectedItems = new HashSet<>();
                while (selectedItems.size() < rewardCount) {
                    int randItemId = rewardItems[Util.nextInt(0, rewardItems.length - 1)];
                    if (!selectedItems.contains(randItemId)) {
                        selectedItems.add(randItemId);
                        Item rewardItem = ItemService.gI().createNewItem((short) randItemId);
                        rewardItem.quantity = Util.nextInt(1, level);  // Số lượng item phụ thuộc vào cấp độ
                        player.itemsWoodChest.add(rewardItem);  // Thêm item vào phần thưởng
                    }
                }

                // Phần thưởng sao pha lê (nâng cấp)
                int saoPhaLeCount = (level > 9) ? 2 : 1;  // Nếu cấp độ > 9, thêm 2 sao phá lệ
                for (int i = 0; i < saoPhaLeCount; i++) {
                    int rand = Util.nextInt(0, 6);
                    Item level1 = ItemService.gI().createNewItem((short) (441 + rand));
                    level1.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));
                    level1.quantity = Util.nextInt(1, 3);  // Số lượng sao phá lệ
                    player.itemsWoodChest.add(level1);  // Thêm sao phá lệ vào phần thưởng
                }
                int dncCount = (level > 9) ? 2 : 1;
                for (int i = 0; i < dncCount; i++) {
                    int rand = Util.nextInt(0, 4);
                    Item dnc = ItemService.gI().createNewItem((short) (220 + rand));
                    dnc.itemOptions.add(new Item.ItemOption(71 - rand, 0));
                    dnc.quantity = Util.nextInt(1, level * 2);  // Số lượng đá nâng cấp phụ thuộc vào cấp độ
                    player.itemsWoodChest.add(dnc);  // Thêm đá nâng cấp vào phần thưởng            
                }

                // Trừ 1 rương gỗ
                InventoryService.gI().subQuantityItemsBag(player, ruongGo, 1);
                InventoryService.gI().sendItemBags(player);

                // Thêm các phần thưởng vào hành trang
                for (Item it : player.itemsWoodChest) {
                    InventoryService.gI().addItemBag(player, it);
                }
                InventoryService.gI().sendItemBags(player);

                // Cập nhật chỉ số rương gỗ
                player.indexWoodChest = player.itemsWoodChest.size() - 1;
                int i = player.indexWoodChest;
                if (i < 0) {
                    return;
                }
                Item itemWoodChest = player.itemsWoodChest.get(i);
                player.indexWoodChest--;
                String info = "|1|" + itemWoodChest.template.name;
                if (itemWoodChest.quantity > 1) {
                    info += " (x" + itemWoodChest.quantity + ")";
                }

                String info2 = "\n|2|";
                if (!itemWoodChest.itemOptions.isEmpty()) {
                    for (Item.ItemOption io : itemWoodChest.itemOptions) {
                        if (io.optionTemplate.id != 102 && io.optionTemplate.id != 73) {
                            info2 += io.getOptionString() + "\n";
                        }
                    }
                }
                info = (info2.length() > "\n|2|".length() ? (info + info2).trim() : info.trim()) + "\n|0|" + itemWoodChest.template.description;
                NpcService.gI().createMenuConMeo(player, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n"
                        + info.trim(), "OK" + (i > 0 ? " [" + i + "]" : ""));
            }
        }
    }


    public int calculateRequiredEmptySlots(int level) {
        // Khởi tạo số ô trống cần thiết
        int requiredSlots = 0;

        // Tính số lượng vàng
        int baseGoldAmount = 100 * level;
        int randomFactor = Util.nextInt(-15, 15);
        int goldAmount = baseGoldAmount + (baseGoldAmount * randomFactor / 100);

        // Vàng có ID 190, không tính vào số ô trống yêu cầu
        if (goldAmount > 0) {
            requiredSlots++;
        }

        // Tính phần thưởng quần áo
        int clothesCount = 1;
        if (level >= 5 && level <= 8) {
            clothesCount = 2;
        } else if (level >= 10 && level <= 12) {
            clothesCount = 3;
        }
        // Đếm số phần thưởng quần áo
        requiredSlots += clothesCount;

        // Tính phần thưởng item hỗ trợ
        int[] rewardItems = {17, 18, 19, 20, 380, 381, 382, 383, 384, 385, 1229};
        int rewardCount = 2;

        if (level >= 5 && level <= 8) {
            rewardCount = 3;
        } else if (level >= 10 && level <= 12) {
            rewardCount = 4;
        }
        // Đếm phần thưởng item hỗ trợ
        requiredSlots += rewardCount;

        // Tính sao pha lê (Số lượng 2 nếu level > 9)
        int saoPhaLeCount = (level > 9) ? 2 : 1;
        requiredSlots += saoPhaLeCount;

        // Tính đá nâng cấp (Số lượng 2 nếu level > 9)
        int dncCount = (level > 9) ? 2 : 1;
        requiredSlots += dncCount;

        // Trả về tổng số ô trống cần thiết
        return requiredSlots;
    }
    

    public int randClothes(int level) {
        int result = level - Util.nextInt(2, 4);
        if (result < 1) {
            result = 1;
        }
        return ConstItem.LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][result];
    }


    public void openCSKB(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] temp = {76, 188, 189, 190, 381, 382, 383, 384, 385};
            int[][] gold = {{5000, 20000}};
            byte index = (byte) Util.nextInt(0, temp.length - 1);
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            if (index <= 3) {
                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
                    pl.inventory.gold = Inventory.LIMIT_GOLD;
                }
                PlayerService.gI().sendInfoHpMpMoney(pl);
                icon[1] = 930;
            } else {
                Item it = ItemService.gI().createNewItem(temp[index]);
                it.itemOptions.add(new ItemOption(73, 0));
                InventoryService.gI().addItemBag(pl, it);
                icon[1] = it.template.iconID;
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBags(pl);

            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void OpenHopThanlinh(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item itemused = InventoryService.gI().findItemBag(player, itemUseiD);

            int[][] itemsByGenderAndType = {
                {555, 556, 562, 563, 561},
                {557, 558, 564, 565, 561},
                {559, 560, 566, 567, 561}
            };

            List<Item> allPreInitializedItems = new ArrayList<>();

            for (int genderIndex = 0; genderIndex < itemsByGenderAndType.length; genderIndex++) {
                short aoId = (short) itemsByGenderAndType[genderIndex][0];
                short quanId = (short) itemsByGenderAndType[genderIndex][1];
                short gangId = (short) itemsByGenderAndType[genderIndex][2];
                short giayId = (short) itemsByGenderAndType[genderIndex][3];
                short nhanId = (short) itemsByGenderAndType[genderIndex][4];

                Item aotl = ItemService.gI().createNewItem(aoId);
                RewardService.gI().initChiSoItem(aotl);
                aotl.itemOptions.add(new ItemOption(30, 1));
                allPreInitializedItems.add(aotl);

                Item wTl = ItemService.gI().createNewItem(quanId);
                RewardService.gI().initChiSoItem(wTl);
                wTl.itemOptions.add(new ItemOption(30, 1));
                allPreInitializedItems.add(wTl);

                Item gTl = ItemService.gI().createNewItem(gangId);
                RewardService.gI().initChiSoItem(gTl);
                gTl.itemOptions.add(new ItemOption(30, 1));
                allPreInitializedItems.add(gTl);

                Item jayTl = ItemService.gI().createNewItem(giayId);
                RewardService.gI().initChiSoItem(jayTl);
                jayTl.itemOptions.add(new ItemOption(30, 1));
                allPreInitializedItems.add(jayTl);

                Item RdTl = ItemService.gI().createNewItem(nhanId);
                RewardService.gI().initChiSoItem(RdTl);
                RdTl.itemOptions.add(new ItemOption(30, 1));
                allPreInitializedItems.add(RdTl);
            }

            Random random = new Random();
            Item chosenItem = allPreInitializedItems.get(random.nextInt(allPreInitializedItems.size()));
            InventoryService.gI().addItemBag(player, chosenItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn vừa nhận được 1 " + chosenItem.template.name + " Thần linh!");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có ít nhất 1 ô trống hành trang");
        }
    }


    public void OpenHopQuaThuong(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 1) {
            Item itemused = InventoryService.gI().findItemBag(player, itemUseiD);

            int[] itemIds = {1772, 1773};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            newItem.itemOptions.add(new ItemOption(50, 10));   // HP
            newItem.itemOptions.add(new ItemOption(77, 10));   // KI
            newItem.itemOptions.add(new ItemOption(103, 10)); // Sức đánh
            newItem.itemOptions.add(new ItemOption(14, 10));  // May mắn

            int randomOption = (int) (Math.random() * 100);
            if (randomOption < 0.5) {
                newItem.itemOptions.add(new ItemOption(93, 0));
            } else {
                newItem.itemOptions.add(new ItemOption(93, 3));
            }

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được item ngẫu nhiên");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có 5 ô trống trong hành trang.");
        }
    }


    public void OpenTrungRongNhi(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            Item itemused = InventoryService.gI().findItemBag(player, itemUseiD);

            if (itemused == null || itemused.quantity < 1) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần dùng!");
                return;
            }

            int[] itemIds = {1765, 1766, 1767, 1768, 1769, 1770, 1771};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            int hp = randomInRange(10, 18);
            int ki = randomInRange(5, 10);
            int damage = randomInRange(5, 10);
            int hsd = randomInRange(0, 1);

            newItem.itemOptions.add(new ItemOption(50, hp));
            newItem.itemOptions.add(new ItemOption(5, ki));
            newItem.itemOptions.add(new ItemOption(14, damage));
            newItem.itemOptions.add(new ItemOption(30, hsd));

            int randomOption = (int) (Math.random() * 100);
            newItem.itemOptions.add(new ItemOption(93, randomOption < 0.5 ? 0 : 15));

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);

            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được vật phẩm ngẫu nhiên!");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có ít nhất 5 ô trống trong hành trang.");
        }
    }


    public int randomInRange(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }


    public void OpenHopQuaVip(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 1) {
            Item itemused = InventoryService.gI().findItemBag(player, itemUseiD);

            int[] itemIds = {1761, 1731, 1732};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            newItem.itemOptions.add(new ItemOption(50, 25));   // HP
            newItem.itemOptions.add(new ItemOption(77, 25));   // KI
            newItem.itemOptions.add(new ItemOption(103, 25)); // Sức đánh
            newItem.itemOptions.add(new ItemOption(236, 10));  // May mắn

            int randomOption = (int) (Math.random() * 100);
            if (randomOption < 0.5) {
                newItem.itemOptions.add(new ItemOption(93, 0));
            } else {
                newItem.itemOptions.add(new ItemOption(93, 3));
            }

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được item ngẫu nhiên");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có 5 ô trống trong hành trang.");
        }
    }


    public void OpenHopQuaGokuDay(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 1) {
            Item itemused = InventoryService.gI().findItemBag(player, itemUseiD);

            int[] itemIds = {1588, 1589, 1595, 1587, 1593, 1590};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            newItem.itemOptions.add(new ItemOption(50, 25));   // HP
            newItem.itemOptions.add(new ItemOption(77, 25));   // KI
            newItem.itemOptions.add(new ItemOption(103, 25)); // Sức đánh
            newItem.itemOptions.add(new ItemOption(210, 4));  // May mắn

            int randomOption = (int) (Math.random() * 100);
            if (randomOption < 0.5) {
                newItem.itemOptions.add(new ItemOption(93, 0));
            } else {
                newItem.itemOptions.add(new ItemOption(93, 3));
            }

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được item ngẫu nhiên");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có 5 ô trống trong hành trang.");
        }
    }


    public void OpenHopQuaGokuDayVangNgoc(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 1) {
            Item itemused = InventoryService.gI().findItemBag(player, itemUseiD);

            int[] itemIds = {1588, 1589, 1595, 1587, 1593, 1590};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            newItem.itemOptions.add(new ItemOption(50, 20));   // HP
            newItem.itemOptions.add(new ItemOption(77, 20));   // KI
            newItem.itemOptions.add(new ItemOption(103, 20)); // Sức đánh
            newItem.itemOptions.add(new ItemOption(210, 2));  // May mắn

            int randomOption = (int) (Math.random() * 100);
            if (randomOption < 0.5) {
                newItem.itemOptions.add(new ItemOption(93, 0));
            } else {
                newItem.itemOptions.add(new ItemOption(93, 3));
            }

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được item ngẫu nhiên");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có 5 ô trống trong hành trang.");
        }
    }


    public void OpenHopQuaGokuDayVip(Player player, int itemUseId) {
        if (InventoryService.gI().getCountEmptyBag(player) >= 5) {
            Item itemUsed = InventoryService.gI().findItemBag(player, itemUseId);

            if (itemUsed == null) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần sử dụng.");
                return;
            }

            int[] itemIds = {1588, 1589, 1595, 1587, 1593, 1590};
            int randomIndex = Util.nextInt(0, itemIds.length - 1);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            // Thêm các option cố định
            newItem.itemOptions.add(new ItemOption(50, 27));   // HP
            newItem.itemOptions.add(new ItemOption(77, 27));   // KI
            newItem.itemOptions.add(new ItemOption(103, 27));  // Sức đánh
            newItem.itemOptions.add(new ItemOption(210, 4));   // Chỉ số ẩn

            int randomOption = Util.nextInt(0, 30);
            if (randomOption < 50) {
                newItem.itemOptions.add(new ItemOption(93, 0));
            } else {
                newItem.itemOptions.add(new ItemOption(93, 3));
            }

            // Thêm vào túi và cập nhật
            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemUsed, 1);
            InventoryService.gI().sendItemBags(player);

            Service.gI().sendThongBao(player, "Bạn đã nhận được " + newItem.template.name + "!");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có ít nhất 5 ô trống trong hành trang.");
        }
    }


    public void OpenHopQuaCadic(Player player, int itemUseiD) {
        if (InventoryService.gI().getCountEmptyBag(player) > 1) {
            Item itemused = InventoryService.gI().findItemBag(player, itemUseiD);

            int[] itemIds = {1741, 1742, 1743, 1744, 1745, 1746};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            newItem.itemOptions.add(new ItemOption(50, 27));   // HP
            newItem.itemOptions.add(new ItemOption(77, 27));   // KI
            newItem.itemOptions.add(new ItemOption(103, 27)); // Sức đánh
            newItem.itemOptions.add(new ItemOption(210, 4));  // May mắn

            int randomOption = (int) (Math.random() * 100);
            if (randomOption < 0.5) {
                newItem.itemOptions.add(new ItemOption(93, 0));
            } else {
                newItem.itemOptions.add(new ItemOption(93, 3));
            }

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);
            InventoryService.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được item ngẫu nhiên");
        } else {
            Service.gI().sendThongBao(player, "Yêu cầu có 5 ô trống trong hành trang.");
        }
    }


    public void NoelItemBox(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int spl = Util.nextInt(441, 445);
            int dnc = Util.nextInt(381, 384);
            int nr = Util.nextInt(17, 20);
            int nrBang = Util.nextInt(925, 931);
            int mts = Util.nextInt(1066, 1070);

            if (Util.isTrue(5, 90)) {
                int ruby = Util.nextInt(10, 20);
                pl.inventory.gem += ruby;
                PlayerService.gI().sendInfoHpMpMoney(pl);
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                InventoryService.gI().sendItemBags(pl);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + ruby + "  Ngọc");
            } else {
                int[] temp = {spl, dnc, nr, nrBang, mts, 533, 380};
                byte index = (byte) Util.nextInt(0, temp.length - 1);
                short[] icon = new short[2];
                icon[0] = item.template.iconID;
                Item it = ItemService.gI().createNewItem((short) temp[index]);
                /*
                # Item Vặt
                 */
                if (temp[index] >= 441 && temp[index] <= 443) {// sao pha le
                    it.itemOptions.add(new ItemOption(temp[index] - 346, 5));
                    it.quantity = Util.nextInt(1, 5);
                } else if (temp[index] >= 444 && temp[index] <= 445) {
                    it.itemOptions.add(new ItemOption(temp[index] - 346, 3));
                    it.quantity = Util.nextInt(1, 5);
                } else if (temp[index] >= 381 && temp[index] <= 384) { // da nang cap
                    it.quantity = Util.nextInt(1, 5);
                } else if (temp[index] >= 1066 && temp[index] <= 1070) { // da nang cap
                    it.quantity = Util.nextInt(1, 5);

                    /*
                   # Item Cải Trang
                     */
                } else if (temp[index] >= 387 && temp[index] <= 393) { // mu noel do
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(30, 40)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(30, 40)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(30, 40)));
                    it.itemOptions.add(new ItemOption(80, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(106, 0));
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                    it.itemOptions.add(new ItemOption(199, 0));
                    /*
                    # Item Pet
                  # Nếu Muốn Random Item Thì:  } else if (temp[index] == 936 && temp[index] == 936) {
                     */
                } else if (temp[index] == 936) { // tuan loc
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
                    /*
                    # Item Flag
                    # Nếu Muốn Random Item Thì:  } else if (temp[index] == 822 && temp[index] == 822) {
                     */
                } else if (temp[index] == 822) { // cay thong noel
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(3, 30)));
                    it.itemOptions.add(new ItemOption(30, 0));
                    it.itemOptions.add(new ItemOption(74, 0));
                    /*
                      # Item VanBay
                      # Nếu Muốn Random Item Thì:  } else if (temp[index] == 746 && temp[index] == 746) {
                     */
                } else if (temp[index] == 746) { // xe truot tuyet
                    it.itemOptions.add(new ItemOption(74, 0));
                    it.itemOptions.add(new ItemOption(30, 0));
                    if (Util.isTrue(99, 100)) {
                        it.itemOptions.add(new ItemOption(93, Util.nextInt(30, 360)));
                    }

                } else if (temp[index] == 821) {
                    it.itemOptions.add(new ItemOption(30, 0));
                } else {
                    it.itemOptions.add(new ItemOption(73, 0));
                }
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                icon[1] = it.template.iconID;
                InventoryService.gI().addItemBag(pl, it);
                InventoryService.gI().sendItemBags(pl);
            }
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void HopQuaChinhChu(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int tst = Util.nextInt(637, 642);
            int itc1 = Util.nextInt(1150, 1154);

            {
                int[] temp = {tst, itc1};
                byte index = (byte) Util.nextInt(0, temp.length - 1);
                short[] icon = new short[2];
                icon[0] = item.template.iconID;
                Item it = ItemService.gI().createNewItem((short) temp[index]);

                /*
                   # Item Cải Trang
                 */
                if (temp[index] == 1503 && temp[index] == 1504) { //oy tiu nun
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(19, 25)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(18, 25)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(17, 25)));
                    it.itemOptions.add(new ItemOption(47, Util.nextInt(13, 12)));
                    it.itemOptions.add(new ItemOption(106, 0));
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));

                } else if (temp[index] == 681) { // tuan loc
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(19, 25)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(18, 25)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(17, 25)));
                    it.itemOptions.add(new ItemOption(47, Util.nextInt(13, 12)));
                    it.itemOptions.add(new ItemOption(106, 0));
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
                }
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                icon[1] = it.template.iconID;
                InventoryService.gI().addItemBag(pl, it);
                InventoryService.gI().sendItemBags(pl);
            }
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void HopQuaNheNhang(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int tst = Util.nextInt(381, 385);
            int itc1 = Util.nextInt(628, 636);

            {
                int[] temp = {tst, itc1};
                byte index = (byte) Util.nextInt(0, temp.length - 1);
                short[] icon = new short[2];
                icon[0] = item.template.iconID;
                Item it = ItemService.gI().createNewItem((short) temp[index]);

                /*
                   # Item Cải Trang
                 */
                if (temp[index] == 1503 && temp[index] == 1504) { //oy tiu nun
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(19, 25)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(18, 25)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(17, 25)));
                    it.itemOptions.add(new ItemOption(47, Util.nextInt(13, 12)));
                    it.itemOptions.add(new ItemOption(106, 0));
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));

                } else if (temp[index] == 681) { // tuan loc
                    it.itemOptions.add(new ItemOption(50, Util.nextInt(19, 25)));
                    it.itemOptions.add(new ItemOption(77, Util.nextInt(18, 25)));
                    it.itemOptions.add(new ItemOption(103, Util.nextInt(17, 25)));
                    it.itemOptions.add(new ItemOption(47, Util.nextInt(13, 12)));
                    it.itemOptions.add(new ItemOption(106, 0));
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
                }
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                icon[1] = it.template.iconID;
                InventoryService.gI().addItemBag(pl, it);
                InventoryService.gI().sendItemBags(pl);
            }
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void BlackGokuItemBoxEventNoel(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int hanhtinh = pl.gender;
            int[] caitrangdietquy = {1087, 1088, 1089, 1090, 1091}; // cải trang diệt wỹ

            int itemBlackGoku = Util.nextInt(0, 3) == 0 ? 746
                    : // 1/3%
                    Util.nextInt(0, 3) == 1 ? (hanhtinh == 0 ? 1155 : hanhtinh == 1 ? 1157 : 1156)
                    : // Cải trang Noel
                    Util.nextInt(0, 3) == 2 ? (hanhtinh == 0 ? 1018 : hanhtinh == 1 ? 1019 : 1020)
                    : // Cải trang Broly
                    caitrangdietquy[Util.nextInt(0, caitrangdietquy.length - 1)]; // Cải Trang Diệt Wỹ

            Item it = ItemService.gI().createNewItem((short) itemBlackGoku);
            if (itemBlackGoku == 746) { // Xe trượt tuyết
                if (Util.isTrue(1, 100)) { // 1% ra HSD vĩnh viễn
                    it.itemOptions.add(new ItemOption(73, 0)); // Option 73 với giá trị 0: HSD vĩnh viễn
                } else {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30))); // 99% ra HSD 7-30 ngày
                }
            } else if (itemBlackGoku == 1155 || itemBlackGoku == 1156 || itemBlackGoku == 1157
                    || itemBlackGoku == 1018 || itemBlackGoku == 1019 || itemBlackGoku == 1020) { // Cải trang Noel hoặc Broly
                it.itemOptions.add(new ItemOption(50, 23));
                it.itemOptions.add(new ItemOption(77, 23));
                it.itemOptions.add(new ItemOption(103, 23));
                if (Util.isTrue(95, 100)) {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3))); // 90% ra option 93
                } else {
                    it.itemOptions.add(new ItemOption(73, 0)); // 10% ra option 73
                }
            } else if (itemBlackGoku >= 1087 && itemBlackGoku <= 1191) { // Diệt Quỷ
                it.itemOptions.add(new ItemOption(50, 22));
                it.itemOptions.add(new ItemOption(77, 21));
                it.itemOptions.add(new ItemOption(103, 21));
                if (Util.isTrue(95, 100)) {
                    it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3))); // 90% ra HSD (1-3 ngày)
                } else {
                    it.itemOptions.add(new ItemOption(73, 0)); // 5% không ra HSD
                }
            }

            // Cập nhật túi đồ
            InventoryService.gI().subQuantityItemsBag(pl, item, 1); // Trừ 1 Item Box
            InventoryService.gI().addItemBag(pl, it); // Thêm item vào túi
            InventoryService.gI().sendItemBags(pl); // send
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void C5(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int[] itemList = {1087, 1088, 1089, 1090, 1091};

            int itemBlackGoku = itemList[Util.nextInt(0, itemList.length - 1)];
            Item it = ItemService.gI().createNewItem((short) itemBlackGoku);
            it.itemOptions.add(new ItemOption(50, Util.nextInt(1, 16))); //
            it.itemOptions.add(new ItemOption(77, Util.nextInt(1, 17))); // 
            it.itemOptions.add(new ItemOption(103, Util.nextInt(1, 15))); // 
            it.itemOptions.add(new ItemOption(95, Util.nextInt(1, 5))); //
            it.itemOptions.add(new ItemOption(96, Util.nextInt(1, 5))); // 

            int[] options = {94, 97, 108};
            int randomOption = options[Util.nextInt(0, options.length - 1)];
            it.itemOptions.add(new ItemOption(randomOption, Util.nextInt(3, 5)));
            if (Util.isTrue(98, 100)) {
                it.itemOptions.add(new ItemOption(93, 5));
            }
            // Cập nhật túi đồ
            InventoryService.gI().subQuantityItemsBag(pl, item, 1); // Trừ 1 Item Box
            InventoryService.gI().addItemBag(pl, it); // Thêm item vào túi
            InventoryService.gI().sendItemBags(pl); // Gửi cập nhật túi đồ
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void C7(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int[] itemList = {1087, 1088, 1089, 1090, 1091};

            int itemBlackGoku = itemList[Util.nextInt(0, itemList.length - 1)];
            Item it = ItemService.gI().createNewItem((short) itemBlackGoku);
            it.itemOptions.add(new ItemOption(50, Util.nextInt(1, 16))); //
            it.itemOptions.add(new ItemOption(77, Util.nextInt(1, 17))); // 
            it.itemOptions.add(new ItemOption(103, Util.nextInt(1, 15))); // 
            it.itemOptions.add(new ItemOption(95, Util.nextInt(1, 5))); //
            it.itemOptions.add(new ItemOption(96, Util.nextInt(1, 5))); // 

            int[] options = {94, 97, 108};
            int randomOption = options[Util.nextInt(0, options.length - 1)];
            it.itemOptions.add(new ItemOption(randomOption, Util.nextInt(3, 5)));
            if (Util.isTrue(100, 100)) {
                it.itemOptions.add(new ItemOption(93, 7));
            }
            // Cập nhật túi đồ
            InventoryService.gI().subQuantityItemsBag(pl, item, 1); // Trừ 1 Item Box
            InventoryService.gI().addItemBag(pl, it); // Thêm item vào túi
            InventoryService.gI().sendItemBags(pl); // Gửi cập nhật túi đồ
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void ChuLunBox(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            // Mảng vật phẩm không giới hạn hành tinh
            int[] itemList = {1158, 1159, 1160, 1161, 1162, 1163, 1164};

            // Chọn ngẫu nhiên một vật phẩm từ danh sách itemList
            int itemBlackGoku = itemList[Util.nextInt(0, itemList.length - 1)];

            // Tạo vật phẩm mới
            Item it = ItemService.gI().createNewItem((short) itemBlackGoku);

            // Thêm các item options cố định
            it.itemOptions.add(new ItemOption(50, 11)); // Option 50 = 11
            it.itemOptions.add(new ItemOption(77, 13)); // Option 77 = 13
            it.itemOptions.add(new ItemOption(103, 13)); // Option 103 = 13        

            int[] options = {94, 97, 108}; // Mảng chứa các option
            int randomOption = options[Util.nextInt(0, options.length - 1)]; // Chọn ngẫu nhiên một option từ mảng
            it.itemOptions.add(new ItemOption(randomOption, Util.nextInt(3, 5))); // Thêm option ngẫu nhiên
            if (Util.isTrue(98, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3))); // 90% ra HSD (1-3 ngày)
            } else {
                it.itemOptions.add(new ItemOption(73, 0)); // 5% không ra HSD
            }
            InventoryService.gI().subQuantityItemsBag(pl, item, 1); // Trừ 1 Item Box
            InventoryService.gI().addItemBag(pl, it); // Thêm item vào túi
            InventoryService.gI().sendItemBags(pl); // Gửi cập nhật túi đồ
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void NonNoelDo(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            int hanhtinh = pl.gender;

            // Chỉ giữ lại cải trang Noel
            int itemBlackGoku = (hanhtinh == 0 ? 387 : hanhtinh == 1 ? 390 : 393);

            // Tạo vật phẩm
            Item it = ItemService.gI().createNewItem((short) itemBlackGoku);
            it.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
            it.itemOptions.add(new ItemOption(80, Util.nextInt(30, 50)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
            it.itemOptions.add(new ItemOption(106, 1));

            // 95% ra HSD 1-3 ngày, 5% ra vĩnh viễn
            if (Util.isTrue(95, 100)) {
                it.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3))); // 95% ra option 93
            } else {
                it.itemOptions.add(new ItemOption(73, 0)); // 5% ra option 73
            }
            // Cập nhật túi đồ
            InventoryService.gI().subQuantityItemsBag(pl, item, 1); // Trừ 1 Item Box
            InventoryService.gI().addItemBag(pl, it); // Thêm item vào túi
            InventoryService.gI().sendItemBags(pl); // send
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void CapsuleKichHoat(Player pl, Item item) {
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada");
    }


    public void openLevel12EquipmentBox(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) < 5) {
            Service.gI().sendThongBao(player, "Cần ít nhất 5 ô trống trong hành trang");
            return;
        }
        NpcService.gI().createMenuConMeo(player, ConstNpc.HOP_TRANG_BI_CAP_12, -1,
                "Hãy chọn hành tinh của bộ trang bị cấp 12",
                "Trái Đất", "Namếc", "Xayda", "Đóng");
    }


    public void receiveLevel12EquipmentSet(Player player, int gender) {
        if (gender < ConstPlayer.TRAI_DAT || gender > ConstPlayer.XAYDA) {
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(player) < 5) {
            Service.gI().sendThongBao(player, "Cần ít nhất 5 ô trống trong hành trang");
            return;
        }

        Item box = InventoryService.gI().findItemBag(player, ConstNpc.HOP_TRANG_BI_CAP_12);
        if (box == null || box.quantity < 1) {
            Service.gI().sendThongBao(player, "Bạn không có hộp trang bị cấp 12");
            return;
        }

        int[][] equipmentByGender = {
            {233, 245, 257, 269, 281}, // Jean Calích - Trái Đất
            {237, 249, 261, 273, 281}, // Vàng Zealot - Namếc
            {241, 253, 265, 277, 281}  // Lưỡng Long - Xayda
        };

        List<Item> equipmentSet = new ArrayList<>();
        for (int i = 0; i < equipmentByGender[gender].length; i++) {
            int itemId = equipmentByGender[gender][i];
            Item equipment = ItemService.gI().createNewItem((short) itemId);
            RewardService.gI().initChiSoItem(equipment, 90, 120);
            equipment.itemOptions.add(new ItemOption(200, 0));
            equipment.itemOptions.add(new ItemOption(201, 0));
            equipment.itemOptions.add(new ItemOption(202, 0));
            equipment.itemOptions.add(new ItemOption(203, 0));
            equipmentSet.add(equipment);
        }

        InventoryService.gI().subQuantityItemsBag(player, box, 1);
        for (Item equipment : equipmentSet) {
            InventoryService.gI().addItemBag(player, equipment);
        }
        InventoryService.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Bạn đã nhận đủ bộ trang bị cấp 12");
    }


    public void TuiVang(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            pl.inventory.gold += Util.nextInt(100000, 10000000);
            int itemBlackGoku = 190;
            Item it = ItemService.gI().createNewItem((short) itemBlackGoku);
            int randomValue = Util.nextInt(3, 333);
            it.itemOptions.add(new ItemOption(1, randomValue));
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().addItemBag(pl, it);
            InventoryService.gI().sendItemBags(pl);
            Service.gI().sendThongBao(pl, "Happy");
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


    public void RuongNgocRong(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            // Số ngẫu nhiên từ 0 đến 100 để quyết định tỷ lệ
            int random = Util.nextInt(0, 100);

            int itemBlackGoku;

            // 85% xuất hiện item 20, 19, 18, 17
            if (random < 85) {
                int[] itemList = {20, 19, 18, 17};  // Các item có tỉ lệ xuất hiện 85%
                itemBlackGoku = itemList[Util.nextInt(0, itemList.length - 1)];
            } // 10% xuất hiện item 16
            else if (random < 95) {
                itemBlackGoku = 16;  // Item 16 có tỉ lệ 10%
            } // 5% xuất hiện item 14 hoặc 15
            else {
                itemBlackGoku = Util.nextInt(14, 15);  // Item 14 hoặc 15 có tỉ lệ 5%
            }

            // Tạo vật phẩm mới từ ID đã chọn
            Item it = ItemService.gI().createNewItem((short) itemBlackGoku);

            // Kiểm tra nếu người chơi có item 1561 (chìa khóa)
            Item item1561 = InventoryService.gI().findItem(pl.inventory.itemsBag, 1561);
            if (item1561 != null) {
                // Trừ vật phẩm và thêm vật phẩm mới vào túi đồ
                InventoryService.gI().subQuantityItemsBag(pl, item, 1); // Trừ 1 Item Box
                InventoryService.gI().subQuantityItemsBag(pl, item1561, 1); // Trừ 1 Item Box
                InventoryService.gI().addItemBag(pl, it); // Thêm item vào túi
                InventoryService.gI().sendItemBags(pl); // Gửi cập nhật túi đồ
                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
            } else {
                Service.gI().sendThongBao(pl, "Bạn không có chìa khoá vàng");
            }
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }


}