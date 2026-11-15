# TASK_MEMORY.md — Bộ Nhớ Ngữ Cảnh & Sổ Theo Dõi Thực Thi (Antigravity Persistent Memory)

> **Hướng dẫn cho AI Agent (Antigravity):**
> 1. **ĐỌC FILE NÀY ĐẦU TIÊN** khi bắt đầu mỗi phiên làm việc để nắm trạng thái hiện tại của dự án.
> 2. **CẬP NHẬT FILE NÀY CUỐI CÙNG** sau khi hoàn thành bất kỳ task nào người dùng giao.
> 3. **KHÔNG** tự ý sinh thêm task mới vào mục `Đang Thực Hiện` nếu người dùng không yêu cầu.

---

## 1. Trạng Thái Kiến Trúc & Môi Trường Hiện Tại (System Snapshot)

* **Thư mục Server Java**: `ToolCaiGameV3/Source/NroBaKhi` (NetBeans Ant Project `build.xml`, xuất ra `NroBaKhi.jar`).
* **Thư mục Web/Admin PHP**: `ToolCaiGameV3/Source/WebTeamobi` (PHP thuần + MySQLi kết nối trực tiếp vào DB `nrobakhi`).
* **Cơ sở dữ liệu**: MySQL (`nrobakhi.sql` - 43 bảng). Cấu hình kết nối Java tại `ToolCaiGameV3/Source/NroBaKhi/Config.properties` (HikariCP) và PHP tại `ToolCaiGameV3/Source/WebTeamobi/config.php`.
* **Bộ 4 Tệp Chỉ Mục Đã Khởi Tạo** (Tra cứu trước khi quét code):
  * `GEMINI.md` / `AGENTS.md`: Quy tắc thực thi, chống bịa task, chuẩn Clean Code & SOLID.
  * `CODEBASE_MAP.md`: Bản đồ 57 packages (543 file Java), 33 file PHP và chỉ mục dòng (`L...–L...`) của các class lớn.
  * `DB_JSON_SCHEMA.md`: Đặc tả 43 bảng SQL và thứ tự index `[0]..[N]` của 39 cột mảng JSON trong bảng `player` & `clan`.
  * `PACKET_PROTOCOL.md`: Đặc tả 58 mã lệnh mạng (`cmd`) trong `Controller.java`.

---

## 2. Các Lỗi Tiềm Ẩn Đã Phát Hiện Trong Code Gốc (Known Issues Registry)

*(Lưu ý cho AI Agent: Chỉ sửa các mục dưới đây khi người dùng yêu cầu trực tiếp, nhưng phải biết để không dẫm phải khi viết tính năng liên quan)*:
1. **`Controller.java` (`L197–L201`)**: Nhánh `case 42:` bị comment mất lệnh `break;`, gây rơi luồng (fall-through) xuống `case -127:` (`LuckyRound.readOpenBall`).
2. **`MrBlue.java` & `PlayerDAO.java` (Lệch Index JSON)**:
   * Cột `data_item_time`: `PlayerDAO` lưu thêm `isUseCoBonLa` (`[12]`) và `isUseBuaSanta` (`[14]`), nhưng `MrBlue.loadPlayer` chưa cập nhật thứ tự đọc từ `[12]` trở đi; dòng `643` đọc nhầm `get(28)` thay vì `get(29)`.
   * Cột `data_item_event`: `PlayerDAO` chỉ lưu 6 phần tử (`[0..5]`), nhưng `MrBlue` đọc tới 16 phần tử (`[0..15]`).
   * Cột `conduongrandoc`: `MrBlue.java:931` đọc nhầm `dataArray.get(2)` hai lần thay vì `get(3)`.
   * Cột `masterDoesNotAttack`: `MrBlue.java:945` gọi sai tên cột `rs.getBoolean("masterDoesAttack")`.
3. **`ConsignShopService.java` & `ConsignShopManager.java`**:
   * Các hàm `buyItem()`, `claimOrDel()` chưa có khóa `synchronized` trên đối tượng `ConsignItem`.
   * Hàm `ConsignShopManager.save()` đang chạy `DELETE FROM shop_ky_gui` toàn bảng rồi `INSERT` lại.
4. **`WebTeamobi`**:
   * File `WebTeamobi/get_cols.php` chưa kiểm tra quyền Admin.
   * File `WebTeamobi/admin/account.php` (`L31–L48`) đang gán cứng `$username = "0325420834"; $password = "bansrcnro";` ở nhánh `action === 'add'`.

---

