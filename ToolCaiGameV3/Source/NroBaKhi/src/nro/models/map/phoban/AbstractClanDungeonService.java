package nro.models.map.phoban;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import nro.models.map.Zone;

/**
 * Lớp cha trừu tượng quản lý Pool phòng và phân bổ phòng cho Phó bản Bang hội
 *
 * @param <T> Loại phó bản kế thừa từ AbstractClanDungeon
 */
public abstract class AbstractClanDungeonService<T extends AbstractClanDungeon> {

    protected final List<T> rooms = new ArrayList<>();

    protected AbstractClanDungeonService(int availableRooms, IntFunction<T> roomFactory) {
        for (int i = 0; i < availableRooms; i++) {
            this.rooms.add(roomFactory.apply(i));
        }
    }

    /**
     * Đăng ký Zone vào phòng phó bản tương ứng
     *
     * @param roomId ID phòng (0 .. AVAILABLE - 1)
     * @param zone Zone bản đồ cần gắn vào phòng
     */
    public void registerZone(int roomId, Zone zone) {
        if (roomId >= 0 && roomId < this.rooms.size()) {
            this.rooms.get(roomId).addZone(zone);
        }
    }

    /**
     * Cấp phát phòng trống an toàn đa luồng (Chống 2 bang bốc trùng 1 phòng)
     *
     * @return Phòng trống hợp lệ hoặc null nếu hết phòng
     */
    protected synchronized T findAvailableRoom() {
        for (T room : this.rooms) {
            if (room != null && !room.isOpened && room.clan == null) {
                return room;
            }
        }
        return null;
    }

    /**
     * Lấy danh sách toàn bộ các phòng
     */
    public List<T> getRooms() {
        return this.rooms;
    }
}
