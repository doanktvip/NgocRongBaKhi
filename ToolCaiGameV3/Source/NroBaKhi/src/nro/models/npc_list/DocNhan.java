package nro.models.npc_list;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.map.service.NpcService;
import nro.models.services.Service;

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
                if (!player.clan.doanhTrai.winDT) {
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