## 3. Task Đang Thực Hiện (Current Active Task)

* **Trạng thái**: Sẵn sàng nhận lệnh từ người dùng.
* **Yêu cầu hiện tại**: *(Trống — Chờ người dùng giao task cụ thể)*.

---

## 4. Lịch Sử Thực Thi Gần Nhất (Completed Tasks Log)

* **[Refactor Hệ Thống Map — Hoàn Thành Toàn Bộ 4 Giai Đoạn Đạt Chuẩn 10/10]**:
  * **Giai đoạn 1 (Sửa 10 Lỗi Nghiêm Trọng)**: Đã sửa đệ quy vô hạn trong `getAllMaps()`, khử vòng lặp vô hạn `while(true)` gây treo CPU trong `getZone()`, sửa tràn số nguyên sát thương/HP Doanh Trại trong `RedRibbonHQ.updateHPDame()`, bổ sung `break;` trong `ChangeMapService` (capsule & núi khỉ vàng), đồng bộ cờ `isPickedUp` và `AtomicInteger` cho `itemMapId`.
  * **Giai đoạn 2 (Tối Ưu Hiệu Năng & Disk/DB I/O)**: Nạp tài nguyên bản đồ tĩnh (`item_bg_map_data`, `eff_map`) lên RAM 0-disk-read; dùng chung `MAP_SCHEDULER` với chu kỳ 1.000ms; cố định 4 kén `maBuHolds`; đẩy lưu SQL người chơi offline sang Virtual Thread trong `BlackBallWar`.
  * **Giai đoạn 3 (Tái Cấu Trúc OOP & DRY)**: Xây dựng lớp cha trừu tượng [AbstractClanDungeon.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/AbstractClanDungeon.java); 4 phó bản kế thừa; rút gọn 155 dòng trùng lặp trong `MapService.getMapCanJoin()` qua `isZoneCleared()` và `resolveDungeonTransition()`.
  * **Giai đoạn 4 (Clean Code & Thread Safety 10/10)**:
    - Triển khai **Initialization-on-demand Holder Singleton** chống race condition đa luồng trên 4 lớp Service cốt lõi: `ChangeMapService`, `MapService`, `ItemMapService`, `NpcService`.
    - Dọn sạch toàn bộ empty catch blocks trong `ChangeMapService.java`, `Map.java`, `Zone.java`, chuyển sang ghi log chuẩn qua `Logger.logException(...)`.
    - Dọn sạch các import thừa (`java.util.Random`, `Executors`, `ScheduledExecutorService`, `TimeUnit`) tại `NpcManager.java`, `BanDoKhoBau.java`, `DestronGas.java`, `SnakeWay.java`, `RedRibbonHQ.java`, `MajinBuu14H.java`.
    - Chuyển toàn bộ vòng lặp duyệt `players`, `mobs`, `bosses` trong `Zone.java` sang duyệt ngược `for (int i = list.size() - 1; i >= 0; i--)` kết hợp check `null` theo đúng Điều 17 của `GEMINI.md`.
    - Thêm kiểm tra biên an toàn `zoneId >= 0 && zoneId < map.zones.size()` trong `ChangeMapService.getZoneJoinByMapIdAndZoneId()`.
    - Kiểm tra biên dịch ngầm tự động thành công 100% (`[COMPILE_SUCCESS]`), đóng gói sạch vào `NroBaKhi.jar`.

* **[Refactor Hệ Thống Map — Hoàn Thành Giai Đoạn 2 (Tối Ưu Hiệu Năng & Disk/DB I/O)]**:
  * Đã chuyển việc đọc file `item_bg_map_data/` và `eff_map/` sang nạp 1 lần duy nhất vào bộ nhớ RAM (`Map.bgItemData`, `Map.effMapData`) khi khởi động server, loại bỏ hoàn toàn việc đọc đĩa cứng trong `Zone.mapInfo()` tại [Map.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/Map.java) & [Zone.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/Zone.java).
  * Đã chuẩn hóa chu kỳ cập nhật `Map.run()` về đúng **1.000ms (1 giây)** thay vì 5.000ms, đồng thời sử dụng `ScheduledExecutorService` dùng chung đa luồng (`MAP_SCHEDULER`) thay cho 180 thread pool riêng lẻ tại [Map.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/Map.java).
  * Đã bảo vệ tính an toàn đa luồng của `Zone.maBuHolds`, khởi tạo sẵn 4 kén giam ở Map 128 trong constructor và không ghi đè `new ArrayList<>()` tại [Zone.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/Zone.java).
  * Đã bất đồng bộ hóa việc nạp và cập nhật SQL người chơi offline sang Virtual Thread trong sự kiện Ngọc Rồng Sao Đen tại [BlackBallWar.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BlackBallWar.java), giải phóng hàm `synchronized win()`.
  * Đã kiểm tra biên dịch ngầm tự động thành công 100% (`[COMPILE_SUCCESS]`) và đóng gói cập nhật vào `NroBaKhi.jar`.

