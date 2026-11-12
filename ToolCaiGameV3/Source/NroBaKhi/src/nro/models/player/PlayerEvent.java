package nro.models.player;

import lombok.Getter;
import lombok.Setter;
import nro.models.player.Player;
import nro.models.services.Service;

/**
 * @author By Mr Blue
 */
@Setter
@Getter
public class PlayerEvent {
    private int eventPoint;
    private Player player;
    public int luotNhanNgocMienPhi = 1;
    public int luotNhanCapsuleBang = 1;

    public PlayerEvent(Player player) {
        this.player = player;
    }
    
    /**
     * Tự động cộng điểm sự kiện và gửi thông báo "+[num] Point" tới người chơi.
     *
     * @param num Số điểm cộng vào (ví dụ: 5 -> tự động gửi thông báo "+5 Point")
     */
    public void addEventPoint(int num) {
        addEventPoint(num, true);
    }

    /**
     * Cộng điểm sự kiện với tùy chọn bật/tắt gửi thông báo.
     *
     * @param num Số điểm cộng vào
     * @param sendNotify true: tự động gửi thông báo "+[num] Point", false: cộng ngầm
     */
    public void addEventPoint(int num, boolean sendNotify) {
        this.eventPoint += num;
        if (sendNotify && this.player != null) {
            Service.gI().sendThongBao(this.player, "+" + num + " Điểm săn Boss");
        }
    }
    
    public void subEventPoint(int num) {
        eventPoint -= num;
    }

    public void update() {
       
    }

}
