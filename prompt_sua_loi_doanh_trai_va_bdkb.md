# HƯỚNG DẪN AI AGENT: BẢN VÁ TOÀN DIỆN PHÓ BẢN DOANH TRẠI ĐỘC NHÃN & BẢN ĐỒ KHO BÁU

> **Mục tiêu**: Khắc phục triệt để 6 lỗi tiềm ẩn liên-module (Cross-File Bugs) giữa `phoban/`, `boss/doanh_trai/`, `map/service/`, `services/` và `ItemMap.java` trong dự án `NroBaKhi`.
>
> **Quy tắc bắt buộc cho AI Agent (Anti-Failure Rules)**:
> 1. **Không cắt bớt code (No Code Truncation)**: Khi sửa các phương thức bên dưới, giữ nguyên đầy đủ toàn bộ logic xung quanh, tuyệt đối không dùng comment dạng `// ... existing code ...` làm mất dòng lệnh.
> 2. **Chuẩn mã hóa UTF-8**: Giữ nguyên đầy đủ dấu tiếng Việt có dấu chuẩn UTF-8.
> 3. **Không dùng khối `catch` rỗng**: Mọi ngoại lệ phải được ghi log qua `Logger.logException(...)`.
> 4. **Bảo toàn cấu trúc Packet Client**: Không thay đổi ID gói tin hay thứ tự byte gửi về Client.

---

## TỔNG HỢP DANH SÁCH 6 TỆP CẦN SỬA

| STT | Đường dẫn tệp (File Path) | Lỗi cần khắc phục | Mức độ |
| :--- | :--- | :--- | :--- |
| **1** | `src/nro/models/map/phoban/TreasureUnderSeaService.java` | Lỗi trừ kép 2 lượt đi BĐKB/lần mở đối với người mở cổng do lệch mili-giây giữa `now` và `banDoKhoBau.getLastTimeOpen()` sau khi gọi SQL `updateBDKB()`. | Nghiêm trọng (Critical) |
| **2** | `src/nro/models/map/ItemMap.java` | Mảnh thẻ sưu tầm (`837..842, 859, 956`) của Độc Nhãn rải ra trong 5 phút nhưng bị `ItemMap.update()` tự động xóa mất sau 50 giây. | Nghiêm trọng (Critical) |
| **3** | `src/nro/models/map/phoban/AbstractClanDungeon.java` | Nguy cơ văng `ConcurrentModificationException` khi `dispose()` duyệt `for (Boss boss : this.bosses)` gọi `boss.leaveMap()`, do `NinjaClone.leaveMap()` gọi `bosses.remove(this)` ngay trong lúc duyệt. | Nghiêm trọng (Critical) |
| **4** | `src/nro/models/boss/doanh_trai/NinjaAoTim.java` | Phân thân `NinjaClone` khi vừa triệu hồi trong `callNinjaClones()` đang nhận 100% HP và 100% Dame của Boss thật thay vì 10% (`1/10`) như thiết kế. | Cao (High) |
| **5** | `src/nro/models/map/phoban/RedRibbonHQService.java` | Thành viên vào tiếp viện sau (`pl.clan.doanhTrai != null`) không kích hoạt `updateHPDame()` (tạo lỗ hổng lấy acc sơ sinh mở map rồi đưa acc chính vào sau) và không nhận được đồng hồ `ItemTime`. | Nghiêm trọng (Critical) |
| **6** | `src/nro/models/map/phoban/RedRibbonHQ.java` & `src/nro/models/services/ItemTimeService.java` | Quên reset 3 cờ `isbulon1Alive`, `isbulon2Alive`, `isTUTAlive` tại Map 59 khi mở lại phòng Doanh Trại mới; đồng thời đồng hồ BĐKB trả về rỗng nếu người chơi vào đúng 60 giây hang sắp sập. | Trung bình - Cao (Medium-High) |

---

## CHI TIẾT TỪNG BƯỚC SỬA CODE CHO AI AGENT

