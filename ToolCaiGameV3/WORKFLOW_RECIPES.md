# WORKFLOW_RECIPES.md — Công Thức Mở Rộng Tính Năng Chuẩn (NgocRongBaKhi Playbook)

Tài liệu này là **Cẩm nang triển khai chuẩn (Step-by-Step Playbook)** cho 5 tác vụ phát triển game thường gặp nhất trong dự án **NgocRongBaKhi**.
Mục tiêu cốt lõi: **Xóa bỏ hoàn toàn việc tìm kiếm mò mẫm trong 543 tệp code**. Khi cần thêm Boss, NPC, vật phẩm hoặc lệnh Admin, Lập trình viên và AI chỉ cần làm theo đúng 3–4 bước chuẩn dưới đây để đảm bảo:
1. Không làm phình to các "God Classes".
2. Tuân thủ kiến trúc **Facade + Handlers** và **Boy Scout Rule**.
3. Biên dịch sạch sẽ 100%, không xung đột và không gãy tương thích ngược.

---

## 1. Recipe 1: Cách Thêm 1 Con Boss Mới (Spawn & Drop Tự Động)

Để thêm một con Boss mới xuất hiện trên bản đồ, có lời thoại, chiêu thức và tự động rơi vật phẩm khi bị tiêu diệt, thực hiện đúng **4 bước**:

### Bước 1: Khai báo ID Boss tại [`nro/models/boss/BossID.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/BossID.java)
Mở tệp và thêm hằng số ID âm duy nhất (tránh trùng với các ID hiện có):
```java
// Thêm vào cuối file hoặc nhóm phù hợp
public static final int MY_NEW_BOSS = -950;
```

### Bước 2: Khai báo Chỉ số & Ngoại hình tại [`nro/models/boss/BossesData.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/BossesData.java)
Định nghĩa mẫu dữ liệu `BossData` cho Boss:
```java
public static final BossData MY_NEW_BOSS_DATA = new BossData(
        "Tên Boss Của Bạn", // Tên hiển thị trên đầu Boss
        ConstPlayer.XAYDA,   // Hành tinh (TRAI_DAT, NAMEC, XAYDA)
        new short[]{1200, 1201, 1202, -1, -1, -1}, // Outfit: {Head, Body, Leg, Bag, Aura, Set}
        100_000_000,         // Sát thương cơ bản (Dame)
        new int[]{5_000_000_000L}, // Máu tối đa (HP) theo từng giai đoạn
        new int[]{105, 106, 107},  // Danh sách Map xuất hiện (ví dụ: Núi tuyết, Rừng tuyết)
        new int[][]{               // Kỹ năng: {SkillId, Cấp độ, Thời gian hồi ms}
            {Skill.DRAGON, 7, 1000},
            {Skill.KAMEJOKO, 7, 2000},
            {Skill.THAI_DUONG_HA_SAN, 1, 30000}
        },
        new String[]{"|-1|Các ngươi muốn nếm mùi sức mạnh không?", "|-1|Ta sẽ nghiền nát tất cả!"}, // Text khi xuất hiện
        new String[]{"|-1|Đòn đánh như gãi ngứa vậy!"}, // Text khi chiến đấu
        new String[]{"|-1|Không thể nào... ta sẽ quay lại!"}, // Text khi bị hạ gục
        60 // Thời gian hồi sinh sau khi chết (phút)
);
```

### Bước 3: Tạo lớp Boss cụ thể kế thừa `Boss`
Tạo tệp mới tại `src/nro/models/boss/.../MyNewBoss.java`:
```java
package nro.models.boss.custom;

import nro.models.boss.Boss;
import nro.models.boss.BossID;
import nro.models.boss.BossesData;
import nro.models.player.Player;

public class MyNewBoss extends Boss {

    public MyNewBoss() throws Exception {
        super(BossID.MY_NEW_BOSS, true, true, BossesData.MY_NEW_BOSS_DATA);
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) return;
        super.reward(plKill);
        
        // Cơ chế rơi đồ Fluent API tiện lợi:
        // Rơi ngẫu nhiên vật phẩm 14, 15, 16 với tỉ lệ 80%
        this.drop(plKill).random(14, 15, 16).rate(80).execute();
        
        // Rơi chắc chắn Thỏi Vàng (457) với số lượng 5 thỏi
        this.drop(plKill).item(457).quantity(5).rate(100).execute();
        
        // Cộng điểm sự kiện săn boss
        this.rewardEventPoint(plKill, 2);
    }
}
```

### Bước 4: Đăng ký Boss vào [`BossManager.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Boss_Manager/BossManager.java)
Mở [`BossManager.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Boss_Manager/BossManager.java):
1. Trong phương thức `createBoss(int id)`:
```java
case BossID.MY_NEW_BOSS -> new MyNewBoss();
```
2. Trong phương thức `loadBoss()` (nếu muốn Boss tự động sinh khi server bật):
```java
this.createBoss(BossID.MY_NEW_BOSS, 1); // Sinh 1 con
```

---

## 2. Recipe 2: Cách Thêm 1 NPC Hoặc Menu NPC Mới

Để tạo một NPC tương tác mới hoặc thêm các nhánh tùy chọn Menu vào NPC:

### Bước 1: Khai báo NPC ID tại [`nro/models/consts/ConstNpc.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/consts/ConstNpc.java)
```java
public static final byte MY_NEW_NPC = 99; // ID chưa bị chiếm
```

