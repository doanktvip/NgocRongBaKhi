package nro.models.npc_list;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.map.service.NpcService;
import nro.models.services.Service;
import nro.models.map.phoban.RedRibbonHQ;
import nro.models.map.Zone;
import nro.models.mob.Mob;

/**
 *
 * @author By Mr Blue
 * 
 */

public class DocNhan extends Npc {

    public DocNhan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (mapId == 57) {
                if (player.clan != null && player.clan.doanhTrai != null) {
                    RedRibbonHQ dt = player.clan.doanhTrai;
                    if (!dt.winDT) {
                        boolean zone57Clean = true;
                        Zone z57 = dt.getMapById(57);
                        if (z57 != null) {
                            if (z57.mobs != null) {
                                for (Mob m : z57.mobs) {
                                    if (m != null && !m.isDie()) {
                                        zone57Clean = false;
                                        break;
                                    }
                                }
                            }
                            if (zone57Clean && z57.getBosses() != null) {
                                for (Player b : z57.getBosses()) {
                                    if (b != null && b.zone != null && b.nPoint != null && !b.isDie()) {
                                        zone57Clean = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (dt.areAllCharactersDead() || (zone57Clean && dt.areAllBossesDead())) {
                            dt.winDT = true;
                        }
                    }
                }
                if (player.clan == null || player.clan.doanhTrai == null || !player.clan.doanhTrai.winDT) {
                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Bọn mi đừng hòng thoát khỏi nơi đây");
                } else {
                    NpcService.gI().createTutorial(player, tempId, this.avartar, "Ta chịu thua! Toàn bộ hồ sơ mật và mảnh sưu tầm của quân đoàn Red Ribbon\nta đã phân tán khắp các ngóc ngách Doanh Trại...\nCác ngươi chỉ có 5 phút đi tìm, đố các ngươi thu thập hết hahaha!");
                    if (!player.clan.doanhTrai.isTimePicking) {
                        Service.gI().sendThongBao(player, "Trại Độc Nhãn đã bị tiêu diệt, bạn có 5 phút để thu thập các Mảnh Sưu Tầm trước khi phi thuyền đến đón!");
                        player.clan.doanhTrai.isTimePicking = true;
                        player.clan.doanhTrai.lastTimePick = System.currentTimeMillis();
                        player.clan.doanhTrai.randomCardFragments();
                        player.clan.doanhTrai.sendTextTimePickDoanhTrai();
                    }
                }

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
    }
}