### BƯỚC 1: Sửa lỗi trừ 2 lượt đi BĐKB trong 1 lần mở
* **File**: `src/nro/models/map/phoban/TreasureUnderSeaService.java`
* **Phương thức**: `public void openBanDoKhoBau(Player player, byte level)` (khoảng dòng 55–85)
* **Nguyên nhân**: Giữa dòng `long now = System.currentTimeMillis();` (dòng 66) và `banDoKhoBau.startRoom(player.clan, level);` (dòng 82) có lệnh `player.clan.updateBDKB();` thực thi SQL tốn vài chục mili-giây. Do đó `banDoKhoBau.getLastTimeOpen()` được gán mốc thời gian mới lớn hơn `now`. Khi bước vào `ChangeMapService.gI().goToDBKB(player)` (dòng 83), điều kiện `if (player.lastTimeOpenBDKBSession != sessionTime)` luôn đúng và cộng `player.timesPerDayBDKB++` lần thứ 2.
* **Thao tác sửa**: Khởi động phòng `banDoKhoBau.startRoom(player.clan, level)` trước, lấy mốc thời gian chuẩn `long sessionTime = banDoKhoBau.getLastTimeOpen();` để gán đồng bộ cho cả `player.clan` và `player.lastTimeOpenBDKBSession`.

#### Code TRƯỚC khi sửa (dòng 56–84):
```java
                    BanDoKhoBau banDoKhoBau = findAvailableRoom();
                    if (banDoKhoBau != null) {
                        if (Util.canDoWithTime(player.lastTimeJoinBDKB, BanDoKhoBau.TIME_RESET_BDKB)) {
                            player.timesPerDayBDKB = 0;
                        }
                        if (player.timesPerDayBDKB >= BanDoKhoBau.MAX_TIMES_PER_DAY) {
                            Service.gI().sendThongBao(player, "Bạn đã vào hang kho báu " + BanDoKhoBau.MAX_TIMES_PER_DAY + " lần trong 24 giờ qua, hẹn gặp lại sau");
                            return;
                        }

                        long now = System.currentTimeMillis();
                        if (player.clan.timesPerDayBDKB == 0) {
                            player.clan.lastTimeOpenBanDoKhoBau = now;
                        }
                        player.clan.timesPerDayBDKB++;
                        player.clan.timeOpenBanDoKhoBau = now;
                        player.clan.updateBDKB();

                        if (player.timesPerDayBDKB == 0) {
                            player.lastTimeJoinBDKB = now;
                        }
                        player.timesPerDayBDKB++;
                        player.lastTimeOpenBDKBSession = now;

                        InventoryService.gI().subQuantityItemsBag(player, item, 1);
                        InventoryService.gI().sendItemBags(player);
                        banDoKhoBau.startRoom(player.clan, level);
                        ChangeMapService.gI().goToDBKB(player);
```

#### Code SAU khi sửa:
```java
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
```

---

### BƯỚC 2: Bảo vệ Mảnh thẻ sưu tầm của Độc Nhãn không bị tự xóa sau 50 giây
* **File**: `src/nro/models/map/ItemMap.java`
* **Phương thức**: `public void update()` (khoảng dòng 110–116)
* **Nguyên nhân**: Hiện tại `ItemMap.update()` chỉ miễn trừ xóa sau 50 giây cho Ngọc Rồng (`id >= 14 && id <= 20`) trong Doanh Trại, khiến các Mảnh sưu tầm (`837..842, 859, 956`) do NPC Độc Nhãn rải ra khắp 10 map (thời hạn nhặt 5 phút) bị thu hồi mất chỉ sau 50 giây.
* **Thao tác sửa**: Tách hàm kiểm tra vật phẩm đặc biệt của Doanh Trại (gồm Ngọc Rồng `14..20` và Mảnh sưu tầm `837..842, 859, 956`) để giữ trên sàn cho tới khi người chơi nhặt hoặc tới khi `RedRibbonHQ.dispose()` gọi `clearAllItems()`.

#### Code TRƯỚC khi sửa (dòng 110–116):
```java
            if ((Util.canDoWithTime(createTime, 50000) && isNotNullItem() && itemTemplate.type != 22 || Util.canDoWithTime(createTime, 1800000)) && !this.isNamecBall) {
                if (this.zone != null && this.zone.map.mapId != 21 && this.zone.map.mapId != 22
                        && this.zone.map.mapId != 23 && this.itemTemplate.id != 78
                        && this.itemTemplate.id != 726 && !(MapService.gI().isMapDoanhTrai(this.zone.map.mapId) && this.itemTemplate.id >= 14 && this.itemTemplate.id <= 20)) {
                    ItemMapService.gI().removeItemMapAndSendClient(this);
                }
            }
```