### Bước 2: Tạo lớp NPC con kế thừa [`Npc`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc/Npc.java)
Tạo tệp tại `src/nro/models/npc_list/MyNewNpc.java`:
```java
package nro.models.npc_list;

import nro.models.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.services.Service;

public class MyNewNpc extends Npc {

    public MyNewNpc(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Xin chào người anh hùng, ta có thể giúp gì cho ngươi?",
                    "Nhận Quà", "Đổi Thưởng", "Từ Chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.BASE_MENU -> {
                    switch (select) {
                        case 0 -> Service.gI().sendThongBao(player, "Ngươi đã nhận quà tân thủ!");
                        case 1 -> Service.gI().sendThongBao(player, "Tính năng đổi thưởng đang mở!");
                        case 2 -> {} // Đóng menu
                    }
                }
            }
        }
    }
}
```

### Bước 3: Đăng ký NPC vào [`NpcFactory.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc/NpcFactory.java)
Mở [`NpcFactory.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc/NpcFactory.java), thêm vào lệnh `switch (tempId)` trong hàm `createNPC`:
```java
case ConstNpc.MY_NEW_NPC -> new MyNewNpc(mapId, status, cx, cy, tempId, avatar);
```

### Bước 4: Đặt NPC lên Bản Đồ
Khai báo tọa độ và map cho NPC trong bảng MySQL `map` (cột `npcs`) hoặc gọi hàm nạp NPC khi khởi tạo bản đồ.

---

## 3. Recipe 3: Cách Xử Lý 1 Vật Phẩm Mới Khi Sử Dụng (`UseItem`)

Toàn bộ hệ thống sử dụng vật phẩm đã được tái cấu trúc theo mô hình **Facade + Handlers** gọn gàng. Tuyệt đối **không** viết code dài vào [`UseItem.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/UseItem.java). Thực hiện đúng **3 bước**:

### Bước 1: Chọn đúng Handler chuyên biệt trong `nro.models.services_func.useitem`
1. [`ConsumableHandler.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/useitem/ConsumableHandler.java): Thức ăn, nước uống, bùa thời gian, pháo bông, loa thế giới, thuốc tăng lực.
2. [`GiftBoxHandler.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/useitem/GiftBoxHandler.java): Mở rương gỗ, hộp quà sự kiện, bao lì xì, capsule kích hoạt.
3. [`MiniPetHandler.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/useitem/MiniPetHandler.java): Thú cưng đeo lưng (chỉ cần thêm ID vào bảng tra cứu).
4. [`SkillAndPetHandler.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/useitem/SkillAndPetHandler.java): Thẻ card sưu tầm, sách chiêu thức, hợp thể Porata, trứng đệ tử.
5. [`TeleportAndRadarHandler.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/useitem/TeleportAndRadarHandler.java): Capsule di chuyển, rada ngọc rồng, vé vào map riêng.

### Bước 2: Viết logic nghiệp vụ tại Handler đã chọn
*Ví dụ: Viết vào [`GiftBoxHandler.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/useitem/GiftBoxHandler.java)*:
```java
public void openMyNewBox(Player pl, Item item) {
    // 1. Kiểm tra ô trống hành trang
    if (InventoryService.gI().getCountEmptyBag(pl) < 2) {
        Service.gI().sendThongBao(pl, "Hành trang cần tối thiểu 2 ô trống!");
        return;
    }
    // 2. Trừ vật phẩm tiêu hao
    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    
    // 3. Trao thưởng ngẫu nhiên
    Item thoiVang = ItemService.gI().createNewItem((short) 457, 1); // 1 Thỏi vàng
    InventoryService.gI().addItemBag(pl, thoiVang, 0);
    
    Service.gI().sendThongBao(pl, "Chúc mừng bạn nhận được 1 Thỏi Vàng!");
    InventoryService.gI().sendItemBags(pl);
}
```

### Bước 3: Móc nối 1 dòng điều phối trong [`UseItem.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/UseItem.java)
Mở [`UseItem.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/UseItem.java), tìm hàm `handleItemByTemplateId(...)` và thêm 1 dòng:
```java
case 2050 -> GiftBoxHandler.gI().openMyNewBox(pl, item);
```

> [!TIP]
> **Trường hợp thêm Thú cưng đeo lưng (MiniPet):**
> Bạn thậm chí **không cần sửa `UseItem.java`**! Chỉ cần mở [`MiniPetHandler.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/useitem/MiniPetHandler.java), thêm 1 dòng vào khối `static`:
> `map.put(ITEM_ID_MOI, new int[]{head, body, leg});`
> Bộ điều phối `tryEquipMiniPet` sẽ tự động nhận diện và trang bị tức thì!

---

