package nro.models.services_func;

import nro.models.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.map.service.NpcService;
import nro.models.network.Message;
import nro.models.network.MySession;
import nro.models.npc.MabuEgg;
import nro.models.player.Player;
import nro.models.services.InventoryService;
import nro.models.services.ItemService;
import nro.models.services.Service;
import nro.models.services.TaskService;
import nro.models.services_dungeon.NgocRongNamecService;
import nro.models.services_func.useitem.ConsumableHandler;
import nro.models.services_func.useitem.GiftBoxHandler;
import nro.models.services_func.useitem.MiniPetHandler;
import nro.models.services_func.useitem.SkillAndPetHandler;
import nro.models.services_func.useitem.TeleportAndRadarHandler;
import nro.models.utils.Logger;
import nro.models.utils.Util;

/**
 * Bộ điều phối trung tâm (Facade/Router) cho toàn bộ thao tác sử dụng vật phẩm.
 * Đã được tái cấu trúc từ 2.885 dòng xuống ~380 dòng bằng cách tách 5 lớp Handler
 * chuyên biệt trong gói nro.models.services_func.useitem.
 */
public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;
    private static final int ITEM_BAG_TO_RUONG = 8;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;

    private UseItem() {
    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(MySession session, Message msg) {
        Player player = session.player;
        if (player == null) {
            return;
        }
        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            if (index == -1) {
                return;
            }
            switch (type) {
                case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryService.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryService.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryService.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryService.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryService.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryService.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryService.gI().itemPetBodyToBag(player, index);
                    break;
            }
            if (player.setClothes != null) {
                player.setClothes.setup();
            }
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.gI().sendFlagBag(player);
            Service.gI().point(player);
            Service.gI().sendSpeedPlayer(player, -1);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    public Item finditem(Player player, int iditem) {
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == iditem) {
                return item;
            }
        }
        return null;
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg = null;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            switch (type) {
                case DO_USE_ITEM -> {
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            if (index < 0 || index >= player.inventory.itemsBag.size()) {
                                return;
                            }
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học " + item.template.name + "?");
                                    player.sendMessage(msg);
                                } else if (item.template.id == 570) {
                                    if (!Util.isAfterMidnight(player.lastTimeRewardWoodChest)) {
                                        Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn mở\n" + item.template.name + " ?");
                                    player.sendMessage(msg);
                                } else if (item.template.type == 22) {
                                    if (player.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22).count() > 2) {
                                        Service.gI().sendThongBaoOK(player, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                                        return;
                                    }
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc muốn dùng\n" + item.template.name + " ?");
                                    player.sendMessage(msg);
                                } else {
                                    this.useItem(player, item, index);
                                }
                            }
                        } else {
                            int iditem = _msg.reader().readShort();
                            Item item = finditem(player, iditem);
                            this.useItem(player, item, index);
                        }
                    }
                }
                case DO_THROW_ITEM -> {
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        if (index < 0) {
                            return;
                        }
                        Item item = (where == 0)
                                ? player.inventory.itemsBody.get(index)
                                : player.inventory.itemsBag.get(index);
                        if (item.isNotNullItem() && item.template.id == 570) {
                            Service.gI().sendThongBao(player, "Không thể bỏ vật phẩm này.");
                            return;
                        }
                        if (!item.isNotNullItem()) {
                            return;
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                }
                case ACCEPT_THROW_ITEM -> {
                    InventoryService.gI().throwItem(player, where, index);
                    Service.gI().point(player);
                    InventoryService.gI().sendItemBags(player);
                }
                case ACCEPT_USE_ITEM -> {
                    if (index >= 0 && index < player.inventory.itemsBag.size()) {
                        this.useItem(player, player.inventory.itemsBag.get(index), index);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (pl == null || item == null || !item.isNotNullItem()) {
            return;
        }
        if (item.template.id == 570) {
            if (!Util.isAfterMidnight(pl.lastTimeRewardWoodChest)) {
                Service.gI().sendThongBao(pl, "Hãy chờ đến ngày mai");
            } else {
                GiftBoxHandler.gI().openRuongGo(pl);
            }
            return;
        }
        if (item.template.strRequire > pl.nPoint.power) {
            Service.gI().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
            return;
        }

        switch (item.template.type) {
            case 33 -> SkillAndPetHandler.gI().UseCard(pl, item);
            case 7 -> SkillAndPetHandler.gI().learnSkill(pl, item);
            case 6 -> ConsumableHandler.gI().eatPea(pl);
            case 12 -> TeleportAndRadarHandler.gI().controllerCallRongThan(pl, item);
            case 23, 24, 25 -> InventoryService.gI().itemBagToBody(pl, indexBag);
            case 11 -> {
                InventoryService.gI().itemBagToBody(pl, indexBag);
                Service.gI().sendFlagBag(pl);
            }
            default -> {
                if (MiniPetHandler.tryEquipMiniPet(pl, item.template.id, indexBag)) {
                    break;
                }
                handleItemByTemplateId(pl, item, indexBag);
            }
        }
        TaskService.gI().checkDoneTaskUseItem(pl, item);
        InventoryService.gI().sendItemBags(pl);
    }

    private void handleItemByTemplateId(Player pl, Item item, int indexBag) {
        switch (item.template.id) {
            case 992 -> {
                pl.type = 2;
                pl.maxTime = 5;
                Service.gI().Transport(pl);
            }
            case 361 -> {
                pl.idGo = (short) Util.nextInt(0, 6);
                NgocRongNamecService.gI().menuCheckTeleNamekBall(pl);
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                InventoryService.gI().sendItemBags(pl);
            }
            case 1655 -> GiftBoxHandler.gI().CapsuleKichHoat(pl, item);
            case 1998 -> GiftBoxHandler.gI().openLevel12EquipmentBox(pl);
            case 1787 -> TeleportAndRadarHandler.gI().MapRiengTu(pl, item);
            case 718 -> ConsumableHandler.gI().VeTangNgoc(pl, item);
            case 1575 -> ConsumableHandler.gI().PhaoBong(pl, item);
            case 1576 -> ConsumableHandler.gI().PhaoBongVip(pl, item);
            case 1609 -> ConsumableHandler.gI().KemTraiCay(pl, item);
            case 1608 -> ConsumableHandler.gI().QuaThieuNhi(pl, item);
            case 1822 -> TeleportAndRadarHandler.gI().RadaNgocRong(pl, item.template.id);
            case 1823 -> TeleportAndRadarHandler.gI().RadaNgocRongVip(pl, item.template.id);
            case 1569 -> TeleportAndRadarHandler.gI().KhoBauHaiTac(pl, item.template.id);
            case 1798, 1799, 1800, 1801, 1802 -> ConsumableHandler.gI().ThucAnChoThan(pl, item);
            case 1652 -> {
                try {
                    ConsumableHandler.gI().LoaTheGioi(pl, item);
                } catch (Exception e) {
                    Logger.logException(UseItem.class, e);
                }
            }
            case 1776 -> GiftBoxHandler.gI().OpenHopQuaThuong(pl, item.template.id);
            case 1777 -> GiftBoxHandler.gI().OpenHopQuaVip(pl, item.template.id);
            case 1592 -> GiftBoxHandler.gI().OpenHopQuaGokuDay(pl, item.template.id);
            case 1591, 1594 -> GiftBoxHandler.gI().OpenHopQuaGokuDayVangNgoc(pl, item.template.id);
            case 1840 -> GiftBoxHandler.gI().OpenHopQuaGokuDayVip(pl, item.template.id);
            case 1757 -> GiftBoxHandler.gI().OpenHopQuaCadic(pl, item.template.id);
            case 1821 -> GiftBoxHandler.gI().OpenTrungRongNhi(pl, item.template.id);
            case 1635, 1614, 1615, 1616, 381, 382, 383, 384, 385, 379, 638, 579,
                 1045, 663, 664, 665, 666, 667, 764, 1150, 1151, 1152, 1153, 1154,
                 1233, 1532, 1628 -> ConsumableHandler.gI().useItemTime(pl, item);
            case 880, 881, 882 -> {
                if (pl.itemTime.isEatMeal2) {
                    Service.gI().sendThongBao(pl, "Chỉ được sử dụng 1 cái");
                } else {
                    ConsumableHandler.gI().useItemTime(pl, item);
                }
            }
            case 1505 -> {
                int qty = (item.isNotNullItem() && item.isGiayMau()) ? item.quantity : 0;
                NpcService.gI().createMenuConMeo(pl, ConstNpc.event3, -1,
                        "|1|Gói Hộp đựng quà\n"
                        + "|2|Giấy màu " + qty + "/99\n"
                        + "|2|Giá vàng 2.000.000",
                        "Đồng ý", "Đóng");
            }
            case 1506, 1507, 1508, 1509 -> NpcService.gI().createMenuConMeo(pl, ConstNpc.event3_1, -1,
                    "Bạn muốn gói loại nào",
                    "Gói Hộp quà nhẹ nhàng", "Gói Hộp quà chỉnh chu", "Từ chối");
            case 211, 212 -> ConsumableHandler.gI().eatGrapes(pl, item);
            case 460 -> ConsumableHandler.gI().XuongCho(pl, item);
            case 962 -> GiftBoxHandler.gI().C5(pl, item);
            case 963 -> GiftBoxHandler.gI().C7(pl, item);
            case 342, 343, 344, 345 -> {
                if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22).count() < 3) {
                    Service.gI().dropSatellite(pl, item, pl.zone, pl.location.x, pl.location.y);
                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                } else {
                    Service.gI().sendThongBaoOK(pl, "Mỗi map chỉ đặt được 3 Vệ Tinh");
                }
            }
            case 380 -> GiftBoxHandler.gI().openCSKB(pl, item);
            case 521 -> ConsumableHandler.gI().useTDLT(pl, item);
            case 454 -> SkillAndPetHandler.gI().usePorata(pl);
            case 921 -> SkillAndPetHandler.gI().usePorata2(pl);
            case 1819 -> SkillAndPetHandler.gI().usePorata3(pl);
            case 193 -> {
                TeleportAndRadarHandler.gI().openCapsuleUI(pl);
                InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            }
            case 194 -> TeleportAndRadarHandler.gI().openCapsuleUI(pl);
            case 401 -> SkillAndPetHandler.gI().changePet(pl, item);
            case 1795 -> SkillAndPetHandler.gI().changePetRamdom(pl, item);
            case 402, 403, 404, 759 -> SkillAndPetHandler.gI().upSkillPet(pl, item);
            case 726 -> ConsumableHandler.gI().ItemManhGiay(pl, item);
            case 727, 728 -> ConsumableHandler.gI().ItemSieuThanThuy(pl, item);
            case 1775 -> GiftBoxHandler.gI().OpenHopThanlinh(pl, item.template.id);
            case 648 -> GiftBoxHandler.gI().NoelItemBox(pl, item);
            case 1171 -> GiftBoxHandler.gI().ChuLunBox(pl, item);
            case 1170 -> GiftBoxHandler.gI().BlackGokuItemBoxEventNoel(pl, item);
            case 1560 -> {
                if (InventoryService.gI().findItem(pl.inventory.itemsBag, 1561) != null) {
                    GiftBoxHandler.gI().RuongNgocRong(pl, item);
                } else {
                    Service.gI().sendThongBao(pl, "Bạn không có chìa khoá vàng!");
                }
            }
            case 736 -> ItemService.gI().OpenItem736(pl, item);
            case 987 -> Service.gI().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp");
            case 568 -> {
                if (pl.mabuEgg == null) {
                    MabuEgg.createMabuEgg(pl);
                    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    if (pl.zone.map.mapId == (21 + pl.gender) && pl.mabuEgg != null) {
                        pl.mabuEgg.sendMabuEgg();
                    }
                } else {
                    Service.gI().sendThongBao(pl, "Bạn đã có quả trứng nên không thể sử dụng");
                }
            }
            case 2006 -> Input.gI().createFormChangeNameByItem(pl);
            case 1758 -> unlockPetSkill(pl, item, 1, "chiêu 2");
            case 1759 -> unlockPetSkill(pl, item, 2, "chiêu 3");
            case 1760 -> unlockPetSkill(pl, item, 3, "chiêu 4");
        }
    }

    private void unlockPetSkill(Player pl, Item item, int skillIndex, String label) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
            return;
        }
        if (pl.pet.playerSkill.skills.get(skillIndex).skillId == -1) {
            Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có " + label + " chứ!");
            return;
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        switch (skillIndex) {
            case 1 -> pl.pet.openSkill2();
            case 2 -> pl.pet.openSkill3();
            case 3 -> pl.pet.openSkill4();
        }
    }

    // ===== CÁC PHƯƠNG THỨC ỦY QUYỀN (DELEGATE) GIỮ TƯƠNG THÍCH NGƯỢC 100% =====

    public void openRuongGo(Player player) {
        GiftBoxHandler.gI().openRuongGo(player);
    }

    public int calculateRequiredEmptySlots(int level) {
        return GiftBoxHandler.gI().calculateRequiredEmptySlots(level);
    }

    public void RadaNgocRong(Player player, int itemUseiD) {
        TeleportAndRadarHandler.gI().RadaNgocRong(player, itemUseiD);
    }

    public void RadaNgocRongVip(Player player, int itemUseiD) {
        TeleportAndRadarHandler.gI().RadaNgocRongVip(player, itemUseiD);
    }

    public void KhoBauHaiTac(Player player, int itemUseiD) {
        TeleportAndRadarHandler.gI().KhoBauHaiTac(player, itemUseiD);
    }

    public void choseMapCapsule(Player pl, int index) {
        TeleportAndRadarHandler.gI().choseMapCapsule(pl, index);
    }

    public void eatPea(Player player) {
        ConsumableHandler.gI().eatPea(player);
    }

    public void OpenHopThanlinh(Player player, int itemUseiD) {
        GiftBoxHandler.gI().OpenHopThanlinh(player, itemUseiD);
    }

    public void OpenHopQuaThuong(Player player, int itemUseiD) {
        GiftBoxHandler.gI().OpenHopQuaThuong(player, itemUseiD);
    }

    public void OpenTrungRongNhi(Player player, int itemUseiD) {
        GiftBoxHandler.gI().OpenTrungRongNhi(player, itemUseiD);
    }

    public void OpenHopQuaVip(Player player, int itemUseiD) {
        GiftBoxHandler.gI().OpenHopQuaVip(player, itemUseiD);
    }

    public void OpenHopQuaGokuDay(Player player, int itemUseiD) {
        GiftBoxHandler.gI().OpenHopQuaGokuDay(player, itemUseiD);
    }

    public void OpenHopQuaGokuDayVangNgoc(Player player, int itemUseiD) {
        GiftBoxHandler.gI().OpenHopQuaGokuDayVangNgoc(player, itemUseiD);
    }

    public void OpenHopQuaGokuDayVip(Player player, int itemUseId) {
        GiftBoxHandler.gI().OpenHopQuaGokuDayVip(player, itemUseId);
    }

    public void OpenHopQuaCadic(Player player, int itemUseiD) {
        GiftBoxHandler.gI().OpenHopQuaCadic(player, itemUseiD);
    }

    public void NoelItemBox(Player pl, Item item) {
        GiftBoxHandler.gI().NoelItemBox(pl, item);
    }

    public void HopQuaChinhChu(Player pl, Item item) {
        GiftBoxHandler.gI().HopQuaChinhChu(pl, item);
    }

    public void HopQuaNheNhang(Player pl, Item item) {
        GiftBoxHandler.gI().HopQuaNheNhang(pl, item);
    }

    public void BlackGokuItemBoxEventNoel(Player pl, Item item) {
        GiftBoxHandler.gI().BlackGokuItemBoxEventNoel(pl, item);
    }

    public void C5(Player pl, Item item) {
        GiftBoxHandler.gI().C5(pl, item);
    }

    public void C7(Player pl, Item item) {
        GiftBoxHandler.gI().C7(pl, item);
    }

    public void ChuLunBox(Player pl, Item item) {
        GiftBoxHandler.gI().ChuLunBox(pl, item);
    }

    public void NonNoelDo(Player pl, Item item) {
        GiftBoxHandler.gI().NonNoelDo(pl, item);
    }

    public void receiveLevel12EquipmentSet(Player player, int gender) {
        GiftBoxHandler.gI().receiveLevel12EquipmentSet(player, gender);
    }

    public void LoaTheGioi(Player pl, Item item) {
        ConsumableHandler.gI().LoaTheGioi(pl, item);
    }

    public void TuiVang(Player pl, Item item) {
        GiftBoxHandler.gI().TuiVang(pl, item);
    }

    public void UseCard(Player pl, Item item) {
        SkillAndPetHandler.gI().UseCard(pl, item);
    }
}