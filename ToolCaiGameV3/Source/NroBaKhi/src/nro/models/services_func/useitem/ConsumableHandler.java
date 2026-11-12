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
 * Handler tách từ UseItem.java: ConsumableHandler
 */
public class ConsumableHandler {

    private static ConsumableHandler instance;
    private static final Random rand = new Random();

    private ConsumableHandler() {
    }

    public static ConsumableHandler gI() {
        if (instance == null) {
            instance = new ConsumableHandler();
        }
        return instance;
    }

    public int randomInRange(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }


    public void eatPea(Player player) {
        if (!Util.canDoWithTime(player.lastTimeEatPea, 1000)) {
            return;
        }
        player.lastTimeEatPea = System.currentTimeMillis();
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            int hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 1000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(player.nPoint.hp + hpKiHoiPhuc);
            player.nPoint.setMp(player.nPoint.mp + hpKiHoiPhuc);
            PlayerService.gI().sendInfoHpMp(player);
            Service.gI().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp(player.pet.nPoint.hp + hpKiHoiPhuc);
                player.pet.nPoint.setMp(player.pet.nPoint.mp + hpKiHoiPhuc);
                Service.gI().sendInfoPlayerEatPea(player.pet);
                Service.gI().chatJustForMe(player, player.pet, "Cám ơn sư phụ");
            }