## 4. Recipe 4: Cách Thêm 1 Lệnh Chat Admin (`Command.java`)

Toàn bộ lệnh chat của Admin đã được phân loại rõ ràng thành 2 bảng tra cứu tại [`nro/models/server/Command.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/server/Command.java), tự động ghi nhận **Audit Log màu tím** chống gian lận.

### Bước 1: Mở tệp [`nro/models/server/Command.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/server/Command.java)

### Bước 2: Chọn đúng kiểu lệnh để đăng ký
#### Kiểu A: Lệnh không tham số (Đăng ký trong `initAdminCommands()`)
Thích hợp cho các lệnh tắt bật nhanh hoặc mở bảng điều khiển:
```java
// Người chơi chat: "battu"
adminCommands.put("battu", player -> {
    player.nPoint.hp = player.nPoint.hpMax;
    Service.gI().sendThongBao(player, "Đã hồi đầy HP và kích hoạt trạng thái bất tử!");
});
```

#### Kiểu B: Lệnh có tham số (Đăng ký trong `initParameterizedCommands()`)
Thích hợp cho các lệnh nhận số lượng, ID map, số tiền:
```java
// Người chơi chat: "setvang 1000000"
parameterizedCommands.put("setvang", (player, text) -> {
    try {
        long amount = Long.parseLong(text.replace("setvang", "").trim());
        player.inventory.gold = amount;
        Service.gI().sendMoney(player);
        Service.gI().sendThongBao(player, "Đã chỉnh vàng thành: " + Util.formatNumber(amount));
    } catch (Exception e) {
        Service.gI().sendThongBao(player, "Cú pháp: setvang <số_lượng>");
    }
});
```

### Bước 3: Đăng nhập Game thử nghiệm
Chat bằng tài khoản có quyền `player.isAdmin() == true`. Lệnh sẽ thực thi ngay lập tức và in log:
`[AUDIT] Admin <Name> (ID: <ID>) executed: <Command>` trên cửa sổ Console của Server.

---

## 5. Recipe 5: Cách Đồng Bộ Tính Năng Từ Web Admin Sang Server Java (Zero-Downtime Hot Reload)

Kiến trúc đồng bộ giữa Web PHP (`WebTeamobi`) và Game Server Java (`NroBaKhi`) hoạt động theo nguyên lý **Bảng Tín Hiệu (Signal Polling Table)** qua MySQL — **Không cần kết nối Socket phức tạp, không cần khởi động lại Server**.

### Bước 1: Tạo bảng tín hiệu trong MySQL `nrobakhi`
```sql
CREATE TABLE IF NOT EXISTS `system_reload_request` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `type` VARCHAR(50) NOT NULL COMMENT 'giftcode, shop, event, notification',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `loaded_at` DATETIME NULL,
    `record_count` INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### Bước 2: Ghi tín hiệu từ Web Admin PHP ([`Source/WebTeamobi`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/WebTeamobi))
Trong tệp PHP xử lý Admin sau khi cập nhật dữ liệu vào MySQL:
```php
<?php
// Sau khi admin bấm "Lưu thay đổi Cửa hàng / Giftcode"
$db->query("INSERT INTO system_reload_request (`type`) VALUES ('shop')");
?>
```

### Bước 3: Tạo phương thức Quét (Poll) trên Server Java
Viết hàm trong DAO hoặc Manager tương ứng (mẫu chuẩn như [`GiftCodeManager.java`](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/managers/GiftCodeManager.java)):
```java
public class MyFeatureManager {
    private long lastReloadId = 0;

    public void pollReloadRequests() {
        try (Connection con = LocalManager.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT id FROM system_reload_request WHERE id > ? AND type = 'my_type' ORDER BY id DESC LIMIT 1")) {
            ps.setLong(1, lastReloadId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                long reqId = rs.getLong("id");
                
                // 1. Tải lại dữ liệu mới từ MySQL vào RAM
                int count = reloadDataFromDb();
                
                // 2. Phản hồi xác nhận đã cập nhật xong
                try (PreparedStatement ack = con.prepareStatement(
                    "UPDATE system_reload_request SET loaded_at = NOW(), record_count = ? WHERE id = ?")) {
                    ack.setInt(1, count);
                    ack.setLong(2, reqId);
                    ack.executeUpdate();
                }
                lastReloadId = reqId;
                Logger.log(Logger.GREEN, "[HOT-RELOAD] Đã cập nhật MyFeature thành công: " + count + " bản ghi.\n");
            }
        } catch (Exception e) {
            Logger.logException(MyFeatureManager.class, e);
        }
    }
}
```

### Bước 4: Móc hàm quét vào vòng lặp nhịp của Server
Trong vòng lặp cập nhật định kỳ (`Manager.update()` hoặc luồng chạy mỗi giây):
```java
MyFeatureManager.gI().pollReloadRequests();
```
*Kết quả:* Khi Admin đổi dữ liệu trên Web, Server Java sẽ tự động làm mới bộ nhớ đệm RAM trong vòng **1 giây** mà **không làm mất kết nối của bất kỳ người chơi nào**.