#### Code SAU khi sửa:
```java
            if ((Util.canDoWithTime(createTime, 50000) && isNotNullItem() && itemTemplate.type != 22 || Util.canDoWithTime(createTime, 1800000)) && !this.isNamecBall) {
                boolean isPersistentDoanhTraiItem = this.zone != null
                        && MapService.gI().isMapDoanhTrai(this.zone.map.mapId)
                        && ((this.itemTemplate.id >= 14 && this.itemTemplate.id <= 20)
                        || (this.itemTemplate.id >= 837 && this.itemTemplate.id <= 842)
                        || this.itemTemplate.id == 859
                        || this.itemTemplate.id == 956);
                if (this.zone != null && this.zone.map.mapId != 21 && this.zone.map.mapId != 22
                        && this.zone.map.mapId != 23 && this.itemTemplate.id != 78
                        && this.itemTemplate.id != 726 && !isPersistentDoanhTraiItem) {
                    ItemMapService.gI().removeItemMapAndSendClient(this);
                }
            }
```

---

### BƯỚC 3: Chống `ConcurrentModificationException` khi giải phóng Boss trong `AbstractClanDungeon.dispose()`
* **File**: `src/nro/models/map/phoban/AbstractClanDungeon.java`
* **Phương thức**: `public void dispose()` (khoảng dòng 159–171)
* **Nguyên nhân**: Trong `NinjaClone.leaveMap()`, phân thân gọi `this.boss.clan.doanhTrai.bosses.remove(this);`. Nếu `AbstractClanDungeon.dispose()` duyệt trực tiếp `for (Boss boss : this.bosses)` và gọi `boss.leaveMap()`, việc xóa phần tử khỏi `this.bosses` ngay trong lúc đang duyệt bằng `Iterator` của `ArrayList` sẽ ném `ConcurrentModificationException`, làm ngắt luồng dọn dẹp các Boss còn lại và bỏ qua `clearAllItems()`.
* **Thao tác sửa**: Duyệt qua bản sao `new ArrayList<>(this.bosses)` khi gọi `boss.leaveMap()`.

#### Code TRƯỚC khi sửa (dòng 159–171):
```java
    public void dispose() {
        this.isOpened = false;
        try {
            this.removeTextTime();
            for (Boss boss : this.bosses) {
                if (boss != null && boss.zone != null) {
                    boss.leaveMap();
                }
            }
            this.bosses.clear();
            this.clearAllItems();
            this.onResetExtraState();
        } catch (Exception e) {
```

#### Code SAU khi sửa:
```java
    public void dispose() {
        this.isOpened = false;
        try {
            this.removeTextTime();
            for (Boss boss : new ArrayList<>(this.bosses)) {
                if (boss != null && boss.zone != null) {
                    boss.leaveMap();
                }
            }
            this.bosses.clear();
            this.clearAllItems();
            this.onResetExtraState();
        } catch (Exception e) {
```

---

### BƯỚC 4: Chuẩn hóa chỉ số Phân thân `NinjaClone` bằng 1/10 Boss thật ngay khi triệu hồi
* **File**: `src/nro/models/boss/doanh_trai/NinjaAoTim.java`
* **Phương thức**: `private void callNinjaClones()` (khoảng dòng 116–120)
* **Nguyên nhân**: Trong `RedRibbonHQ.updateHPDame()` (dòng 428–431), phân thân Ninja được thiết kế có HP và Dame bằng `1/10` Boss thật (`bDame / 10`, `bHp / 10`). Tuy nhiên lúc `NinjaAoTim.callNinjaClones()` khởi tạo `NinjaClone`, nó lại truyền nguyên `this.nPoint.hpMax` và `this.nPoint.dame` (100% sức mạnh của Boss thật) cho cả 4–6 phân thân.
* **Thao tác sửa**: Chia `hpClone` và `dameClone` cho `10` (tối thiểu là `1`) ngay khi tạo phân thân.

#### Code TRƯỚC khi sửa (dòng 116–119):
```java
        int count = Util.nextInt(4, 6);
        int hpClone = this.nPoint.hpMax;
        int dameClone = this.nPoint.dame;
```

#### Code SAU khi sửa:
```java
        int count = Util.nextInt(4, 6);
        int hpClone = Math.max(1, this.nPoint.hpMax / 10);
        int dameClone = Math.max(1, this.nPoint.dame / 10);
```