* **[Refactor Hệ Thống Map — Hoàn Thành Giai Đoạn 1 (Vá 10 Lỗi Chí Mạng)]**:
  * Đã sửa lỗi đệ quy vô tận `getAllMaps()` trả về `Manager.MAPS` trong [MapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/MapService.java).
  * Đã loại bỏ vòng lặp `while` treo CPU trong `getZone()`, lọc danh sách khu an toàn $O(N)$ trong [MapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/MapService.java).
  * Đã đồng nhất kiểm tra `isMapCold()` nhận diện đúng map 152 trong [MapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/MapService.java).
  * Đã thêm `break;` chống rơi nhánh (fall-through) tại `case ConstMap.CHANGE_CAPSULE` và `case 80` (Núi khỉ vàng) trong [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java).
  * Đã sửa lỗi cờ `isPickedUp` không bao giờ bật và cấp phát `itemMapId` an toàn luồng với `AtomicInteger` trong [Zone.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/Zone.java) & [ItemMap.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/ItemMap.java).
  * Đã sửa lỗi tràn số âm máu quái trong Doanh Trại Độc Nhãn tại [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java).
  * Đã kiểm tra biên dịch ngầm tự động thành công 100% (`[COMPILE_SUCCESS]`) và đóng gói cập nhật vào `NroBaKhi.jar`.

* **[Nâng Cấp Bộ Quy Tắc Toàn Diện — Mở Rộng 20 Chốt Chặn Kỹ Thuật]**:
  * Đã bổ sung 8 quy tắc kỹ thuật chuyên sâu thực chiến cho game Ngọc Rồng vào [gemini.md](file:///d:/NgocRongBaKhi/ToolCaiGameV3/gemini.md) và [GEMINI.md](file:///d:/NgocRongBaKhi/GEMINI.md).
  * Bộ quy tắc mới bao gồm: Kiểm tra biên dịch ngầm siêu tốc, Sổ theo dõi bản vá SQL, Chống tràn số âm (Negative Gold/EXP Bug), Độc quyền bộ nhớ RAM khi sửa DB từ Web, An toàn duyệt Zone chống văng ConcurrentModification, Toàn vẹn thuộc tính Option Item, An toàn giao dịch chống hack dup đồ, và Cấm hardcode bí danh/IP/port.

* **[Hoàn Thiện Hệ Sinh Thái Vibe Coding Toàn Diện]**:
  * Đã tạo `GEMINI.md` tại thư mục gốc dự án (`d:\NgocRongBaKhi\GEMINI.md`) để Antigravity tự động kích hoạt 12 chốt chặn kỹ thuật trong mọi phiên làm việc mà không cần người dùng tag file.
  * Đã tạo script biên dịch ngầm siêu tốc `ToolCaiGameV3/Source/NroBaKhi/compile_check.bat` (chỉ chạy javac và đóng gói jar, không pause, không bật server) giúp AI tự kiểm thử lỗi cú pháp ngầm trong 3 giây.
  * Đã tạo `ToolCaiGameV3/SQL_PATCHES.md` để tự động ghi nhận mọi câu lệnh SQL phát sinh khi code tính năng mới, chống thất lạc khi deploy lên VPS.
  * Đã tạo `ToolCaiGameV3/DEV_CHEAT_SHEET.md` tổng hợp toàn bộ lệnh chat Admin (`a`, `item`, `m`, `hs`, `toado`, `brl`) và lệnh đệ tử để test tính năng trong 10 giây.
  * Toàn bộ 543 file Java đã được biên dịch thành công 100% với JDK 21.

* **[Khởi tạo Hệ thống Chỉ mục AI]**:
  * Đã quét toàn bộ dự án `NgocRongBaKhi` và tạo bộ tài liệu `AGENTS.md`, `GEMINI.md`, `PACKET_PROTOCOL.md`, `DB_JSON_SCHEMA.md`, `CODEBASE_MAP.md`, `TASK_MEMORY.md`.
  * Chưa thay đổi bất kỳ dòng code logic nào trong `Source/NroBaKhi` hay `Source/WebTeamobi`.
