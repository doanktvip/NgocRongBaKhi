package nro.models.map.phoban;

import nro.models.item.Item;
import java.util.ArrayList;
import java.util.List;
import nro.models.map.Zone;
import nro.models.map.service.ChangeMapService;
import nro.models.player.Player;
import nro.models.services.InventoryService;
import nro.models.services.Service;
import nro.models.utils.Util;

/**
 * Service quản lý hoạt động Bản Đồ Kho Báu (BanDoKhoBau)
 */
public class TreasureUnderSeaService extends AbstractClanDungeonService<BanDoKhoBau> {

    private static final class Holder {
        private static final TreasureUnderSeaService INSTANCE = new TreasureUnderSeaService();
    }

    public static TreasureUnderSeaService gI() {
        return Holder.INSTANCE;
    }

    public List<BanDoKhoBau> banDoKhoBaus = this.rooms;

    private TreasureUnderSeaService() {
        super(BanDoKhoBau.AVAILABLE, BanDoKhoBau::new);
    }

    public void addMapBanDoKhoBau(int id, Zone zone) {
        registerZone(id, zone);
    }

    public synchronized void openBanDoKhoBau(Player player, byte level) {
        if (level >= BanDoKhoBau.MIN_LEVEL && level <= BanDoKhoBau.MAX_LEVEL) {
            if (player.clan != null && player.clan.BanDoKhoBau == null) {
                if (!player.clan.isLeader(player)) {
                    Service.gI().sendThongBao(player, "Chỉ bang chủ mới có quyền mở Bản Đồ Kho Báu");
                    return;
                }
                if (!player.isAdmin() && player.nPoint.power < BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                    Service.gI().sendThongBao(player, "Yêu cầu sức mạnh lớn hơn " + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                    return;
                }
                if (Util.canDoWithTime(player.clan.lastTimeOpenBanDoKhoBau, BanDoKhoBau.TIME_RESET_BDKB)) {
                    player.clan.timesPerDayBDKB = 0;
                }
                if (player.clan.timesPerDayBDKB >= BanDoKhoBau.MAX_TIMES_PER_DAY) {
                    Service.gI().sendThongBao(player, "Bang hội đã mở hang kho báu " + BanDoKhoBau.MAX_TIMES_PER_DAY + " lần trong 24 giờ qua, hẹn gặp lại sau");
                    return;
                }
                Item item = InventoryService.gI().findItemBag(player, 611);
                if (item != null && item.quantity > 0) {
                    BanDoKhoBau banDoKhoBau = findAvailableRoom();
                    if (banDoKhoBau != null) {
                        if (Util.canDoWithTime(player.lastTimeJoinBDKB, BanDoKhoBau.TIME_RESET_BDKB)) {
                            player.timesPerDayBDKB = 0;
                        }
                        if (player.timesPerDayBDKB >= BanDoKhoBau.MAX_TIMES_PER_DAY) {
                            Service.gI().sendThongBao(player, "Bạn đã vào hang kho báu " + BanDoKhoBau.MAX_TIMES_PER_DAY + " lần trong 24 giờ qua, hẹn gặp lại sau");
                            return;
                        }

                        InventoryService.gI().subQuantityItemsBag(player, item, 1);
                        InventoryService.gI().sendItemBags(player);

                        banDoKhoBau.startRoom(player.clan, level);
                        long sessionTime = banDoKhoBau.getLastTimeOpen();

                        if (player.clan.timesPerDayBDKB == 0) {
                            player.clan.lastTimeOpenBanDoKhoBau = sessionTime;
                        }
                        player.clan.timesPerDayBDKB++;
                        player.clan.timeOpenBanDoKhoBau = sessionTime;
                        player.clan.updateBDKB();

                        if (player.timesPerDayBDKB == 0) {
                            player.lastTimeJoinBDKB = sessionTime;
                        }
                        player.timesPerDayBDKB++;
                        player.lastTimeOpenBDKBSession = sessionTime;

                        ChangeMapService.gI().goToDBKB(player);
                    } else {
                        Service.gI().sendThongBao(player, "Hang kho báu đã đầy, hãy quay lại sau 30 phút");
                    }
                } else {
                    Service.gI().sendThongBao(player, "Không tìm thấy bản đồ kho báu");
                }
            } else {
                Service.gI().sendThongBao(player, "Không thể thực hiện");
            }
        }
    }

    public void updateThanhTich(Player player, BanDoKhoBau banDoKhoBau) {
        if (player == null || player.clan == null || banDoKhoBau == null || player.clan.BanDoKhoBau != banDoKhoBau) {
            return;
        }

        long timeDone = System.currentTimeMillis() - banDoKhoBau.getLastTimeOpen();
        int levelDone = banDoKhoBau.level;
        if (levelDone > player.clan.levelDoneBanDoKhoBau) {
            player.clan.levelDoneBanDoKhoBau = levelDone;
            player.clan.thoiGianHoanThanhBDKB = timeDone;
        } else if (levelDone == player.clan.levelDoneBanDoKhoBau
                && timeDone < player.clan.thoiGianHoanThanhBDKB) {
            player.clan.thoiGianHoanThanhBDKB = timeDone;
        }

        player.clan.updatethanhTichBDKB(player.clan.id);
        player.clan.updatethanhTichBDKBForLeader();
        player.clan.updateThongTinLeader(player.clan.id);
    }
}