---

### BƯỚC 5: Kích hoạt `updateHPDame()` và đồng bộ đồng hồ `ItemTime` khi thành viên vào Doanh Trại sau
* **File**: `src/nro/models/map/phoban/RedRibbonHQService.java`
* **Phương thức**: `public synchronized void joinDoanhTrai(Player pl)` (khoảng dòng 72–75)
* **Nguyên nhân**: Khi Doanh Trại đã mở (`pl.clan.doanhTrai != null`), thành viên vào sau chỉ được dịch chuyển vào Map 53 rồi `return` mà không gọi `pl.clan.doanhTrai.updateHPDame()` và không gửi gói tin đồng hồ đếm ngược `ItemTime`. Điều này tạo lỗ hổng cho phép dùng nhân vật sơ sinh mở Doanh Trại để quái/Boss có chỉ số cực thấp, sau đó mới đưa nhân vật chính sức đánh cao vào sau.
* **Thao tác sửa**: Bổ sung lời gọi `pl.clan.doanhTrai.updateHPDame()` và gửi thanh thời gian tương ứng (`sendTextTimePickDoanhTrai` nếu đang trong 5 phút nhặt mảnh, hoặc `sendTextDoanhTrai` nếu đang trong 30 phút đánh quái) ngay sau khi chuyển map vào Map 53. (Lưu ý thêm `import nro.models.services.ItemTimeService;` nếu đầu file chưa có).

#### Code TRƯỚC khi sửa (dòng 72–75):
```java
        if (pl.clan.doanhTrai != null) {
            ChangeMapService.gI().changeMapInYard(pl, 53, -1, 60);
            return;
        }
```

#### Code SAU khi sửa:
```java
        if (pl.clan.doanhTrai != null) {
            ChangeMapService.gI().changeMapInYard(pl, 53, -1, 60);
            pl.clan.doanhTrai.updateHPDame();
            if (pl.clan.doanhTrai.isTimePicking) {
                nro.models.services.ItemTimeService.gI().sendTextTimePickDoanhTrai(pl);
            } else {
                nro.models.services.ItemTimeService.gI().sendTextDoanhTrai(pl);
            }
            return;
        }
```

---

### BƯỚC 6: Reset cờ trạng thái Bulon & Trung Úy Trắng ở Map 59 và đồng bộ đồng hồ BĐKB khi hang sắp sập
* **File 6A**: `src/nro/models/map/phoban/RedRibbonHQ.java`
* **Phương thức**: `public void init(List<Player> participants)` (khoảng dòng 156–164)
* **Nguyên nhân**: Khi một phòng Doanh Trại được tái sử dụng cho Bang hội mới, các cờ `zone.isbulon1Alive`, `zone.isbulon2Alive` và `zone.isTUTAlive` tại Map 59 vẫn mang giá trị `false` từ phiên trước. Nếu người chơi tiêu diệt Bulon ngay trong lúc Trung Úy Trắng đang `CHAT_S` (trước khi `doneChatS()` kịp đặt `isTUTAlive = true`), Bulon sẽ không tự hồi sinh và khiên bất tử của Trung Úy Trắng bị lệch trạng thái.

#### Code TRƯỚC khi sửa trong `RedRibbonHQ.java` (dòng 156–164):
```java
            if (mapId == 59) {
                try {
                    long bHp = Math.min(avgDame * 60 * n, MAX_HP_CAP);
                    long bDame = Math.min((avgHp * 6) / 100, MAX_DAME_CAP);
                    bosses.add(new TrungUyTrang(zone, (int) Math.max(1L, bDame), (int) Math.max(1L, bHp)));
                } catch (Exception e) {
                    nro.models.utils.Logger.logException(RedRibbonHQ.class, e);
                }
            }
```

#### Code SAU khi sửa trong `RedRibbonHQ.java`:
```java
            if (mapId == 59) {
                zone.isbulon1Alive = true;
                zone.isbulon2Alive = true;
                zone.isTUTAlive = true;
                try {
                    long bHp = Math.min(avgDame * 60 * n, MAX_HP_CAP);
                    long bDame = Math.min((avgHp * 6) / 100, MAX_DAME_CAP);
                    bosses.add(new TrungUyTrang(zone, (int) Math.max(1L, bDame), (int) Math.max(1L, bHp)));
                } catch (Exception e) {
                    nro.models.utils.Logger.logException(RedRibbonHQ.class, e);
                }
            }
```