            InventoryService.gI().subQuantityItemsBag(player, pea, 1);
            InventoryService.gI().sendItemBags(player);
        }
    }


    public void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.gI().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }


    public void PhaoBong(Player pl, Item item) {
        BadgesTaskService.updateCountBagesTask(pl, ConstTaskBadges.XSMAX, 1);
        int[][] gold = {{5000, 20000}};
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
        if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
            pl.inventory.gold = Inventory.LIMIT_GOLD;
        }
        Service.gI().LogicEffect(pl, 62, 1, -1, 1, 1, 15000);
        Service.gI().LogicEffect(pl, 63, 1, -1, 1, 1, 5000);
        Service.gI().LogicEffect(pl, 64, 1, -1, 1, 1, 5000); // eff này live
        Service.gI().LogicEffect(pl, 65, 1, -1, 1, 1, 5000);

        /*    pl.point_sukien1 += 1;
        if (!Manager.isTopSukien1Changed) {
            Manager.isTopSukien1Changed = true;
        } */
        Item removeItem = InventoryService.gI().findItemBag(pl, 1575);
        if (removeItem != null) {
            InventoryService.gI().subQuantityItemsBag(pl, removeItem, 1);
        }

        PlayerService.gI().sendInfoHpMpMoney(pl);
        InventoryService.gI().sendItemBags(pl);
    }


    public void PhaoBongVip(Player pl, Item item) {
        BadgesTaskService.updateCountBagesTask(pl, ConstTaskBadges.XSMAX, 1);
        int[][] gold = {{500000, 2000000}};
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
        if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
            pl.inventory.gold = Inventory.LIMIT_GOLD;
        }
        Service.gI().LogicEffect(pl, 62, 1, -1, 1, 1, 15000);
        Service.gI().LogicEffect(pl, 63, 1, -1, 1, 1, 5000);
        Service.gI().LogicEffect(pl, 64, 1, -1, 1, 1, 5000); // eff này live
        Service.gI().LogicEffect(pl, 65, 1, -1, 1, 1, 5000);
        Service.gI().LogicEffect(pl, 65, 1, -1, 1, 1, 5000);
        Service.gI().LogicEffect(pl, 65, 1, -1, 1, 1, 5000);
        Service.gI().LogicEffect(pl, 65, 1, -1, 1, 1, 5000);
        Service.gI().LogicEffect(pl, 65, 1, -1, 1, 1, 5000);

        /*  pl.point_sukien += 1;
        if (!Manager.isTopSukienChanged) {
            Manager.isTopSukienChanged = true;
        }*/
        Item removeItem = InventoryService.gI().findItemBag(pl, 1576);
        if (removeItem != null) {
            InventoryService.gI().subQuantityItemsBag(pl, removeItem, 1);
        }

        PlayerService.gI().sendInfoHpMpMoney(pl);
        InventoryService.gI().sendItemBags(pl);
    }


    public void KemTraiCay(Player player, Item item) {
        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.XSMAX, 1);
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            Item itemused = InventoryService.gI().findItemBag(player, 1609);

            if (itemused == null || itemused.quantity < 1) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần dùng!");
                return;
            }

            int[] itemIds = {1804};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            RewardService.gI().initChiSoItem(newItem);

            int dame = randomInRange(20, 28);
            int hp = randomInRange(20, 30);
            int ki = randomInRange(20, 30);
            int khongthegiaodich = randomInRange(0, 0);

            newItem.itemOptions.add(new ItemOption(50, dame));
            newItem.itemOptions.add(new ItemOption(77, hp));
            newItem.itemOptions.add(new ItemOption(103, ki));
            newItem.itemOptions.add(new ItemOption(30, khongthegiaodich));

            int randomOption = (int) (Math.random() * 100);
            newItem.itemOptions.add(new ItemOption(93, randomOption < 0.5 ? 0 : 15));

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);

            player.point_sukien2 += 1;
            if (!Manager.isTopSukien2Changed) {
                Manager.isTopSukien2Changed = true;
            }

            PlayerService.gI().sendInfoHpMpMoney(player);
            InventoryService.gI().sendItemBags(player);
        }
    }


    public void QuaThieuNhi(Player player, Item item) {
        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.XSMAX, 1);
        if (InventoryService.gI().getCountEmptyBag(player) >= 0) {
            Item itemused = InventoryService.gI().findItemBag(player, 1608);
            if (itemused == null || itemused.quantity < 1) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần dùng!");
                return;
            }

            int[] itemIds = {1807, 1599, 1600, 1601, 1602};
            int randomIndex = (int) (Math.random() * itemIds.length);
            short randomItemId = (short) itemIds[randomIndex];

            Item newItem = ItemService.gI().createNewItem(randomItemId);
            if (newItem == null) {
                Service.gI().sendThongBao(player, "Không thể tạo vật phẩm. Liên hệ admin.");
                return;
            }

            RewardService.gI().initChiSoItem(newItem);

            int dame = randomInRange(20, 25);
            int hp = randomInRange(20, 25);
            int ki = randomInRange(20, 25);
            int khongthegiaodich = 0;

            newItem.itemOptions.add(new ItemOption(50, dame));
            newItem.itemOptions.add(new ItemOption(77, hp));
            newItem.itemOptions.add(new ItemOption(103, ki));
            newItem.itemOptions.add(new ItemOption(210, 4));
            newItem.itemOptions.add(new ItemOption(30, khongthegiaodich));
            newItem.itemOptions.add(new ItemOption(93, Math.random() < 0.5 ? 0 : 15));

            InventoryService.gI().addItemBag(player, newItem);
            InventoryService.gI().subQuantityItemsBag(player, itemused, 1);

            player.point_sukien += 1;
            Manager.isTopSukienChanged = true;

            PlayerService.gI().sendInfoHpMpMoney(player);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.gI().sendThongBao(player, "Cần ít nhất 5 ô trống trong túi!");
        }
    }


    public void ThucAnChoThan(Player player, Item item) {
        if (InventoryService.gI().getCountEmptyBag(player) > 4) {
            Item itemUsed = InventoryService.gI().findItemBag(player, item.template.id);

            if (itemUsed == null || itemUsed.quantity < 1) {
                Service.gI().sendThongBao(player, "Bạn không có vật phẩm cần dùng!");
                return;
            }

            int id = itemUsed.template.id;
            if ((id == 1789 || id == 1799 || id == 1800 || id == 1801 || id == 1802) && itemUsed.quantity >= 99) {

                InventoryService.gI().subQuantityItemsBag(player, itemUsed, 99);

                Item newItem = ItemService.gI().createNewItem((short) 1805);
                InventoryService.gI().addItemBag(player, newItem);

                Service.gI().sendThongBao(player, "Bạn đã nhận được 1 " + newItem.template.name + "!");
            } else {
                Service.gI().sendThongBao(player, "Số lượng vật phẩm không đủ hoặc không đúng loại!");
                return;
            }

            PlayerService.gI().sendInfoHpMpMoney(player);
            InventoryService.gI().sendItemBags(player);
        } else {
            Service.gI().sendThongBao(player, "Hành trang không đủ chỗ trống!");
        }
    }


    public void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 379: // máy dò capsule
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 1635: // co bon la
                pl.itemTime.lastTimeUseCoBonLa = System.currentTimeMillis();
                pl.itemTime.isUseCoBonLa = true;
                break;
            case 1614: // nuoc mia 1
                if (pl.itemTime.isUseNuocMia2 || pl.itemTime.isUseNuocMia3) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeUseNuocMia1 = System.currentTimeMillis();
                pl.itemTime.isUseNuocMia1 = true;
                Service.gI().point(pl);
                break;
            case 1615: // nuoc mia 1
                if (pl.itemTime.isUseNuocMia1 || pl.itemTime.isUseNuocMia3) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeUseNuocMia2 = System.currentTimeMillis();
                pl.itemTime.isUseNuocMia2 = true;
                Service.gI().point(pl);
                break;
            case 1616: // nuoc mia 1
                if (pl.itemTime.isUseNuocMia1 || pl.itemTime.isUseNuocMia2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeUseNuocMia3 = System.currentTimeMillis();
                pl.itemTime.isUseNuocMia3 = true;
                Service.gI().point(pl);
                break;
            case 381: // cuồng nộ
                if (pl.itemTime.isUseCuongNo2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.gI().point(pl);
                break;
            case 382: //bổ huyết
                if (pl.itemTime.isUseBoHuyet2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                Service.gI().point(pl);
                break;
            case 383: //bổ khí
                if (pl.itemTime.isUseBoKhi2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                Service.gI().point(pl);
                break;
            case 384: //giáp xên
                if (pl.itemTime.isUseGiapXen2) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                Service.gI().point(pl);
                break;
            case 385: // ẩn danh
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 764: // Khau trang
                pl.itemTime.lastTimeKhauTrang = System.currentTimeMillis();
                pl.itemTime.isUseKhauTrang = true;
                break;
            case 1150: // cuồng nộ 2
                if (pl.itemTime.isUseCuongNo) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                Service.gI().point(pl);
                break;

            case 1151: // bổ khí 2
                if (pl.itemTime.isUseBoKhi) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                Service.gI().point(pl);
                break;

            case 1152: // bổ huyết 2
                if (pl.itemTime.isUseBoHuyet) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                Service.gI().point(pl);
                break;

            case 1153: // giáp xên 2
                if (pl.itemTime.isUseGiapXen) {
                    Service.gI().sendThongBao(pl, "Chỉ có thể sự dụng cùng lúc 1 vật phẩm bổ trợ cùng loại");
                    return;
                }
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                Service.gI().point(pl);
                break;

            case 1154: // an danh
                pl.itemTime.lastTimeAnDanh2 = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh2 = true;
                break;

            case 638: //Commeson
                pl.itemTime.lastTimeUseCMS = System.currentTimeMillis();
                pl.itemTime.isUseCMS = true;
                break;
            case 1233: //Nồi cơm điện
                pl.itemTime.lastTimeUseNCD = System.currentTimeMillis();
                pl.itemTime.isUseNCD = true;
                break;
            case 579:
            case 1045: // Đuôi khỉ
                pl.itemTime.lastTimeUseDK = System.currentTimeMillis();
                pl.itemTime.isUseDK = true;
                break;
            case 663: //bánh pudding
            case 664: //xúc xíc
            case 665: //kem dâu
            case 666: //mì ly
            case 667: //sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
            case 880:
            case 881:
            case 882:
                pl.itemTime.lastTimeEatMeal2 = System.currentTimeMillis();
                pl.itemTime.isEatMeal2 = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal2);
                pl.itemTime.iconMeal2 = item.template.iconID;
                break;

            case 1532: //máy dò đồ
                pl.itemTime.lastTimeUseKhoBauX2 = System.currentTimeMillis();
                pl.itemTime.isUseKhoBauX2 = true;

//            case 2109: //máy dò đồ
//                pl.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis();
//                pl.itemTime.isUseMayDo2 = true;
                break;
            case 1628: //máy dò đồ
                long currentTime = System.currentTimeMillis();

                // +++
                if (pl.itemTime.isUseBuaSanta) {
                    pl.itemTime.lastTimeBuaSanta += 1_800_000; // 30p
                } else {
                    //null
                    pl.itemTime.lastTimeBuaSanta = currentTime + 1; // 0
                    pl.itemTime.isUseBuaSanta = true;
                }
                break;

        }
        Service.gI().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
    }


    public void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }


    public void ItemManhGiay(Player pl, Item item) {
        if (pl.winSTT && !Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            Service.gI().sendThongBao(pl, "Hãy gặp thần mèo Karin để sử dụng");
            return;
        } else if (pl.winSTT && Util.isAfterMidnight(pl.lastTimeWinSTT)) {
            pl.winSTT = false;
            pl.callBossPocolo = false;
            pl.zoneSieuThanhThuy = null;
        }
        NpcService.gI().createMenuConMeo(pl, item.template.id, 564, "Đây chính là dấu hiệu riêng của...\nĐại Ma Vương Pôcôlô\nĐó là một tên quỷ dữ đội lốt người, một kẻ đại gian ác\ncó sức mạnh vô địch và lòng tham không đáy...\nĐối phó với hắn không phải dễ\nCon có chắc chắn muốn tìm hắn không?", "Đồng ý", "Từ chối");
    }


    public void XuongCho(Player pl, Item item) {
        List<Player> bosses = pl.zone.getBosses();
        boolean checkSoi = false;

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.SOI_HEC_QUYN && !pl.isDie()) {
                    checkSoi = true;
                }
            }
        }

        if (!checkSoi) {
            Service.gI().sendThongBao(pl, "Không tìm thấy Sói hẹc quyn");
            return;
        }

        synchronized (bosses) {
            for (Player bossPlayer : bosses) {
                if (bossPlayer.id == BossID.SOI_HEC_QUYN) {
                    Boss soihecQuyn = (Boss) bossPlayer;
                    if (soihecQuyn != null) {
                        if (((SoiHecQuyn) soihecQuyn).KiemTraNhatXuong()) {
                            Service.gI().sendThongBao(pl, "Sói đã no rồi");
                            continue;
                        } else {
                            ((SoiHecQuyn) soihecQuyn).NhatXuong();
                            Service.gI().chat(soihecQuyn, "Ê, Cục xương ngon quá");
                        }

                        ItemMap itemMap = null;
                        int x = pl.location.x;
                        if (x < 0 || x >= pl.zone.map.mapWidth) {
                            return;
                        }
                        int y = pl.zone.map.yPhysicInTop(x, pl.location.y - 24);
                        itemMap = new ItemMap(pl.zone, 460, 1, x, y, pl.id);
                        BadgesTaskService.updateCountBagesTask(pl, ConstTaskBadges.KE_THAO_TUNG_SOI, 1);
                        itemMap.isPickedUp = true;
                        itemMap.createTime -= 23000;
                        if (itemMap != null) {
                            Service.gI().dropItemMap(pl.zone, itemMap);
                        }

                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                        InventoryService.gI().sendItemBags(pl);

                        if (Util.nextInt(4) < 3) { // 75% cơ hội
                            int rand = Util.nextInt(0, 6); // Random từ 0 đến 6
                            short idItem = (short) (rand + 441); // Item 441 + rand
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(95 + rand, (rand == 3 || rand == 4) ? 3 : 5));

                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);
                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        } else {
                            short idItem = 459; // Item 459
                            Item it = ItemService.gI().createNewItem(idItem);
                            it.itemOptions.add(new Item.ItemOption(112, 80));
                            it.itemOptions.add(new Item.ItemOption(93, 90));
                            it.itemOptions.add(new Item.ItemOption(20, Util.nextInt(10000)));
                            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                                InventoryService.gI().addItemBag(pl, it);
                                Service.gI().sendThongBao(pl, "Bạn vừa nhận được " + it.template.name);
                            } else {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống.");
                            }
                        }

                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ItemMapService.gI().removeItemMapAndSendClient(itemMap);
                        ((SoiHecQuyn) soihecQuyn).leaveMapNew();
                    }

                }
            }

            InventoryService.gI().sendItemBags(pl);
        }

    }


    public void VeTangNgoc(Player pl, Item item) {
        NpcService.gI().createMenuConMeo(pl, 900, -1, "Nhập tên người chơi mà bạn muốn tặng ngọc", "Tặng", "Từ chối");
    }


    public void LoaTheGioi(Player pl, Item item) {
        NpcService.gI().createMenuConMeo(pl, 901, -1, "Nhập Nội Dung Muốn Chát", "Ok", "Từ chối");
    }


    public void ItemSieuThanThuy(Player pl, Item item) {
        long tnsm = 5_000_000;
        int n = 0;
        switch (item.template.id) {
            case 727:
                n = 2;
                break;
            case 728:
                n = 10;
                break;
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        if (Util.isTrue(90, 100)) {
            Service.gI().sendThongBao(pl, "Bạn đã bị chết vì độc của thuốc tăng lực siêu thần thủy.");
            pl.setDie();
        } else {
            for (int i = 0; i < n; i++) {
                Service.gI().addSMTN(pl, (byte) 2, tnsm, true);
            }
        }
    }


}