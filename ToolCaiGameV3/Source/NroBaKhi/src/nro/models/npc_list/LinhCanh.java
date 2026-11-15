package nro.models.npc_list;

import nro.models.consts.ConstNpc;
import nro.models.map.phoban.RedRibbonHQ;
import nro.models.map.phoban.RedRibbonHQService;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.map.service.NpcService;
import nro.models.services.Service;
import nro.models.utils.TimeUtil;
import nro.models.utils.Util;

public class LinhCanh extends Npc {

    public LinhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) {
            return;
        }
        if (player.clan == null) {
            NpcService.gI().createTutorial(player, tempId, this.avartar,
                    "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai");
            return;
        }
        if (player.getSession() == null || !player.getSession().actived) {
            Service.gI().sendThongBao(player, "Vui lòng mở thành viên trước");
            return;
        }
        if (player.clan.getMembers().size() < RedRibbonHQ.N_PLAYER_CLAN) {
            NpcService.gI().createTutorial(player, tempId, this.avartar,
                    "Bang hội phải có ít nhất " + RedRibbonHQ.N_PLAYER_CLAN + " thành viên mới có thể tham gia");
            return;
        }
        if (player.clanMember == null || player.clanMember.getNumDateFromJoinTimeToToday() < RedRibbonHQ.N_DAY_JOIN_CLAN) {
            NpcService.gI().createTutorial(player, tempId, this.avartar,
                    "Gia nhập bang hội trên " + RedRibbonHQ.N_DAY_JOIN_CLAN + " ngày mới được tham gia");
            return;
        }
        // 1. Nếu Doanh Trại đang mở dở dang -> Cho phép tham gia tiếp viện ngay (đồng bộ chính xác thời gian theo từng giai đoạn)
        if (player.clan.doanhTrai != null) {
            String timeLeft;
            String statusText;
            if (player.clan.doanhTrai.isTimePicking) {
                timeLeft = TimeUtil.getTimeLeft(player.clan.doanhTrai.lastTimePick, RedRibbonHQ.TIME_PICK_DOANH_TRAI / 1000);
                statusText = "Bang hội của ngươi đã tiêu diệt trại độc nhãn và đang nhặt ngọc\nThời gian còn lại là "
                        + timeLeft + ". Ngươi có muốn tham gia không?";
            } else {
                timeLeft = TimeUtil.getTimeLeft(player.clan.doanhTrai.getLastTimeOpen(), RedRibbonHQ.TIME_DOANH_TRAI / 1000);
                statusText = "Bang hội của ngươi đang đánh trại độc nhãn\nThời gian còn lại là "
                        + timeLeft + ". Ngươi có muốn tham gia không?";
            }
            createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI, statusText,
                    "Tham gia", "Không", "Hướng\ndẫn\nthêm");
            return;
        }
        // 2. Kiểm tra giới hạn lượt đi trong ngày TRƯỚC khi bắt gọi đồng đội
        if (player.clan.haveGoneDoanhTrai && !Util.isAfterMidnight(player.clan.lastTimeOpenDoanhTrai)) {
            if (!Util.isAfterMidnight(player.lastTimeJoinDT)) {
                NpcService.gI().createTutorial(player, tempId, this.avartar,
                        "Hôm nay bạn đã tham gia doanh trại rồi, hẹn gặp bạn vào ngày mai");
                return;
            }
            String openerName = (player.clan.playerOpenDoanhTrai != null) ? player.clan.playerOpenDoanhTrai.name : "đồng đội";
            createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Bang hội của ngươi ngày hôm nay đã vào 1 lần rồi (thành viên " + openerName + ") lúc "
                    + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm") + "\n"
                    + "Nên ngươi không thể vào được nữa.\n"
                    + "Hãy chờ đến ngày mai để có thể vào miễn phí", "OK", "Hướng\ndẫn\nthêm");
            return;
        }
        // 3. Kiểm tra số đồng đội đứng gần (nếu N_PLAYER_MAP > 0)
        if (RedRibbonHQ.N_PLAYER_MAP > 0) {
            int nPlSameClan = 0;
            if (player.zone != null) {
                for (Player pl : player.zone.getPlayers()) {
                    if (pl != null && !pl.equals(player) && pl.clan != null
                            && pl.clan.equals(player.clan) && pl.location != null
                            && pl.location.x >= RedRibbonHQ.MIN_X_JOIN && pl.location.x <= RedRibbonHQ.MAX_X_JOIN) {
                        nPlSameClan++;
                        if (nPlSameClan >= RedRibbonHQ.N_PLAYER_MAP) {
                            break;
                        }
                    }
                }
            }
            if (nPlSameClan < RedRibbonHQ.N_PLAYER_MAP) {
                createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Ngươi phải có ít nhất " + RedRibbonHQ.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể vào\n"
                        + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết. "
                        + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                return;
            }
        }
        // 4. Đủ mọi điều kiện -> Mở menu vào Doanh trại
        createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\nkhông?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng.",
                "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) {
            return;
        }
        switch (player.idMark.getIndexMenu()) {
            case ConstNpc.MENU_JOIN_DOANH_TRAI -> {
                if (select == 0) {
                    RedRibbonHQService.gI().joinDoanhTrai(player);
                } else if (select == 2) {
                    NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                }
            }
            case ConstNpc.IGNORE_MENU -> {
                if (select == 1) {
                    NpcService.gI().createTutorial(player, tempId, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                }
            }
        }
    }
}