* **File 6B**: `src/nro/models/services/ItemTimeService.java`
* **Phương thức**: `public void sendTextBanDoKhoBau(Player player)` (khoảng dòng 215–227)
* **Nguyên nhân**: Khi người chơi hoàn tất hiệu ứng tàu ngầm (`Controller.java` case `-105`, dòng 177), server gọi `ItemTimeService.gI().sendTextBanDoKhoBau(player)`. Nếu lúc đó BĐKB đang trong 60 giây đếm ngược sập hang (`isKickout() == true`), hàm `sendTextBanDoKhoBau` lại `return` trắng thay vì hiển thị đồng hồ `"Hang sắp sập:"`.

#### Code TRƯỚC khi sửa trong `ItemTimeService.java` (dòng 215–227):
```java
    public void sendTextBanDoKhoBau(Player player) {
        if (player != null && player.clan != null && player.clan.BanDoKhoBau != null && player.clan.BanDoKhoBau.isOpened()) {
            if (player.clan.BanDoKhoBau.isKickout()) {
                return;
            }
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.BanDoKhoBau.getLastTimeOpen()) / 1000);
            int secondsLeft = (BanDoKhoBau.TIME_BAN_DO_KHO_BAU / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, BAN_DO_KHO_BAU, "Hang kho báu:", secondsLeft);
        }
    }
```

#### Code SAU khi sửa trong `ItemTimeService.java`:
```java
    public void sendTextBanDoKhoBau(Player player) {
        if (player != null && player.clan != null && player.clan.BanDoKhoBau != null && player.clan.BanDoKhoBau.isOpened()) {
            if (player.clan.BanDoKhoBau.isKickout()) {
                sendTextTimeSapBanDoKhoBau(player);
                return;
            }
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.BanDoKhoBau.getLastTimeOpen()) / 1000);
            int secondsLeft = (BanDoKhoBau.TIME_BAN_DO_KHO_BAU / 1000) - secondPassed;
            if (secondsLeft < 0 || secondsLeft > 1800) {
                return;
            }
            sendTextTime(player, BAN_DO_KHO_BAU, "Hang kho báu:", secondsLeft);
        }
    }
```

---

## CHECKLIST KIỂM TRA SAU KHI AI AGENT THỰC THI (VERIFICATION)

1. **Kiểm tra Biên dịch (Build Verification)**:
   - Biên dịch toàn bộ project đảm bảo 0 lỗi cú pháp, 0 thiếu `import`.
2. **Kiểm tra Bản Đồ Kho Báu (`BanDoKhoBau`)**:
   - Dùng Bang chủ mở Bản Đồ Kho Báu 1 lần -> Kiểm tra biến `player.timesPerDayBDKB` và `player.clan.timesPerDayBDKB` chỉ tăng đúng `+1` (không còn bị nhảy lên `2`).
   - Ngay khi hết 5 giây tàu ngầm đáp xuống Map `135`, đồng hồ `"Hang kho báu: 30:00"` hiện lên ngay lập tức.
3. **Kiểm tra Doanh Trại Độc Nhãn (`RedRibbonHQ`)**:
   - Cho nhân vật thứ 2 có chỉ số cao vào Doanh Trại sau khi đã mở -> Máu/Dame của Quái và Boss còn sống trong các khu tự động cập nhật tăng lên theo trung bình cộng mới (`updateHPDame()`), nhân vật thứ 2 nhận được đồng hồ đếm ngược ngay khi vào Map `53`.
   - Tại Map `59`, đánh Bulon khi Trung Úy Trắng còn sống -> Trung Úy Trắng bất tử (`0` sát thương) và Bulon hồi sinh sau 10 giây kể cả ở lần mở phòng thứ 2, thứ 3.
   - Tại Map `54`, khi Ninja Áo Tím phân thân -> Phân thân có HP/Dame bằng `1/10` Boss thật, và khi hết giờ Doanh Trại (`dispose()`) không xảy ra lỗi `ConcurrentModificationException`.
   - Khi hạ xong toàn bộ quái/Boss và nói chuyện với Độc Nhãn ở Map `57` -> Các mảnh thẻ sưu tầm (`837..842, 859, 956`) tồn tại đủ suốt 5 phút thu thập, không bị tự hủy sau 50 giây.
