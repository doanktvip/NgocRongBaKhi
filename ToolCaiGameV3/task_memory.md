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
* **Điều Phối Nhịp Game Loop (Zone.update())**: Bộ lập lịch tập trung nằm duy nhất tại `Manager.initMap()` (`Manager.java` L199–L242), gom các Zone theo batch 10 khu và cập nhật 1 giây/lần bằng `CompletionService` đa luồng. **TUYỆT ĐỐI KHÔNG gọi `map.run()`** trong vòng đời server để tránh chạy đúp luồng `zone.update()`.

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

* **[Chặn Hiển Thị Danh Sách Khu & Đổi Khu Trong Tất Cả Map Phó Bản / Offline]**:
  * **Hiện tượng & Nguyên nhân**:
    - Khi người chơi (đặc biệt tài khoản Admin) ở trong map phó bản (như Map 149 - Khí Gas Hủy Diệt), việc nhấn vào biển báo khu vực làm hiện popup danh sách "Chọn khu vực" (0: 1/15, 1: 0/15...).
    - Nguyên nhân: Trong lõi Server, các map phó bản (BĐKB, Khí Gas, Doanh Trại, CDRD) vẫn cần nhiều Zone (0..49) để gán làm phòng riêng (instance) cho từng Bang hội khác nhau nhằm tránh xung đột. Tuy nhiên, người chơi không được phép nhìn thấy hay tự do chuyển khu. Code cũ trong [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java) lại bọc điều kiện kiểm tra `MapService.isMapPhoBan` bên trong nhánh `if (!pl.isAdmin())`, khiến tài khoản Admin bị bỏ qua kiểm tra và Server vẫn gửi gói tin `cmd 29` mở bảng chọn khu.
  * **Khắc phục**:
    - Tại [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java):
      1. Trong hàm `openZoneUI(Player pl)`: Gỡ bỏ ngoại lệ `!pl.isAdmin()`, chặn hiển thị UI khu vực cho mọi đối tượng nếu đang ở map offline, map phó bản (`isMapPhoBan`), map Ma Bư hoặc map bụng Bư 128:
         ```java
         if (MapService.gI().isMapOffline(pl.zone.map.mapId)
                 || MapService.gI().isMapPhoBan(pl.zone.map.mapId)
                 || MapService.gI().isMapMaBu(pl.zone.map.mapId)
                 || pl.zone.map.mapId == 128) {
             Service.gI().sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
             return;
         }
         ```
      2. Trong hàm `changeZone(Player pl, int zoneId)`: Đồng bộ điều kiện chặn chuyển khu tuyệt đối cho mọi người chơi (kể cả Admin) khi đang ở map phó bản, offline hoặc Ma Bư.
  * **Biên dịch**: `javac` biên dịch 100% thành công không có lỗi cú pháp (`build\classes` đã cập nhật). Khi tắt tiến trình server game đang chạy, chạy `compile_check.bat` để cập nhật `NroBaKhi.jar`.

* **[Chuẩn Hóa Khí Gas Hủy Diệt: Cấp Độ 1-100, HP & Dame Đồng Bộ Theo BDKB, Tối Giản Code Hàm]**:
  * **Yêu cầu**: Sửa `DestronGasService.java` và `DestronGas.java` sao cho cấp độ phó bản là 1-100, lượng HP và Dame của Mob và Boss được tính toán chuẩn theo tỉ lệ của Bản Đồ Kho Báu (BDKB), cố định công thức trực tiếp trong hàm `init()` và `update()` giống hệt `BanDoKhoBau.java` để code gọn gàng, loại bỏ các hằng số MIN rườm rà.
  * **Khắc phục**:
    1. Tại [DestronGas.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/DestronGas.java):
       - Cấu hình cấp độ và thời gian: `MIN_LEVEL = 1;`, `MAX_LEVEL = 100;`, `TIME_KICKOUT_KGHD = 60_000;`, `MAX_TIMES_PER_DAY = 3;`, `N_DAY_JOIN_CLAN = 2;`, `MAP_BOSS = 148;`.
       - Lược bỏ toàn bộ các hằng số `MIN_HP`, `MIN_DAME`, `MAX_HP`, `MAX_DAME` rườm rà; đưa các con số cố định trực tiếp vào hàm theo đúng phong cách của `BanDoKhoBau.java`:
         * Quái thủ lĩnh / Big Mob (`CO_MAY_HUY_DIET` tempId 76): `dame = 20_000_000L * scale`, `hp = 2_000_000_000L * scale`.
         * Quái tinh anh / Super Mob (các vị trí đặc biệt các map): `dame = 10_000_000L * scale`, `hp = 1_000_000_000L * scale`.
         * Quái thường: `dame = 5_000_000L * scale`, `hp = 500_000_000L * scale`.
         * Boss (`DrLychee` / `Hatchiyack`): `dame = 20_000_000L * scale`, `hp = 2_000_000_000L * scale`.
       - Giữ nhịp làm mới `sendTextTime()` mỗi 15 giây.
    2. Tại [DestronGasService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/DestronGasService.java):
       - Dùng `DestronGas.MIN_LEVEL`, `DestronGas.MAX_LEVEL`, `DestronGas.N_DAY_JOIN_CLAN`, `DestronGas.MAX_TIMES_PER_DAY`.
       - Chốt an toàn kiểm tra `player.getSession() == null || !player.getSession().actived`.
    3. Tại [Input.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/Input.java):
       - Dùng `DestronGas.MIN_LEVEL` và `DestronGas.MAX_LEVEL` cho form chọn cấp độ và bộ kiểm duyệt.
    4. Tại [MrPoPo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/MrPoPo.java):
       - Dùng `DestronGas.N_DAY_JOIN_CLAN`.
  * **Biên dịch**: `[COMPILE_SUCCESS]` Đã biên dịch và đóng gói thành công mã nguồn mới vào `NroBaKhi.jar`.

* **[Bổ Sung Điều Kiện Mở Thành Viên (actived) Cho Bản Đồ Kho Báu (BDKB)]**:
  * **Yêu cầu**: Bổ sung điều kiện người chơi phải đã mở thành viên (`actived`) mới được tham gia / mở Bản Đồ Kho Báu (tương tự như Doanh Trại, Khí Gas Hủy Diệt và Con Đường Rắn Độc).
  * **Khắc phục**:
    1. Tại [QuyLaoKame.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/QuyLaoKame.java#L306-L340):
       - Tại `handleTreasureMap(Player player)` (khi bấm vào menu "Kho báu dưới biển"): kiểm tra nếu `player.getSession() == null || !player.getSession().actived` thì gửi thông báo `"Vui lòng mở thành viên trước"` và ngắt menu ngay.
       - Tại `handleMenuTreasureMap(Player player, int select)` (nhánh `case 2` - "Chọn cấp độ" hoặc "Đồng ý"): chốt an toàn kép kiểm tra `player.getSession() == null || !player.getSession().actived`, báo `"Vui lòng mở thành viên trước"`.
    2. Tại [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java#L36-L55):
       - Tại hàm `openBanDoKhoBau(Player player, byte level)`: kiểm tra nếu người mở không có session hoặc `!player.getSession().actived` thì chặn và báo `"Vui lòng mở thành viên trước"`.
    3. Tại [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java#L845-L865):
       - Tại hàm `goToDBKB(Player player)`: chốt kiểm tra tầng chuyển map, chặn người chơi chưa mở thành viên đi vào hang kho báu.
  * **File cập nhật**:
    - [QuyLaoKame.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/QuyLaoKame.java)
    - [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java)
    - [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java)
  * **Biên dịch**: `[COMPILE_SUCCESS]` Đã biên dịch và đóng gói thành công mã nguồn mới vào `NroBaKhi.jar`.

* **[Chuyển Đổi Sát Thương Bẫy Chông Map 135 BDKB Thành 10% HP Max Chuẩn & Dọn Code Dư Thừa]**:
  * **Yêu cầu**: Sát thương bẫy chông sắt (Map 135 Bản Đồ Kho Báu) gây trừ đúng 10% lượng HP tối đa (`hpMax`) của người chơi giẫm phải; xóa bỏ vòng lặp gán `trap.dame` dư thừa trong `BanDoKhoBau.java`.
  * **Khắc phục**:
    1. Tại [TrapMap.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/TrapMap.java#L16-L26), sửa hàm `doPlayer(Player player)`:
       - Tính sát thương chuẩn: `long damage = Math.max(1L, (long) player.nPoint.hpMax * 10 / 100);`.
       - Gọi `player.injured(null, damage, true, false);` với cờ `piercing = true` để xuyên giáp/thủ, trừ chính xác 10% HP Max mà không bị lệch số do phòng thủ hay giáp.
       - Xử lý nếu người chơi hết máu: gọi `player.setDie()` đúng chuẩn.
    2. Tại [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java#L135-L141), đã xóa bỏ vòng lặp `for (TrapMap trap : zone.trapMaps)` tính toán sát thương số phẳng cố định cũ.
  * **File cập nhật**:
    - [TrapMap.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/TrapMap.java)
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
  * **Biên dịch**: `javac` JDK 21 biên dịch thành công 100% không có lỗi.

* **[Sửa Triệt Để Lỗi Không WinDT & Boss Disposed Trong Doanh Trại]**:
  * **Nguyên nhân cốt lõi phát hiện thêm**:
    1. Toàn bộ các Boss trong Doanh Trại (`TrungUyTrang`, `TrungUyXanhLo`, `TrungUyThep`, `NinjaAoTim`, `RobotVeSi`) khi chết đều gọi `leaveMap()` -> `this.dispose()`.
    2. Hàm `dispose()` của `Player.java` dòng 1761 thực hiện `nPoint.dispose(); nPoint = null;`.
    3. Khi `nPoint` bị gán `null`, hàm `Player.isDie()` (`if (this.nPoint != null && this.nPoint.hp <= 0)`) **trả về `false`**! Do đó kiểm tra `!boss.isDie()` luôn trả về `true` trên các Boss đã chết và bị dispose!
    4. Quá trình đóng gói `NroBaKhi.jar` bị hệ điều hành Windows chặn ghi đè vì Server game (`java.exe`) đang chạy và khóa tệp `.jar`. Server vẫn đang chạy mã bytecode cũ từ trước.
  * **Khắc phục**:
    1. Tại [AbstractClanDungeon.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/AbstractClanDungeon.java):
       - Chuẩn hóa `areAllMobsDead()`: duyệt toàn bộ mob trong các zone, nếu không còn mob nào sống (`!mob.isDie()`) thì trả về `true`.
       - Chuẩn hóa `areAllBossesDead()`: Boss chỉ được coi là còn sống nếu `boss.zone != null && boss.nPoint != null && !boss.isDie() && boss.bossStatus != BossStatus.REST && boss.bossStatus != BossStatus.DIE`. Đồng thời kiểm tra `zone.getBosses()`.
    2. Tại [DocNhan.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/DocNhan.java):
       - Bổ sung chốt an toàn kép: Khi người chơi tới Map 57 nói chuyện với Độc Nhãn, kiểm tra nếu `dt.areAllCharactersDead()` HOẶC `(zone57Clean && dt.areAllBossesDead())` thì lập tức kích hoạt `dt.winDT = true` ngay để mở giai đoạn 5 phút nhặt thẻ.
  * **File cập nhật**:
    - [AbstractClanDungeon.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/AbstractClanDungeon.java)
    - [DocNhan.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/DocNhan.java)
  * **Trạng thái**: `[COMPILE_SUCCESS]` Đã biên dịch và đóng gói thành công mã nguồn mới vào `NroBaKhi.jar`.

* **[Sửa Lỗi Mảnh Sưu Tầm Doanh Trại Bị Mất Sau 50 Giây]**:
  * **Nguyên nhân**: `ItemMap.update()` có đoạn code quét xóa vật phẩm sau 50 giây (`Util.canDoWithTime(createTime, 50000)`), trong đó điều kiện ngoại lệ chỉ miễn trừ cho Ngọc Rồng (ID 14..20), khiến toàn bộ Mảnh Sưu Tầm Doanh Trại (ID: 837, 838, 839, 840, 841, 842, 859, 956) bị biến mất sau 50s thay vì tồn tại đủ 5 phút.
  * **Khắc phục**: Tại [ItemMap.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/ItemMap.java#L110-L116), mở rộng miễn trừ toàn bộ cho `!MapService.gI().isMapDoanhTrai(this.zone.map.mapId)`. Mọi vật phẩm rơi trong Doanh Trại sẽ tồn tại trọn vẹn 5 phút cho đến khi phòng Doanh Trại kết thúc và tự động giải phóng (`RedRibbonHQ.clearAllItems()`).
  * **Biên dịch**: `[COMPILE_SUCCESS]` đã biên dịch và đóng gói thành công `NroBaKhi.jar`.

* **[Đồng Bộ Toàn Bộ Quá Trình Rơi Đồ Quái BDKB Sang Hệ Thống DropItem]**:
  * Đã chuyển đổi toàn bộ cơ chế rơi đồ của Bigmob, Supermob và Mob thường trong Bản Đồ Kho Báu tại [Mob.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob/Mob.java#L1052-L1115) sang dùng chuẩn **`DropItem.to(player, this.zone, this.location.x, this.location.y)`**:
    1. **Bigmob**:
       - `DropItem.to(...).item(ConstItem.THOI_VANG, 1)` lặp $1 \to 5$ thỏi theo level và `.executeAll()` bung tỏa cánh sen.
       - `DropItem.to(...).thanLinh().rate(rateTL).execute()` cho đồ Thần Linh (tối đa 3%).
       - `DropItem.to(...).weighted(nrWeights).execute()` cho Ngọc Rồng 2s–5s.
    2. **Supermob**:
       - `DropItem.to(...).item(ConstItem.THOI_VANG, 1).rate(50).executeAll()`.
       - `DropItem.to(...).random(4s, 5s, 6s).rate(30).execute()`.
    3. **Mob thường**:
       - `DropItem.to(...).item(ConstItem.THOI_VANG, 1).rate(rateTv).execute()`.
       - `DropItem.to(...).random(6s, 7s).rate(5).execute()`.
  * Đảm bảo mọi vật phẩm rơi đều được kiểm soát chống văng ra ngoài map (`clampX`), tự động bám mặt sàn vật lý (`getGroundY`), tương thích bùa thu hút và hiển thị mượt mà.
  * **File cập nhật**: [Mob.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob/Mob.java).
  * **Biên dịch**: `javac` JDK 21 biên dịch thành công 100% tất cả các file vào `build/classes`.

* **[Hệ Thống Rơi Đồ Toàn Diện Cho Bigmob, Supermob Và Mob Thường BDKB]**:
  * Đã tích hợp cơ chế rơi đồ biến thiên theo cấp độ Level ($1 \to 100$) cho toàn bộ quái trong Bản Đồ Kho Báu tại [Mob.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob/Mob.java#L1050-L1130):
    1. **Bigmob (Vua Bạch Tuộc & Robot bảo vệ)**:
       - Rơi $1 \to 5$ Thỏi Vàng theo level (`level * 5 / 100`).
       - Tỉ lệ rơi 1 món Thần Linh tối đa $3\%$ ở Level 100 bằng `RewardService.randItemDoCap(CAP_DO_THAN_LINH)`.
       - Rơi 1 viên Ngọc Rồng từ 2s đến 5s với trọng số biến thiên theo level.
    2. **Supermob (Quái Tinh Anh `lvMob == 1`)**:
       - 50% cơ hội rơi $1 \to 2$ Thỏi Vàng (ở cấp $\ge 70$ cơ hội ra 2 thỏi).
       - 30% cơ hội rơi Ngọc Rồng 4s, 5s hoặc 6s.
    3. **Mob thường (`lvMob == 0`)**:
       - $1\% \to 2\%$ cơ hội rơi 1 Thỏi Vàng.
       - $5\%$ cơ hội rơi Ngọc Rồng 6s hoặc 7s.
  * **File cập nhật**: [Mob.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob/Mob.java).
  * **Biên dịch**: `javac` JDK 21 biên dịch thành công 100% tất cả các file vào `build/classes`.

* **[Hệ Thống Rơi Đồ Toàn Diện Biến Thiên Theo Level Cho Boss Trung Úy Xanh Lơ (BDKB)]**:
  * Đã áp dụng hệ số Level ($1 \to 100$) thống nhất cho toàn bộ cơ chế rơi đồ tại [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java):
    1. **Thỏi Vàng**: Tuyến tính từ 1 thỏi (Level 1) đến tối đa 20 thỏi (Level 100), tự động tỏa cánh sen bằng `DropItem.executeAll()`.
    2. **Trang bị Thần Linh**: Tỉ lệ tăng dần theo cấp từ $1\%$ (Level 1) đến $10\%$ (Level 100) bằng `.thanLinh().rate(rateThanLinh).execute()`.
    3. **Ngọc Rồng (1s -> 7s)**: Trọng số của từng viên biến thiên theo Level bằng `.weighted(dragonBallWeights).execute()`. Ở Level thấp chủ yếu ra 5s, 6s, 7s; ở Level 100 thì tỉ lệ ra 1s, 2s, 3s chiếm hơn 70%.
    4. **Vật phẩm sự kiện & Point**: Rơi item 705 và cộng 5 điểm sự kiện.
  * **File cập nhật**: [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java).
  * **Biên dịch**: `javac` JDK 21 biên dịch thành công 100% tất cả các file vào `build/classes`.

* **[Ứng Dụng Fluent DropItem Cho Boss Trung Úy Xanh Lơ (BDKB)]**:
  * Đã chuyển đổi toàn bộ cơ chế rơi đồ của Boss Trung Úy Xanh Lơ sang hệ thống `DropItem` (`this.drop(plKill)`):
    1. **Thỏi vàng (ID 457)**: Tính theo công thức $\min(20, \max(1, \text{round}(\frac{\text{Level} \times 20}{100})))$, tự động bung tỏa rải rác từng thỏi hình cánh sen trên mặt sàn map bằng `DropItem.executeAll()`.
    2. **Trang bị Thần Linh**: Tỉ lệ 5% rơi 1 món Thần Linh chuẩn option bằng `this.drop(plKill).thanLinh().rate(5).execute()`.
    3. **Ngọc Rồng (1s, 2s, 3s)**: Rơi ngẫu nhiên 1 trong 3 viên Ngọc Rồng 1, 2, 3 sao bằng `this.drop(plKill).random(14, 15, 16).execute()`.
    4. **Vật phẩm sự kiện**: Rơi item 705 và cộng 5 điểm sự kiện.
  * **File cập nhật**: [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java).
  * **Biên dịch**: `javac` JDK 21 biên dịch thành công 100% tất cả các file vào `build/classes`.

* **[Phần Thưởng Rơi Thỏi Vàng Rải Rác Từng Thỏi Theo Level Cho Boss Trung Úy Xanh Lơ (BDKB)]**:
  * Đã xóa constructor overload thừa `TrungUyXanhLo(Zone, int, int, int)`.
  * Lưu trữ biến `private int level;` từ constructor chính.
  * Trong hàm `reward(Player plKill)`:
    - Tính toán số thỏi vàng theo công thức tuyến tính: `int soThoiVang = Math.min(20, Math.max(1, (int) Math.round(this.level * 20.0 / 100.0)));`.
      + Level 1–4: 1 thỏi.
      + Level 50: 10 thỏi.
      + Level 100: Tối đa 20 thỏi.
    - Duyệt vòng lặp thả từng thỏi vàng (Item ID: 457, quantity = 1) rải rác xung quanh tọa độ của Boss trên sàn map bằng `Service.gI().dropItemMap(...)`.
  * **File cập nhật**: [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java).
  * **Biên dịch**: `javac` JDK 21 biên dịch thành công 100% tất cả các file vào `build/classes`.

* **[Chuyển Đổi Boss Trung Úy Xanh Lơ (BDKB) Sang BossesData Chuẩn Mẫu Doanh Trại]**:
  * **1. Chuyển `new BossData(...)` sang `BossesData.java`**:
    - Thêm hàm builder tĩnh `public static BossData TRUNG_UY_XANH_LO_BDKB(int dame, int hp)` trong [BossesData.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/BossesData.java#L1192-L1220), cấu hình đầy đủ chỉ số, trang bị, skill `FULL_DEMON` và hội thoại chuẩn.
  * **2. Refactor `TrungUyXanhLo.java` (BDKB) giống hệt cấu trúc Boss Doanh Trại**:
    - Import `nro.models.boss.BossesData;` và `nro.models.map.phoban.BanDoKhoBau;`.
    - Khai báo biến `private BanDoKhoBau bdkb;` và gán trong constructor.
    - Gọi `super(PHOBANBDKB, BossID.TRUNG_UY_XANH_LO, BossesData.TRUNG_UY_XANH_LO_BDKB(dame, hp));`.
  * **Các file cập nhật**:
    - [BossesData.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/BossesData.java)
    - [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java)
  * **Biên dịch**: `javac` JDK 21 đã biên dịch thành công 100% tất cả các file vào `build/classes`.

* **[Loại Bỏ Popup Thông Báo Vàng "Cái hang này sắp sập rồi...", Giữ Widget "Hang sắp sập: 60s" Gọn Đẹp Chuẩn Doanh Trại]**:
  * Đã xóa bỏ hàm `notifyCollapse()` và toàn bộ lệnh gọi `Service.gI().sendThongBao(pl, "Cái hang này sắp sập rồi...")` trong [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java).
  * Thời gian sập hang đếm lùi 60 giây hiển thị hoàn toàn gọn đẹp và chuyên nghiệp qua duy nhất widget `"Hang sắp sập: 60s"` ở góc màn hình y hệt như widget `"Thu thập mảnh: 5'"` của Doanh Trại, không che khuất tầm nhìn người chơi bằng popup chữ vàng.
  * **Biên dịch**: `javac` JDK 21 đã biên dịch thành công 100% tất cả các file vào `build/classes`.


* **[Đồng Bộ Kiến Trúc Đếm Ngược Sập Hang BDKB Giống 100% Mẫu Doanh Trại]**:
  * **1. Thiết kế cơ chế chuyển pha đếm ngược thời gian chuẩn Doanh Trại (`sendTextDoanhTrai` $\rightarrow$ `sendTextTimePickDoanhTrai`)**:
    - **Pha 1 (Thám hiểm hang)**:
      + Widget hiển thị `"Hang kho báu:"` (30 phút đếm lùi) qua `sendTextBanDoKhoBau(player)`.
    - **Pha 2 (Hang sập - 60 giây)**:
      + Khi đã tiêu diệt sạch toàn bộ mob và Boss Trung Úy Xanh Lơ (`allCleared = allMobsDead && isBossKilled`) hoặc khi phó bản còn 60s cuối cùng:
      + `triggerKickout()` kích hoạt: `kickout = true`, `timeKickOut = System.currentTimeMillis()`.
      + Gọi ngay `sendTextTime()`: Widget tự động chuyển mượt mà sang `"Hang sắp sập: 60s"` qua `sendTextTimeSapBanDoKhoBau(player)`.
      + Đồng thời `notifyCollapse()` gửi popup thông báo chữ vàng: *"Cái hang này sắp sập rồi, chúng ta phải rời khỏi đây ngay 60s nữa"* (lặp lại định kỳ mỗi 10 giây và mỗi giây khi $\le 5$s).
    - **Chống nhảy chữ tuyệt đối**:
      + Trong `sendTextBanDoKhoBau`: Nếu `isKickout() == true` thì `return` ngay, không bao giờ gửi lại `"Hang kho báu:"` đè lên `"Hang sắp sập:"`.
      + Trong `sendAllItemTime` và `sendTextTime`: Phân nhánh `if (bdkb.isKickout()) sendTextTimeSapBanDoKhoBau else sendTextBanDoKhoBau`, hoàn toàn giống hệt Doanh Trại `if (dt.isTimePicking) sendTextTimePickDoanhTrai else sendTextDoanhTrai`.
  * **Các file cập nhật**:
    - [ItemTimeService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/ItemTimeService.java)
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
  * **Biên dịch**: `javac` JDK 21 biên dịch thành công 100% tất cả các file vào `build/classes`.


* **[Sửa Chuẩn Xác 4 Điểm BDKB Theo Yêu Cầu & Gốc Teamobi2026]**:
  * **1. Khôi phục text widget "Hang kho báu:" khi vào map**:
    - Trong `Controller.java`: Ngay khi tàu đưa player vào Map 135, gọi ngay `ItemTimeService.gI().sendTextBanDoKhoBau(player)`.
    - Trong `ChangeMapService.java` (`checkJoinSpecialMap`): Tự động gửi packet `sendTextBanDoKhoBau(player)` mỗi khi người chơi chuyển giữa các Map 135, 136, 137, 138.
    - Trong `BanDoKhoBau.java`: Duyệt `this.zones` và các player trong zone để gọi `sendTextBanDoKhoBau(pl)` định kỳ mỗi 15 giây trong `update()`, đảm bảo widget luôn hiển thị ổn định 30 phút trên màn hình.
  * **2. Đưa Boss Trung Úy Xanh Lơ về đúng Map 137 (Hang kho báu)**:
    - Sửa điều kiện sinh Boss: `if (zone.map.mapId == 137)` (Hang kho báu - nơi có rương kho báu). Map 138 (Đáy hang kho báu) chỉ dành riêng cho Rôbốt bảo vệ (Mob 72).
  * **3. Cơ chế sập hang: Bắt buộc tiêu diệt TOÀN BỘ MOB VÀ BOSS mới kích hoạt đếm ngược 60s**:
    - Trong `BanDoKhoBau.update()`: Duyệt kiểm tra toàn bộ quái trong 4 map (`allMobsDead`), kết hợp với cờ `isBossKilled` (được bật khi người chơi đánh chết Boss Trung Úy Xanh Lơ).
    - Chỉ khi `allMobsDead && isBossKilled` thì phó bản mới bắt đầu đếm ngược 60s và phát thông báo popup. Đánh chết boss mà quái chưa hết thì không kích hoạt đếm ngược.
  * **4. Khôi phục 100% code gốc của Rôbốt Bảo Vệ từ Teamobi2026**:
    - Ghi đè file `RobotBaoVe.java` bằng nguyên bản code từ `Teamobi2026/SRC/src/nro/models/mob_bigboss/RobotBaoVe.java` (đầy đủ các action 0, 1, 2 và bám mục tiêu theo player).
  * **Các file cập nhật**:
    - [RobotBaoVe.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob_bigboss/RobotBaoVe.java)
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
    - [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java)
    - [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java)
    - [Controller.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/server/Controller.java)
    - [ItemTimeService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/ItemTimeService.java)
  * **Kiểm tra biên dịch**: Tất cả các file `.java` đã được `javac` biên dịch thành công 100% vào `build/classes`.


* **[Đồng Bộ Chuẩn Gốc Teamobi2026: Giữ Widget "Hang kho báu:" 30 Phút Ổn Định & Thông Báo Đếm Ngược Sập Hang]**:
  * **1. Khôi phục widget ItemTime "Hang kho báu:" đếm lùi 30 phút ổn định như bản gốc Teamobi2026**:
    - **Nguyên nhân gây nhảy chữ**: Trước đây server gửi packet 65 với ID `BAN_DO_KHO_BAU = 1` lúc thì gửi `"Hang kho báu:"`, lúc lại ghi đè `"Hang sắp sập:"` mỗi khi chuyển map hoặc nhịp update, khiến Client Unity 2D bị giật và nhảy chữ lung tung.
    - **Khắc phục**:
      + Trong `ItemTimeService.java`: `sendAllItemTime` chỉ gọi duy nhất `sendTextBanDoKhoBau(player)`.
      + `sendTextBanDoKhoBau(player)`: Loại bỏ điều kiện `|| bdkb.isKickout()`, giữ widget hiển thị ổn định xuyên suốt 30 phút từ lúc vào hang cho đến khi hang kết thúc.
      + Vô hiệu hóa việc can thiệp ghi đè của `sendTextTimeSapBanDoKhoBau` lên widget này.
  * **2. Bổ sung cơ chế thông báo đếm ngược thời gian sập hang chuẩn Teamobi2026**:
    - Khi Boss Trung Úy Xanh Lơ bị tiêu diệt (`die(plKill)`) hoặc khi phó bản còn 60 giây cuối cùng: kích hoạt `triggerKickout()`.
    - Phát thông báo popup hệ thống `Service.gI().sendThongBao`:
      `"Cái hang này sắp sập rồi, chúng ta phải rời khỏi đây ngay " + secondsLeft + "s nữa"`
      định kỳ mỗi 10 giây (và đếm lùi từng giây khi còn $\le 5$s) trực tiếp trên màn hình của tất cả người chơi trong các tầng hang, không làm ảnh hưởng hay xáo trộn widget góc trái.
  * **3. Đặt Boss Trung Úy Xanh Lơ chuẩn xác tại Map 138 (Đáy hang kho báu)**:
    - Trong `BanDoKhoBau.java`: Kiểm tra đúng `if (zone.map.mapId == 138)` để sinh Trung Úy Xanh Lơ tại bệ chỉ huy Map 138, hoàn toàn khớp với giao diện game của dự án.
  * **Các file cập nhật**:
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
    - [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java)
    - [ItemTimeService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/ItemTimeService.java)
  * **Kiểm tra biên dịch**: Chạy `compile_check.bat`, trả về `[COMPILE_SUCCESS] Da bien dich va dong goi thanh cong NroBaKhi.jar!`.


* **[Sửa Triệt Để Lỗi "Chưa Gì Đã Sập" & "RobotBaoVe Nhảy Lên Đầu Map Đẩy Người Chơi Qua Map Trước"]**:
  * **1. Khắc phục lỗi "Vừa mở BDKB đã sập hang ngay" (`Hang sắp sập: 40s`, `15s`, `2s`)**:
    - **Phát hiện nguyên nhân gốc rễ (Lõi vòng đời Boss)**:
      + Khi tạo `new TrungUyXanhLo(...)`, boss ở trạng thái `BossStatus.REST`. Lúc này `initBase()` chưa được gọi, thuộc tính `nPoint.hp` của thực thể boss ban đầu bằng `0`.
      + Hàm `Player.isDie()` kiểm tra `if (this.nPoint != null && this.nPoint.hp <= 0) return true;`. Do đó `this.boss.isDie()` ngay lập tức trả về `true` ngay từ tick 0.15s đầu tiên của phòng!
      + Khi đó lệnh `if (this.boss != null && this.boss.isDie()) this.isBossKilled = true;` lập tức gán `isBossKilled = true` $\rightarrow$ `kickout = true` $\rightarrow$ hang bị sập ngay từ đầu!
    - **Giải pháp dứt điểm**:
      + Loại bỏ hoàn toàn việc kiểm tra `this.boss.isDie()` trong `BanDoKhoBau.update()`.
      + Truyền tham chiếu `BanDoKhoBau` trực tiếp vào constructor `TrungUyXanhLo`.
      + Cờ `isBossKilled = true` CHỈ ĐƯỢC PHÉP BẬT bên trong phương thức `TrungUyXanhLo.die(Player plKill)`. Khi người chơi đánh boss cạn máu thì boss mới kích hoạt cờ sập hang.
      + Loại bỏ điều kiện kiểm tra `allCharactersDead` trong `BanDoKhoBau.update()` để hang chỉ đếm ngược sập khi Boss thực sự bị hạ gục hoặc khi hết 29 phút.
  * **2. Khắc phục lỗi "RobotBaoVe nhảy lên đầu map khi tàn sát làm người chơi bị đẩy qua map trước"**:
    - **Phát hiện nguyên nhân gốc rễ (Từ mã nguồn Client `BigBoss2.cs`)**:
      + Đã tra cứu trực tiếp file mã nguồn Client `Mod-nro-unity/Assets/Scripts/BigBoss2.cs` (`updateMobAttack`):
        * Khi `type == 0` (đấm cận chiến): Client thực thi `x += (charAttack[0].cx - x) / 4; xTo = x;` $\rightarrow$ kéo Robot bay lướt áp sát người chơi.
        * Khi `type == 2` (húc): Client CŨNG thực thi `x += (charAttack[0].cx - x) / 4; xTo = x;` $\rightarrow$ kéo Robot bay lướt áp sát người chơi.
        * **CHỈ DUY NHẤT `type == 1`**: Client chỉ gọi `MonsterDart.addMonsterDart` phóng đạn từ xa, hoàn toàn không có lệnh thay đổi tọa độ `x` hay `xTo`.
      + Ở turn trước code để `action = (dis <= 150) ? 1 : 2;` nên khi người chơi ở xa, Robot tung `action = 2` khiến Client kéo Robot bay theo người chơi lên đầu map!
    - **Giải pháp dứt điểm**:
      + Trong `RobotBaoVe.java`: Khóa cứng duy nhất `action = 1`. Robot Bảo Vệ sẽ đứng yên 100% tại bệ $X = 492, Y = 336$ và chỉ bắn đạn `MonsterDart` từ xa, không bao giờ di chuyển hay lướt dù chỉ 1 pixel.
      + Bổ sung cờ `isSentDie` trong `RobotBaoVe.update()` để chỉ gửi gói tin chết (`action = 6`) đúng 1 lần duy nhất, chống spam gói tin làm lag client.
  * **Các file cập nhật**:
    - [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/ban_do_kho_bau/TrungUyXanhLo.java)
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
    - [RobotBaoVe.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob_bigboss/RobotBaoVe.java)
    - [AbstractClanDungeon.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/AbstractClanDungeon.java)
  * **Kiểm tra biên dịch**: Đã biên dịch và đóng gói thành công 100% vào `NroBaKhi.jar` (`[COMPILE_SUCCESS]`).

* **[Triệt Tiêu Hành Vi Nhảy Lên Đầu Map Của Rôbốt Bảo Vệ BDKB]**:
  * **Thực hiện Giải pháp 1 (Stationary Defense BigMob)**:
    - Xóa bỏ hoàn toàn dòng lệnh ép tọa độ `this.location.x = (short) player.location.x;` trong `RobotBaoVe.java`.
    - Giữ cố định Rôbốt Bảo Vệ tại bệ canh gác trung tâm ($X = 492, Y = 336$).
    - Bổ sung phạm vi giao chiến an toàn `dis <= 300`:
      + Khi người chơi ở xa ($dis > 300$, đứng ở cổng vào $X = 60$), Rôbốt hoàn toàn không tấn công hay dịch chuyển, người chơi an toàn tuyệt đối khi vừa vào map.
      + Khi người chơi tiến vào trong cự ly $\le 300$px, Rôbốt tung chiêu tương ứng: đấm cận chiến ($dis \le 100$), bắn laser ($dis \le 200$) hoặc bắn hỏa tiễn ($dis \le 300$).
  * **File cập nhật**:
    - [RobotBaoVe.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob_bigboss/RobotBaoVe.java)
  * **Kiểm tra biên dịch**: `compile_check.bat` trả về `[COMPILE_SUCCESS] Da bien dich va dong goi thanh cong NroBaKhi.jar!`.

* **[Khắc Phục Lỗi Mới Mở BDKB Chỉ Còn 4-5 Phút (Lệch Session Time)]**:
  * **Phát hiện nguyên nhân gốc rễ**:
    - Khi triển khai cơ chế "Reset 24h tính từ lần mở đầu tiên", trường `clan.lastTimeOpenBanDoKhoBau` được giữ nguyên làm mốc 24h của chu kỳ và không cập nhật lại ở các lần mở 2, 3.
    - Tuy nhiên, trong `BanDoKhoBau.startRoom` lại gán `this.lastTimeOpen = clan.lastTimeOpenBanDoKhoBau;` và `ItemTimeService` cũng lấy mốc này trừ đi.
    - Hậu quả: Khi người chơi mở lần thứ 2 cách lần đầu 26 phút, hệ thống lấy 30 phút trừ đi 26 phút đã trôi qua $\rightarrow$ phòng mới mở chỉ còn đúng $30 - 26 = 4$ phút (`Hang kho báu: 4'`), thậm chí lần 3 sau 30 phút sẽ bị hết giờ ngay khi vừa vào!
  * **Giải pháp khắc phục toàn diện**:
    - Tách bạch dứt điểm giữa **Mốc chu kỳ 24h** (`lastTimeOpenBanDoKhoBau`) và **Thời điểm mở phòng của phiên hiện tại** (`this.lastTimeOpen` / `clan.timeOpenBanDoKhoBau`):
      + `BanDoKhoBau.startRoom`: `this.lastTimeOpen = System.currentTimeMillis();` (mỗi phòng mới mở LUÔN LUÔN được chơi đủ 30 phút = 1800s).
      + `ItemTimeService.sendTextBanDoKhoBau`: Tính `secondPassed` dựa trên `bdkb.getLastTimeOpen()` $\rightarrow$ luôn hiển thị đủ 30 phút khi mới mở.
      + `TreasureUnderSeaService.updateThanhTich`: Kỷ lục thời gian tính theo `now - bdkb.getLastTimeOpen()`.
      + `ChangeMapService.goToDBKB` & `QuyLaoKame`: Bổ sung theo dõi phiên `player.lastTimeOpenBDKBSession` để không trừ trùng lượt khi người chơi chết hồi sinh vào lại cùng phiên, đồng thời ghi nhận chính xác lượt mở của phiên mới.
  * **Các file cập nhật**:
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
    - [ItemTimeService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/ItemTimeService.java)
    - [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java)
    - [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java)
    - [QuyLaoKame.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/QuyLaoKame.java)
    - [Player.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/player/Player.java)
  * **Kiểm tra biên dịch**: `compile_check.bat` trả về `[COMPILE_SUCCESS] Da bien dich va dong goi thanh cong NroBaKhi.jar!`.

* **[Đồng Bộ Mẫu Thiết Kế 2 Hàm ItemTime BDKB Giống Doanh Trại]**:
  * **Tách bạch 2 hàm hiển thị thời gian BDKB theo kiến trúc Doanh Trại (`sendTextDoanhTrai` & `sendTextTimePickDoanhTrai`)**:
    - `sendTextBanDoKhoBau(Player player)`: Chuyên trách hiển thị thời gian thám hiểm hang kho báu thông thường (`"Hang kho báu:"`, tối đa 30 phút).
    - `sendTextTimeSapBanDoKhoBau(Player player)`: Chuyên trách hiển thị thời gian đếm ngược 60 giây sập hang (`"Hang sắp sập:"`, đếm lùi từng giây).
  * **Tích hợp tại các điểm gọi**:
    - `sendAllItemTime`: Điều phối rẽ nhánh rõ ràng:
      ```java
      if (player.clan != null && player.clan.BanDoKhoBau != null && player.clan.BanDoKhoBau.isKickout()) {
          ItemTimeService.gI().sendTextTimeSapBanDoKhoBau(player);
      } else {
          ItemTimeService.gI().sendTextBanDoKhoBau(player);
      }
      ```
    - `BanDoKhoBau.sendKickoutTime`: Chỉ việc gọi `ItemTimeService.gI().sendTextTimeSapBanDoKhoBau(pl);`, đóng gói trọn vẹn logic tạo/gửi packet trong tầng Service.
  * **Các file cập nhật**:
    - [ItemTimeService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/ItemTimeService.java)
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
  * **Kiểm tra biên dịch**: `javac` đã biên dịch thành công 100% tất cả các class vào `build/classes`.

* **[Khắc Phục Lỗi Đồng Bộ Thời Gian Hang Kho Báu & Chốt Chặn Hoàn Thành BDKB]**:
  * **Xử lý dứt điểm tình trạng "đi xong rồi sao không giảm đúng số giây còn lại"**:
    - **Phát hiện nguyên nhân 1 (Chốt chặn hoàn thành)**: Trước đây `BanDoKhoBau` yêu cầu `allCharactersDead` (toàn bộ quái ở cả 4 tầng 135-138 + Boss đều phải chết). Người chơi thường hạ gục Boss Trung Úy Xanh Lơ ở Map 138 rồi quay lại Map 137 nhặt rương kho báu, nếu còn sót quái ở tầng 135/136 thì hang không bao giờ kích hoạt sập hang (`kickout`), dẫn đến đồng hồ vẫn treo 28-29 phút.
    - Đã bổ sung `isBossDead()`: Ngay khi Boss Trung Úy Xanh Lơ bị tiêu diệt hoặc toàn bộ Boss/quái chết, phó bản lập tức ghi nhận hoàn thành và kích hoạt đếm ngược 60 giây sập hang (`kickout = true; timeKickOut = System.currentTimeMillis();`).
    - **Phát hiện nguyên nhân 2 (Lệch giao thức ItemTime - Packet 65)**: Khi `kickout` diễn ra, server chỉ gửi thông báo vàng popup mà không gửi lại packet 65 cho client. Khi người chơi chuyển map về 137, `sendTextBanDoKhoBau` lại tính theo 30 phút ban đầu và ghi đè `28'` lên màn hình.
    - Đã nâng cấp `ItemTimeService.sendTextBanDoKhoBau`:
      + Tự động dọn sạch widget (`removeTextBanDoKhoBau`) khi người chơi ra khỏi map BDKB hoặc khi hang đã đóng.
      + Khi hang đang trong 60 giây sập hang (`bdkb.isKickout()`), tự động chuyển tiêu đề và số giây đếm ngược: `"Hang sắp sập: 60s"`, `59s`... hiển thị đếm lùi từng giây chính xác trên màn hình.
    - Đã bổ sung `sendKickoutTime()` trong `BanDoKhoBau.java` cập nhật tức thì cho toàn bộ người chơi trong hang ngay thời điểm hoàn thành và định kỳ mỗi 10 giây.
  * **Các file cập nhật**:
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
    - [ItemTimeService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/ItemTimeService.java)
  * **Kiểm tra biên dịch**: `javac` đã biên dịch thành công 100% tất cả các class vào `build/classes` không có bất kỳ lỗi nào.

* **[Tái Cấu Trúc Chuẩn Hóa Vai Trò Service vs Entity BDKB]**:
  * **Chuyển `updateThanhTich` về đúng Tầng Service (`TreasureUnderSeaService`)**:
    - Tách logic tính toán kỷ lục thời gian và ghi xuống MySQL của Bang hội ra khỏi `BanDoKhoBau.java`.
    - `TreasureUnderSeaService` hiện đảm nhiệm hàm `updateThanhTich(Player player, BanDoKhoBau banDoKhoBau)`.
    - `BanDoKhoBau.onPlayerFinish` chỉ kích hoạt sự kiện và ủy quyền lại cho Service xử lý Database.
  * **Tách bạch khởi động phòng (`startRoom`) và chuyển map**:
    - `BanDoKhoBau` hiện chỉ đảm nhiệm hàm `startRoom(Clan clan, byte level)` quản lý nội tại của phòng (reset cờ, quái, boss, virtual thread).
    - `TreasureUnderSeaService` trực tiếp điều phối chuyển map cho người chơi sau khi phòng sẵn sàng (`ChangeMapService.gI().goToDBKB(player)`).
  * **Đảm bảo tính tương thích ngược**:
    - Giữ lại các alias cũ `@Deprecated` để tránh ảnh hưởng đến các module khác nếu có gọi tới.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, trả về `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công 100%.

* **[Cập Nhật Giới Hạn Cấp 100, Reset Chu Kỳ 24h & Khóa Bang Hội BDKB]**:
  * **Reset đúng 24h từ bản đồ đầu tiên**:
    - Thay thế cơ chế reset nửa đêm (`isAfterMidnight`) bằng cơ chế đếm ngược 24 tiếng (`TIME_RESET_BDKB = 86400000L`).
    - Lần đầu tiên tham gia/mở (`timesPerDay == 0`) sẽ chốt mốc thời gian bắt đầu chu kỳ (`lastTimeJoinBDKB` / `lastTimeOpenBanDoKhoBau`).
    - Các lần 2, 3 đi trong vòng 24h chỉ tăng biến đếm mà giữ nguyên mốc thời gian ban đầu để đảm bảo reset chính xác sau 24h tính từ lần đầu tiên.
  * **Điều chỉnh cấp độ từ 1–110 thành 1–100**:
    - `BanDoKhoBau.MAX_LEVEL = 100`.
    - Scale Hardcore: `scale = level / 100.0` (Cấp 100 đạt trần 2 tỷ HP, 20 triệu Dame cho Boss và Big Mob).
    - Cập nhật Form nhập cấp độ và giới hạn kiểm tra trong [Input.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services_func/Input.java) và [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java).
  * **Khóa Bang Hội & Chống Hardcode**:
    - Thêm `BanDoKhoBau.MAX_TIMES_PER_DAY = 3;`.
    - Mở rộng cột `thanhTichBDKB` trong bảng `clan` lưu đủ `[levelDone, thoiGian, timesPerDayBDKB, lastTimeOpenBanDoKhoBau]` và cập nhật tức thì xuống MySQL qua `clan.updateBDKB()`.
  * **Kiểm tra biên dịch**: `javac` đã biên dịch thành công 100% tất cả các class vào `build/classes`.

* **[Hoàn Thiện Ràng Buộc & Khắc Phục Lỗi Giới Hạn 3 Lần/Ngày BDKB]**:
  * **Giải quyết thắc mắc & lỗi Bang chủ đi 3 lần**:
    - Trước đây, nhánh mở hang mới trong `QuyLaoKame.java` chưa kiểm tra `timesPerDayBDKB >= 3`, khiến Bang chủ đã đi hết lượt vẫn mở được Form chọn cấp độ (1-110).
    - Đã bổ sung kiểm tra chặn ngay tại NPC Quy Lão Kame trước khi mở form nhập cấp độ: Nếu `player.timesPerDayBDKB >= 3` (chưa qua nửa đêm), NPC sẽ báo ngay *"Con đã vào hang kho báu 3 lần trong hôm nay rồi, hẹn gặp lại ngày mai!"*.
  * **Khắc phục Bug cộng đúp lượt đi (`timesPerDayBDKB++`)**:
    - Khi Bang chủ mở hang, `TreasureUnderSeaService` đã tăng lượt, nhưng thời gian `player.lastTimeJoinBDKB` và `clan.lastTimeOpenBanDoKhoBau` bị lệch vài mili giây dẫn đến việc `ChangeMapService.goToDBKB()` tiếp tục tăng thêm lượt thứ 2.
    - Đã đồng bộ chuẩn xác `lastTimeOpen` giữa `TreasureUnderSeaService`, `BanDoKhoBau` và `ChangeMapService`, loại bỏ triệt để hiện tượng bị tính 2 lượt sau 1 lần mở.
  * **Thắt chặt an toàn đa tầng**:
    - Thêm kiểm tra sức mạnh $\ge 2$ tỷ (`POWER_CAN_GO_TO_DBKB`) trực tiếp tại cả `TreasureUnderSeaService.openBanDoKhoBau` và `ChangeMapService.goToDBKB` để chống hack packet trực tiếp.
  * **Các file cập nhật**:
    - [QuyLaoKame.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/QuyLaoKame.java)
    - [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java)
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java)
    - [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java)
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, trả về `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công 100%.

* **[Tối Giản Hóa Xử Lý Menu Bản Đồ Kho Báu Trong QuyLaoKame.java]**:
  * Đã gộp 2 hàm trùng lặp `handleMenuOpenDBKB` và `handleMenuOpenedDBKB` thành 1 hàm duy nhất `handleMenuTreasureMap(Player player, int select)`.
  * `confirmMenu`: Gộp 2 nhánh `case ConstNpc.MENU_OPENED_DBKB, ConstNpc.MENU_OPEN_DBKB:` cùng trỏ về `handleMenuTreasureMap`.
  * Tái cấu trúc chuẩn DRY, sửa lỗi hiển thị Thành tích bang (`showMyTopClanBDKB`) và tập trung logic kiểm tra Bang chủ / Sức mạnh / Trạng thái hang vào 1 nơi duy nhất.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, trả về `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công 100%.

* **[Thiết Lập Chế Độ Bản Đồ Kho Báu Hardcore & Thắt Chặt Điều Kiện Mở/Tham Gia]**:
  * **Yêu cầu & Điều kiện mở/tham gia**:
    - **Chỉ cho Bang Chủ** (`player.clan.isLeader(player)`) mới được mở Bản Đồ Kho Báu (BDKB).
    - **Người mở (Bang Chủ)**: Bắt buộc sở hữu Bản Đồ Kho Báu (Item ID `611`) trong hành trang và bị trừ 1 vé khi mở hang thành công.
    - **Thành viên tham gia**: Khi bấm "Đồng ý" vào hang cùng bang hội tại NPC Quy Lão Kame (`handleMenuOpenedDBKB`), hệ thống kiểm tra số lần vào trong ngày (`timesPerDayBDKB < 3`) và dịch chuyển vào hang (`goToDBKB`) hoàn toàn miễn phí, không bắt buộc sở hữu hay trừ thêm Bản Đồ Kho Báu (chỉ Bang Chủ khi mở hang mới tốn 1 Bản đồ kho báu).
  * **Chỉ số quái & Boss Bản Đồ Kho Báu Hardcore theo Cấp Độ 1-110 (`scale = level / 110.0`)**:
    - **Boss (Trung Úy Xanh Lơ - Map 138) & Big Mob (Vua Bạch Tuộc Mob 71 - Map 136, Rôbốt bảo vệ Mob 72 - Map 138)**:
      * HP: Kịch trần `2.000.000.000` (2 tỷ HP) tại Cấp 110.
      * Dame: Max `20.000.000` (20 triệu Dame) tại Cấp 110.
    - **Siêu Mob** (Vị trí đặc biệt trong Map 135, 136, 137):
      * HP: Max `1.000.000.000` (1 tỷ HP) tại Cấp 110.
      * Dame: Max `10.000.000` (10 triệu Dame) tại Cấp 110.
    - **Mob Thường** (Toàn bộ quái còn lại trong 4 map):
      * HP: Max `500.000.000` (500 triệu HP) tại Cấp 110.
      * Dame: Max `5.000.000` (5 triệu Dame) tại Cấp 110.
    - **Bẫy (TrapMap)**: Sát thương tăng tiến theo cấp độ, tối đa `10.000.000` Dame tại Cấp 110.
  * **Các file cập nhật**:
    - [QuyLaoKame.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/QuyLaoKame.java): Thắt chặt kiểm tra Bang chủ khi mở, kiểm tra & trừ Item 611 của thành viên tham gia.
    - [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java): Chặn tài khoản không phải Bang chủ gọi `openBanDoKhoBau`.
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java): Cập nhật hàm `init()` thiết lập chỉ số HP, Dame chuẩn xác theo tỉ lệ `level / 110.0` đạt đỉnh Hardcore ở cấp 110 cho Boss, Big Mob, Siêu Mob, Mob thường và TrapMap.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, trả về `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công 100%.

* **[Kiểm Tra & Tối Ưu Toàn Diện Hiệu Năng, An Toàn & Chuẩn Hóa Logic Toàn Bộ Boss Doanh Trại]**:
  * **Phạm vi kiểm tra**: 6 Boss trong package `nro.models.boss.doanh_trai`:
    - `TrungUyTrang.java`:
      * Giữ nguyên `super.reward(plKill);` nguyên bản của Trung Úy Trắng (phục vụ nhiệm vụ/thành tựu đặc thù phó bản).
      * Phòng ngừa lỗi NPE trong `injured`: bổ sung kiểm tra an toàn `this.zone != null` trước khi truy cập `isbulon1Alive` / `isbulon2Alive`.
      * Khắc phục tình trạng đứng im đơ mục tiêu (Target Freeze): khi người chơi được nhắm nằm ngoài phạm vi giao chiến (`location.x < 755 || location.x > 1060`), tự động reset `this.playerTarger = null` để Boss chuyển mục tiêu ngay sang người chơi đang đứng trong phòng đánh Boss.
      * Xóa bỏ khối `catch (Exception ex) {}` rỗng nuốt lỗi (vi phạm Rule 9), thay bằng ghi nhận log `Logger.logException`. Dọn sạch toàn bộ import dư thừa.
    - `TrungUyThep.java`, `TrungUyXanhLo.java`, `RobotVeSi.java`, `NinjaAoTim.java`, `NinjaClone.java`:
      * Giữ nguyên cơ chế gốc **KHÔNG gọi `super.reward(plKill)`** như thiết kế chuẩn NRO (chỉ có Trung Úy Trắng mới có `super.reward`).
      * Khắc phục đứng im đơ mục tiêu khi người chơi ngoài vùng giao chiến bằng cách reset `this.playerTarger = null`.
      * Thay khối catch rỗng bằng `Logger.logException`. Dọn dẹp import.
      * Tách biệt tọa độ xuất hiện cho 4 Rô bốt Vệ Sĩ theo ID (`220 + id * 50`).
      * Chuyển `clones` của Ninja Áo Tím sang `CopyOnWriteArrayList` chống xung đột đa luồng.
      * Dọn dẹp clone khỏi RAM ngay khi chết trong `leaveMap()`.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, đóng gói thành công `NroBaKhi.jar`.

* **[Cơ Chế Bẻ Lái Target Focus & Thuật Thế Thân Cho Ninja Áo Tím (Doanh Trại)]**:
  * **Giải đáp câu hỏi cơ chế**: Quá trình `respawn()` là phương thức nạp lại chỉ số trong RAM (0ms, không có thời gian chờ). Cơ chế bẻ lái mục tiêu của Client NRO 2D phụ thuộc vào gói tin `Message -6` (xóa thực thể khỏi map): khi client nhận `Message -6` của một nhân vật, client lập tức reset con trỏ mục tiêu `charFocus = null`.
  * **Cập nhật trong [NinjaAoTim.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaAoTim.java)**:
    - Khi máu $\le 50\%$ HP và dùng "Phân thân chi thuật":
      * Boss thật hồi full 100% HP.
      * Boss thật phát gói tin `Message -6` (`sendExitMap(this)`) khiến toàn bộ người chơi trong map bị hủy target (`charFocus = null`).
      * Clone 0 xuất hiện ngay đúng tọa độ cũ của Boss thật để người chơi khi đánh sẽ tự động bắt mục tiêu (auto-target) vào Clone.
      * Các Clone 1..N xuất hiện rải rác xung quanh.
      * Boss thật dịch chuyển sang vị trí ngẫu nhiên cách đó 120-220px và hiện hình trở lại (`load_Me_To_Another`).
    - Trong khi giao tranh (Thuật Thế Thân):
      * Mỗi đòn đánh của người chơi vào Boss thật có 50% tỉ lệ kích hoạt "Đó chỉ là tàn ảnh thôi!".
      * Boss thật gửi `Message -6` hủy focus, tráo đổi tọa độ với 1 Ninja Clone còn sống.
      * Sát thương từ đòn đánh của người chơi được chuyển hướng sang con Clone đó (`aliveClone.injured(...)`).
      * Khi người chơi tiếp tục đánh, họ sẽ đánh vào con Clone đang thế thân ngay trước mặt.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Sửa Lỗi Phân Thân Ninja Áo Tím Chết Ngay Khi Sinh Ra & Chuẩn Hóa Hồi Full HP Cho Boss Thật / HP Dame Cho Clone]**:
  * **Nguyên nhân gốc rễ**: Khi Ninja Áo Tím gọi phân thân (`new NinjaClone(...)`), code cũ gọi trực tiếp `clone.joinMap()` mà bỏ qua chu trình `clone.respawn()`. Dẫn đến `clone.initBase()` không được gọi, `clone.nPoint.hpMax` và `clone.nPoint.hp` bằng 0 $\rightarrow$ `clone.isDie() == true` ngay từ lúc xuất hiện, biến thành hồn ma lơ lửng có vòng sáng trên đầu (như trong ảnh chụp).
  * **Cập nhật trong [NinjaClone.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaClone.java)**:
    - Trong constructor: Bổ sung gọi `this.respawn();` để nạp dữ liệu `initBase()` (gán HP, Dame, kỹ năng, tính toán `calPoint()` chính xác trước khi vào map).
    - Trong `joinMap()`: Đặt tọa độ rơi an toàn trên mặt đất trong giới hạn map, chuyển thẳng sang trạng thái PK và `BossStatus.ACTIVE` để có thể giao tranh ngay.
  * **Cập nhật trong [NinjaAoTim.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaAoTim.java)**:
    - Khi máu $\le 50\%$ HP và kích hoạt "Phân thân chi thuật": Ninja Áo Tím thật được **hồi phục 100% full HP** (`this.nPoint.hp = this.nPoint.hpMax`) và đồng bộ lên thanh máu client (`sendInfoHp`).
    - Các Ninja Clone phân thân được sinh ra với **HP và Dame giống hệt Boss thật** (`hpClone = this.nPoint.hpMax`, `dameClone = this.nPoint.dame`).
    - Khi Ninja Áo Tím thật bị tiêu diệt (`die()` / `leaveMap()`), tự động dọn dẹp biến mất toàn bộ phân thân còn sống để không làm nghẽn tiến độ phó bản.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Sửa Lỗi Quái/Boss Bị Tụt Còn 40 HP & 600 HP Khi Thành Viên Ra Vào Doanh Trại]**:
  * **Nguyên nhân gốc rễ**: Tại [RedRibbonHQService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQService.java#L73-L78), khi thành viên tiếp viện hoặc chết quay lại bấm "Tham gia", code cũ gọi `pl.clan.doanhTrai.updateHPDame();` trước khi chuyển người chơi vào map. Tại thời điểm đó trong các map Doanh Trại chỉ còn lại Acc phụ (Dame = 10, HP = 400), dẫn đến công thức tính lại chỉ số theo đúng 1 acc phụ: Quái tụt còn `10 * 4 = 40 HP`, Boss map 59 tụt còn `10 * 60 * 1 = 600 HP`.
  * **Khắc phục**: Loại bỏ dòng gọi `updateHPDame()` khi tiếp viện trong `RedRibbonHQService.java`. Đúng chuẩn NRO, chỉ số Quái và Boss được chốt cố định suốt 30 phút theo đội hình ban đầu lúc mở, không bị giảm sút hay reset máu khi thành viên ra vào.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Sửa Lỗi Hiển Thị Đè Đồng Hồ Doanh Trại & Tách Biệt Đếm Ngược 5 Phút Nhặt Mảnh]**:
  * **Cập nhật trong [ItemTimeService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/ItemTimeService.java)**:
    - Trong `sendTextDoanhTrai(Player player)`: Thêm điều kiện `if (player.clan.doanhTrai != null && player.clan.doanhTrai.isTimePicking) return;` để không bị gửi đè đồng hồ 25-30 phút cũ khi Doanh Trại đã vào giai đoạn 5 phút nhặt mảnh.
    - Trong `sendTextTimePickDoanhTrai(Player player)`: Đổi nhãn text từ `"Trại độc nhãn:"` sang `"Thu thập mảnh:"` để hiển thị rõ ràng giai đoạn 5 phút nhặt mảnh, tránh người chơi nhìn nhầm với thời gian Doanh Trại gốc.
    - Trong `sendAllItemTime(Player player)`: Phân nhánh rõ ràng, nếu `isTimePicking == true` thì gửi `sendTextTimePickDoanhTrai`, ngược lại mới gửi `sendTextDoanhTrai`.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Đổi Thưởng Kết Thúc Doanh Trại: Rải Ngẫu Nhiên 7 Mảnh Sưu Tầm Đơn Lẻ Rải Rác Khắp Map (Không Dồn Cọc, SL=1 Như Ngọc Rồng)]**:
  * **Cập nhật trong [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java#L233-L300)**:
    - Loại bỏ hoàn toàn cơ chế rơi dồn theo cọc nhiều món. Mỗi món rơi ra đất là đúng **1 mảnh đơn lẻ** (`quantity = 1`) như 1 viên Ngọc Rồng.
    - Hàm `dropSingleCardFragment(tempId)` chọn ngẫu nhiên một Map bất kỳ trong Doanh Trại (`zones`) và thả 1 mảnh xuống đất (`(x, y)`).
    - Cấu hình chuẩn xác tổng số lượng rơi ngẫu nhiên trên toàn Doanh Trại:
      * Mảnh Lính Độc Nhãn (`ConstItem.MANH_LINH_DOC_NHAN` - 837): Rơi ngẫu nhiên **2 – 5 mảnh**.
      * Mảnh Lính Độc Nhãn 2 (`ConstItem.MANH_LINH_DOC_NHAN_838` - 838): Rơi ngẫu nhiên **2 – 5 mảnh**.
      * Mảnh Sói Xám (`ConstItem.MANH_SOI_XAM` - 839): Rơi ngẫu nhiên **2 – 5 mảnh**.
      * Mảnh Trung Úy Trắng (`ConstItem.MANH_TRUNG_UY_TRANG` - 840): Rơi ngẫu nhiên **1 – 3 mảnh**.
      * Mảnh Trung Úy Xanh Lơ (`ConstItem.MANH_TRUNG_UY_XANH_LO` - 842): Rơi ngẫu nhiên **1 – 3 mảnh**.
      * Mảnh Ninja Áo Tím (`ConstItem.MANH_NINJA_AO_TIM` - 841): Rơi ngẫu nhiên **1 – 3 mảnh**.
      * Mảnh Độc Nhãn (`ConstItem.MANH_DOC_NHAN` - 859): Rơi đúng **1 mảnh**.
      * Bỏ hoàn toàn Đội Trưởng Vàng theo yêu cầu người dùng.
    - Giữ alias `randomNR()` để tương thích ngược.
  * **Cập nhật trong [DocNhan.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/DocNhan.java#L26-L36) & [LinhCanh.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/LinhCanh.java#L48-L52)**:
    - Gọi `player.clan.doanhTrai.randomCardFragments()`.
    - Sửa câu thoại NPC Độc Nhãn và thông báo hệ thống sang nội dung tìm kiếm Mảnh Sưu Tầm trong 5 phút.
    - Sửa câu thoại NPC Lính Canh ở cửa vào: "Bang hội của ngươi đã tiêu diệt trại độc nhãn và đang thu thập mảnh sưu tầm... Ngươi có muốn tham gia không?".
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Chuẩn Hóa Rơi Đồ Toàn Bộ Boss, DropItem & RewardService: Thay Thế Số Bằng ConstItem & Bổ Sung thanLinh()]**:
  * **Cập nhật trong [RewardService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/services/RewardService.java)**:
    - Bảng `ITEMS_BY_LEVEL` mảng Cấp 13 Thần Linh đã nạp toàn bộ hằng số `ConstItem.AO_THAN_LINH` .. `NHAN_THAN_LINH`.
    - Bổ sung helper `randItemDoThanLinh()` gọi trực tiếp `randItemDoCap(ConstItem.CAP_DO_THAN_LINH)`.
  * **Cập nhật trong [DropItem.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/player/DropItem.java)**:
    - Import `ConstItem`.
    - Thêm phương thức `.thanLinh()` trong cả 2 lớp `DropBuilder` và `PickOneBuilder` (`return equipment(ConstItem.CAP_DO_THAN_LINH);`), loại bỏ hoàn toàn magic number `13`.
  * **Cập nhật trong Toàn Bộ 19 Boss Hệ Thống**:
    - **[BlackGoku.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Black_Goku/BlackGoku.java)**: Dùng `.thanLinh()`, thay danh sách ngọc rồng bằng `ConstItem.NGOC_RONG_2_SAO` .. `7_SAO` và `ConstItem.NHAN_THOI_KHONG_SAI_LECH`.
    - **[Mabu2H.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/MajinBuu_14h/Mabu2H.java)**: Dùng `.thanLinh()`, thay danh sách ngọc rồng bằng `ConstItem.NGOC_RONG_2_SAO` .. `7_SAO`.
    - **Nhóm Nappa ([Kuku.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Nappa/Kuku.java), [MapDauDinh.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Nappa/MapDauDinh.java), [Rambo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Nappa/Rambo.java))**: Thay `18, 19, 20` bằng `ConstItem.NGOC_RONG_5_SAO`, `6_SAO`, `7_SAO`.
    - **Nhóm Tiểu Đội Sát Thủ ([SO1.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/tieu_doi_sat_thu/SO1.java) .. [SO4.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/tieu_doi_sat_thu/SO4.java), [TDT.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/tieu_doi_sat_thu/TDT.java))**: Thay `18, 19, 20` bằng `ConstItem.NGOC_RONG_5_SAO`, `6_SAO`, `7_SAO`.
    - **Nhóm Tiểu Đội Sát Thủ Namek ([SO1_NM.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/tieu_doi_sat_thu_namek/SO1_NM.java) .. [SO4_NM.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/tieu_doi_sat_thu_namek/SO4_NM.java), [TDT_NM.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/tieu_doi_sat_thu_namek/TDT_NM.java))**: Thay `861` bằng `ConstItem.HONG_NGOC` và `18, 19, 20` bằng `ConstItem.NGOC_RONG_5_SAO` .. `7_SAO`.
    - **[Fide.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Frieza/Fide.java)**: Thay `18, 19, 20` bằng `ConstItem.NGOC_RONG_5_SAO`, `6_SAO`, `7_SAO`.
    - **Nhóm Android ([Android13.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/Android13.java), [Android14.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/Android14.java), [Android15.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/Android15.java), [Android19.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/Android19.java), [DrKore.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/DrKore.java), [KingKong.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/KingKong.java), [Pic.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/Pic.java), [Poc.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/Android/Poc.java))**: Thay `18, 19, 20` bằng `ConstItem.NGOC_RONG_5_SAO`, `6_SAO`, `7_SAO`.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Chuẩn Hóa Mob.java: Thay Hardcode ID Bằng ConstItem & ConstMob Cho Quái Doanh Trại]**:
  * **Cập nhật trong [Mob.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob/Mob.java#L776-L790)**:
    - Thay thế các số hardcode magic number bằng hằng số tường minh:
      * `ConstMob.LINH_DOC_NHAN` (34) $\rightarrow$ `ConstItem.MANH_LINH_DOC_NHAN` (837)
      * `ConstMob.LINH_DOC_NHAN2` (35) $\rightarrow$ `ConstItem.MANH_LINH_DOC_NHAN_838` (838)
      * `ConstMob.SOI_XAM` (36) $\rightarrow$ `ConstItem.MANH_SOI_XAM` (839)
    - Tuân thủ nghiêm ngặt quy tắc Zero-Hardcode, giúp code tự giải thích và an toàn khi refactor.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Chuẩn Hóa RobotVeSi: Nhận Điểm Săn Boss & Rơi Ngọc Rồng 4s–7s]**:
  * **Cập nhật trong [RobotVeSi.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/RobotVeSi.java)**:
    - `reward(Player plKill)`: Thưởng 3 điểm sự kiện săn boss (`this.rewardEventPoint(plKill, 3)`), rơi ngẫu nhiên Ngọc Rồng từ 4 đến 7 sao (`ConstItem.NGOC_RONG_4_SAO` .. `7_SAO`) qua `this.drop(plKill)...execute()`. Không rơi Mảnh thẻ hay Bản đồ kho báu.
    - Loại bỏ hàm rỗng `active()` và hàm `injured()` trùng lặp, kế thừa 100% xử lý sát thương của lớp cha `Boss.java`.
    - Bảo toàn nguyên vẹn cơ chế nhảy dù phục kích độc đáo `afk()` (đáp từ y=0 xuống khi người chơi tới gần).
    - Dọn sạch các import không dùng.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[NinjaAoTim: Tích Hợp Thuật Hoán Đổi Vị Trí & Thuật Thế Thân Khi Trúng Đòn]**:
  * **Cập nhật trong [NinjaAoTim.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaAoTim.java)**:
    - Quản lý danh sách phân thân bằng `List<NinjaClone> clones` an toàn bộ nhớ (`clear()` khi `leaveMap`).
    - **Tối ưu kiến trúc (Refactoring)**: Tách logic tạo phân thân thành hàm riêng `callNinjaClones()`, gọi `clone.joinMap()` tức thì để clone xuất hiện không độ trễ.
    - **Tối ưu hiệu năng (Zero-Allocation)**: Viết lại `getRandomAliveClone()` duyệt mảng trực tiếp bằng 2 lượt đếm và chọn, loại bỏ hoàn toàn việc cấp phát `new ArrayList<>()` ở mỗi đòn đánh, giảm áp lực rác cho GC.
    - **Hoán đổi an toàn**: Bổ sung điều kiện kiểm tra `location.x > 0` trong `swapPosition()` chống glitch tọa độ 0.
    - **Thuật Thế Thân (50% cơ hội)**: Khi Boss thật bị tấn công trong lúc vẫn còn Clone sống xung quanh, có 50% tỉ lệ kích hoạt thế thân (chat: `"Đó chỉ là tàn ảnh thôi!"`, tráo vị trí, chuyển sát thương cho Clone chịu thay).
    - **Giai đoạn kết liễu**: Khi người chơi đã tiêu diệt sạch toàn bộ các NinjaClone, Boss thật không còn ai để thế thân nữa và nhận sát thương trực tiếp từ 50% về 0 bình thường.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[NinjaAoTim: Chặn Máu 50% Chống One-Shot & Gọi Ngẫu Nhiên 4-6 Clone (50% HP Boss Hiện Tại)]**:
  * **Cập nhật trong [NinjaAoTim.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaAoTim.java#L57-L95)**:
    - Khi nhận đòn làm máu tụt xuống $\le 50\%$ HP tối đa mà chưa phân thân (`!this.calledNinja`):
      * Cố định ngay máu boss ở mốc 50% HP (`this.nPoint.hp = halfHp;`), ngăn chặn hoàn toàn việc bị One-shot đòn đầu.
      * Gửi gói tin cập nhật máu cho map qua `PlayerService.gI().sendInfoHp(this)`.
      * Chat câu thoại `"Phân thân chi thuật!"`.
      * Triệu hồi ngẫu nhiên từ 4 đến 6 NinjaClone (`Util.nextInt(4, 6)`), mỗi clone sở hữu máu = 50% máu hiện tại của Boss (`this.nPoint.hp / 2`).
      * Hóa giải đòn đánh đó (`return 0`).
    - Sau khi đã phân thân (`this.calledNinja = true`), các đòn đánh tiếp theo trừ máu từ 50% về 0 bình thường qua `super.injured(...)`.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Chuẩn Hóa Thưởng Cho NinjaAoTim & Tách Biệt NinjaClone (Không Cộng Điểm, Không Mảnh Thẻ)]**:
  * **Cập nhật trong [NinjaAoTim.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaAoTim.java)**:
    - `reward(Player plKill)`: Gọi `super.reward(plKill)` xác nhận nhiệm vụ, cộng 3 điểm sự kiện (`rewardEventPoint`), gom rơi đồ 1 giỏ `this.drop(plKill)...execute()`: Ngọc Rồng 4s–7s, Mảnh Ninja Áo Tím (ID 841), Bản đồ kho báu (ID 611).
    - `injured()`: Giữ nguyên cơ chế phân thân 4-6 NinjaClone khi HP <= 50%, ủy quyền cho `super.injured(plAtt, damage, piercing, isMobAttack)`. Xóa bỏ `active()` rỗng.
  * **Cập nhật trong [NinjaClone.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaClone.java)**:
    - `reward(Player plKill)`: Loại bỏ hoàn toàn cộng điểm săn boss (`rewardEventPoint`), không gọi `super.reward`, không rơi Mảnh Ninja Áo Tím. Sử dụng trực tiếp `.rate(10)` của `DropBuilder` thay vì khối `if (Util.isTrue(...))` bên ngoài để rơi ngẫu nhiên Ngọc Rồng 4s–7s.
    - `injured()`: Tối giản hóa ủy quyền cho `super.injured(...)` với 20% né "Xí hụt".
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[TrungUyThep: Cấu Hình Skill Đấm Chỉ Gây 10% Dame]**:
  * **Cập nhật trong [TrungUyThep.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyThep.java#L44-L60)**:
    - Nhận diện các chiêu đấm bằng phương thức chuẩn `SkillUtil.isUseSkillDam(plAtt)` (kiểm tra an toàn null trên `plAtt`, `playerSkill`, `skillSelect`): bao gồm 5 chiêu đấm cận chiến của 3 hành tinh (`DRAGON`, `DEMON`, `GALICK`, `KAIOKEN`, `LIEN_HOAN`).
    - Nếu trúng skill đấm: `damage = damage / 10;` (chỉ gây 10% sát thương), các skill chưởng khác giữ nguyên 100% sát thương.
    - Giữ nguyên 20% né chiêu *"Xí hụt"* nếu không phải đòn xuyên thấu (`!piercing`).
    - Ủy quyền toàn bộ xử lý trừ HP, phá khiên và tử trận cho `super.injured(plAtt, damage, piercing, isMobAttack)`.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Chuẩn Hóa Phần Thưởng & Tối Giản Hóa TrungUyThep Bằng Kế Thừa Lớp Cha Boss]**:
  * **Cập nhật trong [TrungUyThep.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyThep.java)**:
    - **`reward(Player plKill)`**:
      * Gọi `super.reward(plKill)` để hoàn thành nhiệm vụ tiêu diệt Boss trung tâm.
      * Thưởng 5 điểm sự kiện `this.rewardEventPoint(plKill, 5)`.
      * Gom rơi đồ vào 1 giỏ `this.drop(plKill)...execute()`: Rơi ngẫu nhiên Ngọc Rồng từ 4 đến 7 sao (`ConstItem.NGOC_RONG_4_SAO` .. `7_SAO`) và 1 Bản đồ kho báu (`ConstItem.BAN_DO_KHO_BAU`). *(Lưu ý: Hệ thống thẻ sưu tầm NRO gốc không có thẻ của Trung Úy Thép)*.
    - **`attack()`**:
      * Giới hạn phạm vi tấn công trong sàn tháp tầng 3 Doanh trại (`640 <= x <= 980`).
      * Nếu boss văng ra khỏi sàn tháp, tự động đưa về vị trí trung tâm (`884, 312`).
      * Ủy quyền xử lý chiến đấu toàn diện cho `super.attack()`.
      * Dọn dẹp triệt để các hàm thừa: `goToXY()`, `goToPlayer()`, `getY()`, biến `lastTimeMove`.
    - **`injured()`**:
      * Giữ nguyên cơ chế đặc trưng "mình đồng da sắt" của Trung Úy Thép: 20% tỉ lệ né đòn "Xí hụt" và giảm 50% sát thương nhận vào (`damage / 2`).
      * Ủy quyền toàn bộ việc trừ máu, vỡ khiên, phản đòn và xử lý hạ gục cho `super.injured(plAtt, damage / 2, piercing, isMobAttack)`.
    - **Dọn dẹp code**: Xóa bỏ `active()` rỗng, loại bỏ các import không sử dụng.
  * **Kiểm tra biên dịch**: Đã chạy `compile_check.bat`, đạt `[COMPILE_SUCCESS]`, xuất file `NroBaKhi.jar` thành công.

* **[Xóa Bỏ injured() Thừa Trong TrungUyXanhLo Kế Thừa Lớp Cha Boss]**:
  * **Cập nhật trong [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyXanhLo.java#L47-L65)**:
    - Loại bỏ hoàn toàn hàm `injured()` thừa (không có cơ chế đặc biệt như Bulon của Trung Úy Trắng).
    - Kế thừa 100% `injured()` chuẩn của `Boss.java`: Tự động tính toán phòng ngự (Def), né đòn theo `tlNeDon`, vỡ khiên năng lượng, trừ máu và xử lý khi chết.

* **[Gom Đợt Rơi Đồ Vào 1 Giỏ execute() Duy Nhất (TrungUyTrang & TrungUyXanhLo)]**:
  * **Cập nhật trong [TrungUyTrang.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyTrang.java#L33-L49)**:
    - Gom tất cả các món đồ (Ngọc Rồng 4s–7s, Mảnh Trung úy Trắng) vào cùng 1 chuỗi `this.drop(plKill)...execute()`, kích hoạt rơi hình cánh sen (Fan-Out) tránh đè tọa độ.
  * **Cập nhật trong [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyXanhLo.java#L28-L45)**:
    - Gom tất cả các món đồ (Ngọc Rồng 4s–7s, Mảnh Trung úy Xanh Lơ, Bản đồ kho báu) vào cùng 1 chuỗi `this.drop(plKill)...execute()`.

* **[Nâng Cấp Phần Thưởng Rơi Đồ Trung Úy Xanh Lơ (NR 4s–7s, Thẻ Sưu Tầm & Bản Đồ Kho Báu)]**:
  * **Cập nhật trong [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyXanhLo.java#L28-L52)**:
    - Đồng bộ chuẩn `super.reward(plKill)` và `this.drop(plKill)`:
      * `this.drop(plKill).random(17, 18, 19, 20).execute()`: Rơi ngẫu nhiên 1 viên Ngọc Rồng từ 4 sao đến 7 sao.
      * `this.drop(plKill).item(ConstItem.MANH_TRUNG_UY_XANH_LO).execute()`: Rơi Thẻ sưu tầm (Mảnh Trung úy Xanh Lơ ID 842).
      * `this.drop(plKill).item(ConstItem.BAN_DO_KHO_BAU, 1).execute()`: Rơi 1 Bản đồ kho báu (ID 611).
      * `this.drop(plKill).item(1824).execute()`: Rơi vật phẩm sự kiện / nhiệm vụ Doanh Trại (ID 1824).
    - Dọn dẹp hàm `active()` dư thừa.

* **[Nâng Cấp Phần Thưởng Rơi Đồ Trung Úy Trắng (Ngọc Rồng 4s–7s & Thẻ Sưu Tầm)]**:
  * **Cập nhật trong [TrungUyTrang.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyTrang.java#L33-L53)**:
    - Sử dụng chuẩn `this.drop(plKill)` (Fluent DropBuilder) thay cho việc tạo thủ công `ItemMap`:
      * `this.drop(plKill).random(17, 18, 19, 20).execute()`: Rơi ngẫu nhiên 1 viên Ngọc Rồng từ 4 sao đến 7 sao.
      * `this.drop(plKill).item(ConstItem.MANH_TRUNG_UY_TRANG).execute()`: Rơi thẻ sưu tầm (Mảnh Trung úy Trắng ID 840).
      * `this.drop(plKill).item(1824).execute()`: Rơi vật phẩm nhiệm vụ Doanh Trại (ID 1824).
    - Giữ nguyên `rewardEventPoint(plKill, 5)` và `TaskService.checkDoneTaskKillBoss(plKill, this)`.

* **[Tối Giản Hóa attack() Và injured() Của TrungUyTrang Bằng Kế Thừa Lớp Cha]**:
  * **Cập nhật trong [TrungUyTrang.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyTrang.java#L70-L100)**:
    - Loại bỏ các hàm phụ trợ dư thừa `goToXY(...)`, `goToPlayer(...)` và hàm rỗng `active()`.
    - Tái cấu trúc `attack()` theo mô hình kế thừa OOP sạch: Chỉ giữ lại logic giới hạn người chơi trong sàn tháp Doanh Trại (`755 <= x <= 1060`) và dịch chuyển tức thời vào trận khi boss ở vị trí spawn (`x < 775`), sau đó ủy quyền toàn bộ cho `super.attack()`.
    - Tối ưu `injured()`: Chỉ kiểm tra 2 Bulon còn sống thì miễn nhiễm, còn lại ủy quyền cho `super.injured(...)` đảm bảo trừ máu và chết đúng quy chuẩn.

* **[Chuẩn Hóa Builder Cho Toàn Bộ Boss Doanh Trại Trong BossesData.java]**:
  * **Cập nhật trong [BossesData.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/BossesData.java#L1138-L1275)**:
    - Xây dựng đầy đủ các hàm sinh `BossData` bằng `BossData.builder()` cho toàn bộ 6 Boss Doanh Trại:
      * `TRUNG_UY_XANH_LO(int dame, int hp)`
      * `TRUNG_UY_TRANG(int dame, int hp)`
      * `TRUNG_UY_THEP(int dame, int hp)`
      * `NINJA_AO_TIM(int dame, int hp)`
      * `NINJA_CLONE(int dame, int hp)`
      * `ROBOT_VE_SI(int id, int dame, int hp)` & overload `ROBOT_VE_SI(int dame, int hp)`
    - Cú pháp chuẩn hoá `.builder()` tường minh (name, gender, outfit, dame, hp, mapJoin, skillTemp, textS, textM, textE, secondsRest), đảm bảo Thread-Safety 100%.
  * **Cập nhật trong các Boss Subclasses Doanh Trại**:
    - [TrungUyXanhLo.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyXanhLo.java#L22-L25): `super(PHOBANDT, BossID.TRUNG_UY_XANH_LO, BossesData.TRUNG_UY_XANH_LO(dame, hp));`
    - [TrungUyTrang.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyTrang.java#L26-L30): `super(PHOBANDT, BossID.TRUNG_UY_TRANG, BossesData.TRUNG_UY_TRANG(dame, hp));`
    - [TrungUyThep.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyThep.java#L28-L31): `super(PHOBANDT, BossID.TRUNG_UY_THEP, BossesData.TRUNG_UY_THEP(dame, hp));`
    - [NinjaAoTim.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaAoTim.java#L24-L28): `super(PHOBANDT, BossID.NINJA_AO_TIM, BossesData.NINJA_AO_TIM(dame, hp));`
    - [NinjaClone.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/NinjaClone.java#L23-L27): `super(PHOBANDT, id, BossesData.NINJA_CLONE(dame, hp));`
    - [RobotVeSi.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/RobotVeSi.java#L22-L25): `super(PHOBANDT, BossID.ROBOT_VE_SI - id, BossesData.ROBOT_VE_SI(id, dame, hp));`
  * **Biên dịch**: `javac` biên dịch mới 100% thành công vào `build/classes/`. (Lưu ý: `NroBaKhi.jar` đang được tiến trình server game giữ khóa, sẽ tự cập nhật khi restart server).

* **[Nâng Cấp Cơ Chế Hút Đồ Của DropItem.java Chuẩn Gốc NRO]**:
  * **Cập nhật trong [DropItem.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/player/DropItem.java#L428-L445)**:
    - Sửa lỗi thứ tự packet: Luôn gửi gói tin rơi đồ `Service.gI().dropItemMap(zone, itemMap);` (`Message 68`) cho Client trước, giúp Client hiển thị đúng animation món đồ rơi ra đất.
    - Ngay sau đó mới kích hoạt Bùa Thu Hút:
      * Tự động cộng tiến độ danh hiệu: `BadgesTaskService.updateCountBagesTask(beneficiary, ConstTaskBadges.ONG_THAN_VE_CHAI, 1);`
      * Gọi `ItemMapService.gI().pickItem(beneficiary, itemMap.itemMapId, true);` để hút thẳng vào hành trang người chơi (hoặc Sư Phụ nếu do Đệ Tử kết liễu).
    - Tạo hiệu ứng thị giác hoàn hảo: Người chơi nhìn thấy đồ vừa rớt ra đất thì bị Bùa Thu Hút lập tức hút vèo vào người.
  * **Biên dịch & Đóng gói**: Script `compile_check.bat` chạy thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Kích Hoạt Tỉ Lệ Rơi Mảnh Thẻ Quái Doanh Trại Bằng DropItem (Mob.java)]**:
  * **Cập nhật trong [Mob.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/mob/Mob.java#L787-L799)**:
    - Áp dụng chuẩn `DropItem.to(player, this.zone, this.location.x, this.location.y).item(cardId).rate(10).execute();` vào `Mob.java`:
      * Quái 34 (Lính bazooka) $\rightarrow$ Rơi **Mảnh Lính độc nhãn (ID 837)**.
      * Quái 35 (Lính độc nhãn) $\rightarrow$ Rơi **Mảnh Lính độc nhãn (ID 838)**.
      * Quái 36 (Sói xám) $\rightarrow$ Rơi **Mảnh Sói xám (ID 839)**.
    - Cấu hình tỉ lệ rơi 10% bằng mô hình Fluent DropItem: tự động tính raycast mặt đất, kẹp mép bản đồ an toàn (`clampX`), tự nhận diện quyền nhặt đồ.
  * **Biên dịch & Đóng gói**: Script `compile_check.bat` chạy thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Tái Cấu Trúc Phân Định Trách Nhiệm Giữa RedRibbonHQService & RedRibbonHQ]**:
  * **Chuẩn hóa tầng Dịch vụ (`RedRibbonHQService.java`)**:
    - Chuyển các hằng số điều kiện vào cửa & cấu hình phòng (`AVAILABLE = 50`, `N_PLAYER_CLAN = 1`, `N_PLAYER_MAP = 1`, `N_DAY_JOIN_CLAN = 1`, `MIN_X_JOIN = 1285`, `MAX_X_JOIN = 1645`, `TIME_RESET_DOANH_TRAI = 86400000L`) về làm Single Source of Truth tại [RedRibbonHQService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQService.java).
    - Bổ sung hàm tiện ích `getNearbyClanMembers(Player player)` để quét danh sách thành viên cùng bang đang đứng gần NPC tại Map 27.
    - Chuyển toàn bộ logic thu thập người tham gia và dịch chuyển người chơi (`ChangeMapService.gI().changeMapInYard(...)`) vào `RedRibbonHQService.joinDoanhTrai()`.
  * **Chuẩn hóa tầng Mô hình phòng phó bản (`RedRibbonHQ.java`)**:
    - Cho các hằng số điều kiện tham chiếu ngược lại từ `RedRibbonHQService` để đảm bảo tương thích ngược 100% với các class khác (`LinhCanh.java`, `ItemTimeService.java`, `Manager.java`).
    - Giữ lại thuần túy các hằng số nội tại của phòng phó bản: `TIME_DOANH_TRAI = 1800000`, `TIME_PICK_DOANH_TRAI = 300000`, `MAX_HP_CAP = 2_000_000_000L`, `MAX_DAME_CAP = 200_000_000L`.
    - Tinh giản hàm `openDoanhTrai(Clan clan, Player opener, List<Player> participants)`: Chỉ tập trung quản lý trạng thái của phòng phó bản (khởi tạo boss, quái, đồng hồ đếm ngược, lưu mốc mở cho bang hội).
    - Cung cấp overload `openDoanhTrai(Player player)` hỗ trợ tương thích ngược.
  * **Biên dịch & Đóng gói**: Script `compile_check.bat` chạy thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Cân Bằng Chỉ Số Doanh Trại: Bỏ Điểm Sàn, Trần Cố Định 2 Tỷ HP / 200 Triệu Dame]**:
  * **Cải tiến trong [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java)**:
    - Loại bỏ việc dùng `clan.membersInGame` (tính cả người offline/AFK ở nhà); chuyển sang tính toán chuẩn 100% dựa trên danh sách người thực tế tham gia Doanh Trại (`participants`).
    - Bỏ việc nhân/chia tùy tiện với `mob.tempId`.
    - Loại bỏ điểm sàn: Cho phép chỉ số scale tự nhiên theo sức mạnh thực tế của người chơi.
    - Thiết lập trần cố định: `MAX_HP_CAP = 2_000_000_000L` (2 tỷ HP) và `MAX_DAME_CAP = 200_000_000L` (200 triệu Dame) cho toàn bộ Quái và Boss trong Doanh Trại, chống 100% lỗi tràn số nguyên âm (`Integer.MAX_VALUE`).
    - Đồng bộ thống nhất công thức giữa lúc mở phó bản (`init`) và lúc thành viên vào tiếp viện (`updateHPDame`), tránh lỗi Boss bị hồi phục 100% máu khi có đồng đội vào sau.
  * **Biên dịch**: `javac` biên dịch sạch sẽ 100% vào `build/classes`.

* **[Tối Ưu Kiến Trúc Doanh Trại: Lưu Hồi Chiêu Bang Vào MySQL & Bỏ Mốc Cá Nhân Dư Thừa]**:
  * **Cơ sở dữ liệu (MySQL Patch 002)**:
    - Thêm cột `last_time_open_doanhtrai` BIGINT(20) NOT NULL DEFAULT 0 vào bảng `clan` (ghi nhận tại [SQL_PATCHES.md](file:///d:/NgocRongBaKhi/ToolCaiGameV3/SQL_PATCHES.md)).
    - [Clan.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/clan/Clan.java): Bổ sung hàm `updateLastTimeOpenDoanhTrai()` để ghi trực tiếp mốc mở Doanh Trại vào MySQL.
    - [Manager.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/server/Manager.java): Khi khởi động server, tự động nạp `clan.lastTimeOpenDoanhTrai` từ bảng `clan`, phục hồi trạng thái `haveGoneDoanhTrai` nếu chưa đủ 24h.
  * **Tinh giản logic Doanh Trại**:
    - [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java): Gọi `clan.updateLastTimeOpenDoanhTrai()` khi mở phó bản; loại bỏ kiểm tra `lastTimeJoinDT` khi kéo đồng đội vào.
    - [RedRibbonHQService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQService.java): Loại bỏ ràng buộc `lastTimeJoinDT`, chỉ kiểm tra thâm niên vào bang $\ge$ 1 ngày và thời gian mở của Bang $\ge$ 24h.
    - [LinhCanh.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/LinhCanh.java): Bỏ chặn mốc cá nhân `lastTimeJoinDT`, kiểm tra duy nhất mốc 24h của Bang; đồng đội chết chạy lại bấm "Tham gia" không bao giờ bị chặn nhầm.
  * **Biên dịch & Đóng gói**: `compile_check.bat` thành công 100% (`[COMPILE_SUCCESS]`), đóng gói sạch sẽ vào `NroBaKhi.jar`.

* **[Cập Nhật Điều Kiện Mở Doanh Trại & Cơ Chế Cooldown 24 Giờ]**:
  * **Sửa điều kiện thành viên**:
    - Nâng `N_PLAYER_CLAN = 5` trong [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java) (Bang hội phải có từ 5 thành viên trở lên mới được mở).
    - Nâng `N_PLAYER_MAP = 1` trong [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java) (Phải có ít nhất 1 đồng đội cùng bang đứng gần tọa độ 1285..1645).
  * **Đổi cơ chế hồi mở đợt mới từ qua nửa đêm sang đúng 24 giờ**:
    - Thêm `TIME_RESET_DOANH_TRAI = 86400000L` (24 giờ = 86.400 giây).
    - Cập nhật trong [LinhCanh.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/LinhCanh.java), [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java) và [RedRibbonHQService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQService.java):
      * Nếu mở lúc 11h ngày 15/11 thì đúng 11h ngày 16/11 mới được mở tiếp.
      * Hiển thị chính xác thời gian còn lại đến từng giờ/phút/giây qua `TimeUtil.getDateLeft(..., 86400)`.
    - Bảo vệ 2 lớp chống bug out/vào lại bang khác & Chuẩn hóa thứ tự chốt chặn:
      * **Chốt 2 (Lớp Bang Hội)**: Kiểm tra thâm niên gia nhập bang `getNumDateFromJoinTimeToToday() >= 1` TRƯỚC HẾT để từ chối ngay lính mới nhảy bang phá hoại.
      * **Nhánh Tiếp viện (`clan.doanhTrai != null`)**: Cho phép thành viên hợp lệ vào tiếp viện hoặc quay lại phó bản nếu lỡ chết bay về nhà.
      * **Chốt 1 (Lớp Cá Nhân)**: `player.lastTimeJoinDT` lưu trực tiếp MySQL `doanhtrai` $\ge$ 24h khi mở đợt mới.
      * **Hồi chiêu Bang**: `player.clan.lastTimeOpenDoanhTrai` $\ge$ 24h khi mở đợt mới.
  * **Biên dịch**: `javac` biên dịch mới 100% không lỗi vào `build/classes`.

* **[Hoàn Thành Giai Đoạn 3: Chuẩn Hóa OOP Cho Phân Hệ Bản Đồ Mở Theo Giờ (timed_map/)]**:
  * **Tạo mới lớp cha trừu tượng `AbstractTimedMapEvent.java`**:
    - Chuẩn hóa vòng đời mọi sự kiện mở theo giờ (`tick()`, `isEventOpen()`, `getEventMapIds()`).
    - Phân tách 2 pha: Khi sự kiện mở (`updateActiveZone`, `updateActivePlayer`) và khi sự kiện đóng (`evictPlayersWhenClosed`, `kickOutPlayer`, `onClosedZoneReset`).
  * **Xây dựng 4 sự kiện mở theo giờ kế thừa `AbstractTimedMapEvent`**:
    - [BlackBallWarEvent.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/timed_map/BlackBallWarEvent.java): Quản lý 7 map Sao Đen (85..91) từ 20h00–21h00, tự động trao thưởng, reset cờ `zone.finishBlackBallWar`.
    - [MajinBuu12HEvent.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/timed_map/MajinBuu12HEvent.java): Quản lý 6 map Ma Bư 12h (114, 115, 117, 118, 119, 120) từ 12h00–13h00, tự động thôi miên/giải bùa.
    - [MajinBuu14HEvent.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/timed_map/MajinBuu14HEvent.java): Quản lý 2 map Ma Bư 14h (127, 128) từ 14h00–15h00, tự động dọn dẹp giữ chỗ `MaBuHold`.
    - [HirudegarnEvent.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/timed_map/HirudegarnEvent.java): Quản lý Map 126 Hirudegarn từ 22h00–23h00.
  * **Bộ điều phối trung tâm `TimedMapManager.java` & Tiêu diệt 42 Virtual Threads chạy không tải**:
    - Chuyển đổi [TimedMapManager.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/timed_map/TimedMapManager.java) thành Engine trung tâm điều phối 1 luồng duy nhất (`TimedMapManager-Engine`, nhịp 500ms) kiểm soát toàn bộ 16 Map.
    - Loại bỏ hoàn toàn `implements Runnable` và **35 luồng chạy ngầm 24/24h** trong [BlackBallWar.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/timed_map/BlackBallWar.java).
    - Loại bỏ hoàn toàn `implements Runnable` và **7 luồng chạy ngầm 24/24h** trong [MajinBuu14H.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/timed_map/MajinBuu14H.java).
    - Khởi chạy engine tự động trong [ServerManager.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/server/ServerManager.java#L123).
  * **Biên dịch & Đóng gói**: Script `compile_check.bat` chạy thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Hoàn Thành Giai Đoạn 2: Chuẩn Hóa OOP Cho Toàn Bộ 4 Phó Bản Bang Hội (phoban/)]**:
  * **Nâng cấp `AbstractClanDungeon.java`**:
    - Chuẩn hóa vòng lặp `run()` 150ms cố định với `final`, bọc `try-catch` và tự phục hồi sau 1s nếu gặp ngoại lệ nghiêm trọng.
    - Cung cấp phương thức quản trị luồng `startDungeonThread(String title)` bằng Java Virtual Threads (`Thread.ofVirtual()`).
    - Nâng các logic chung lên lớp cha: `finish()`, `dispose()`, `clearAllItems()`, `areAllMobsDead()`, `areAllBossesDead()`, `areAllCharactersDead()`.
    - Thiết lập các Template Method hooks cho các lớp con cài đặt: `kickOut(Player)`, `sendTextTime()`, `removeTextTime()`, `unlinkClan()`, `onResetExtraState()`, `onPlayerFinish(Player)`.
  * **Tạo mới lớp cha quản lý phòng `AbstractClanDungeonService.java`**:
    - Generic Service Pool `AbstractClanDungeonService<T extends AbstractClanDungeon>` quản lý tập trung danh sách phòng `rooms`.
    - Cung cấp cơ chế cấp phát phòng an toàn đa luồng `findAvailableRoom()` với khóa `synchronized` và kiểm tra `!room.isOpened && room.clan == null`.
    - Chuẩn hóa đăng ký map phó bản qua `registerZone(int roomId, Zone zone)`.
  * **Tái cấu trúc 4 phó bản và 4 dịch vụ phó bản bang hội**:
    - [BanDoKhoBau.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/BanDoKhoBau.java) & [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java): Kế thừa `AbstractClanDungeon` & `AbstractClanDungeonService<BanDoKhoBau>`, Bill Pugh Singleton.
    - [DestronGas.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/DestronGas.java) & [DestronGasService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/DestronGasService.java): Kế thừa `AbstractClanDungeon` & `AbstractClanDungeonService<DestronGas>`, Bill Pugh Singleton.
    - [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java) & [RedRibbonHQService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQService.java): Kế thừa `AbstractClanDungeon` & `AbstractClanDungeonService<RedRibbonHQ>`, Bill Pugh Singleton.
    - [SnakeWay.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/SnakeWay.java) & [SnakeWayService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/SnakeWayService.java): Kế thừa `AbstractClanDungeon` & `AbstractClanDungeonService<SnakeWay>`, Bill Pugh Singleton.
  * **Biên dịch & Đóng gói**: Script `compile_check.bat` chạy thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Hoàn Thành Giai Đoạn 1: Vá 5 Lỗi Tiềm Ẩn Hệ Thống Map Theo Tài Liệu chuan_hoa_oop_va_loi_tiem_an_map.md]**:
  * **Vá Lỗi 1 (Bản Đồ Kho Báu)**: Cập nhật `lastTimeJoinBDKB` khi sang ngày mới trong [TreasureUnderSeaService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java) và [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java), khắc phục triệt để bug người chơi đi BĐKB vô hạn lần trong ngày do biến đếm bị reset về 1 liên tục.
  * **Vá Lỗi 2 (Race Condition Phó Bản)**: Khóa `synchronized` và kiểm tra `!isOpened && clan == null` trên cả 4 Service phó bản ([TreasureUnderSeaService](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/TreasureUnderSeaService.java), [DestronGasService](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/DestronGasService.java), [RedRibbonHQService](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQService.java), [SnakeWayService](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/SnakeWayService.java)), chống triệt để bug 2 bang hội mở cùng mili-giây bị nhét chung vào 1 phòng hoặc 1 bang chiếm 2 phòng.
  * **Vá Lỗi 3 & 4 (NPE Map Trống & Đọc TileMap)**: Đưa kiểm tra an toàn `zoneJoin.map.tileMap == null || zoneJoin.map.tileMap.length == 0` lên TRƯỚC điều kiện `player.isAdmin()` trong [ChangeMapService.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/service/ChangeMapService.java#L888-L895) chống crash đen màn hình khi admin dịch chuyển vào map giữ chỗ (như `m116`).
  * **Vá Lỗi 5 (Gói `-105` Khí Gas)**: Thêm kiểm tra `player.idMark != null && player.idMark.getZoneKhiGasHuyDiet() != null` trong [Controller.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/server/Controller.java#L184-L187) chống giật lùi nhân vật khi client gửi đúp gói `-105`.
  * **Biên dịch & Đóng gói**: Script `compile_check.bat` chạy thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Vá Lỗi NPE Tại TrungUyTrang.leaveMap & RedRibbonHQ.dispose]**:
  * **Sửa lỗi `Cannot assign field isTUTAlive because this.zone is null`**:
    - Khi Trung Úy Trắng bị tiêu diệt trong game, boss đã chuyển sang status `REST` và rời map (`this.zone = null`).
    - Khi hết thời gian, hàm `dispose()` của Doanh Trại duyệt danh sách boss và kiểm tra `!boss.isDie()`. Vì status của boss lúc này là `REST` (không phải `DIE`), code lại gọi `boss.leaveMap()` thêm một lần nữa.
    - Tại `TrungUyTrang.leaveMap()`, dòng code `this.zone.isTUTAlive = false;` gọi trên `this.zone` đã null $\rightarrow$ Ném `NullPointerException`.
  * **Giải pháp khắc phục**:
    - Thêm `if (this.zone != null)` trong [TrungUyTrang.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/boss/doanh_trai/TrungUyTrang.java#L168-L171).
    - Trong [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java#L412-L415) hàm `dispose()`, chuyển điều kiện thành `if (boss != null && boss.zone != null)` để chỉ dọn những boss còn đang sống thực tế trong map.
  * **Biên dịch**: `javac` biên dịch mới 100% thành công không có bất kỳ lỗi nào vào `build/classes`.

* **[Vá Triệt Để Lỗi Loop NPE Khi Hết Giờ Doanh Trại (kickOutOfDT & dispose)]**:
  * **Sửa lỗi spam NPE vô hạn khi hết 5 phút nhặt ngọc**:
    - Khi hết thời gian, hàm `finish()` gọi `kickOutOfDT(player)` truy cập `player.zone.map.mapId` mà không kiểm tra null khi người chơi đang bay phi thuyền về nhà $\rightarrow$ Ném NPE khiến `dispose()` không chạy được.
    - Trong `dispose()`, `removeTextDoanhTrai()` duyệt thành viên trong bang gửi gói tin `Message(65)` nhưng không kiểm tra `player.getSession() == null` $\rightarrow$ Ném NPE.
    - Vì `this.isOpened = false;` đặt ở cuối `dispose()`, khi có lỗi ở trên thì biến này không bao giờ được gán `false`, khiến vòng lặp `while (isOpened)` trong hàm `run()` chạy liên tục mỗi 150ms $\rightarrow$ Spam lỗi NPE liên tục như thác nước trên console.
  * **Giải pháp khắc phục**:
    - Thêm `this.isOpened = false;` ngay đầu `dispose()`, bọc dọn dẹp trong `try ... finally` đảm bảo `clan.doanhTrai = null; clan = null;` luôn giải phóng 100%.
    - Thêm kiểm tra null an toàn trong `kickOutOfDT` và `ItemTimeService.sendTextTime`.
    - Thêm `return;` ngay sau `dispose()` trong `update()`.
  * **Biên dịch & Đóng gói**: Chạy `compile_check.bat` thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Đồng Bộ Thời Gian & Trạng Thái Doanh Trại Tại NPC Lính Canh]**:
  * **Sửa lỗi lệch thời gian giữa Icon đếm ngược và NPC**:
    - Khi người chơi đã tiêu diệt trại và gặp Độc Nhãn, Doanh Trại bước vào giai đoạn 5 phút nhặt ngọc (`isTimePicking == true`).
    - Trước đó, `LinhCanh.java` luôn tính thời gian theo tổng 30 phút (`TIME_DOANH_TRAI`) dẫn tới việc góc trái màn hình hiển thị `4'` nhưng NPC lại báo `25 phút`.
    - Đã phân tách rõ trong [LinhCanh.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/LinhCanh.java): khi `isTimePicking == true`, lấy thời gian từ `lastTimePick` với `TIME_PICK_DOANH_TRAI` (5 phút) và thông báo câu thoại *"đã tiêu diệt trại độc nhãn và đang nhặt ngọc"*. Khi đang đánh, đếm theo 30 phút. Đồng bộ 100% với giao diện client.
  * **Biên dịch**: `javac` biên dịch mới 100% thành công không có bất kỳ lỗi nào vào `build/classes`.

* **[Vá Lỗi Crash NPE Doanh Trại & Giải Quyết Vòng Đời Phoban]**:
  * **Sửa lỗi crash `NullPointerException` tại `RedRibbonHQ.updateHPDame()`**:
    - Khi người chơi bấm "Tham gia" để vào lại Doanh Trại, hàm `updateHPDame` duyệt qua danh sách `bosses`. Những con boss đã bị hạ gục trước đó gọi `leaveMap()` khiến `boss.zone == null`, dẫn đến văng lỗi `NullPointerException: Cannot read field "map" because "<local10>.zone" is null`.
    - Đã thêm kiểm tra an toàn toàn diện: `if (boss == null || boss.isDie() || boss.zone == null || boss.zone.map == null) continue;` và dùng `int mapId = boss.zone.map.mapId;` với cấu trúc `else if` tối ưu.
  * **Giải thích cơ chế vòng đời kết thúc Doanh Trại**: Doanh trại chỉ thực sự đóng và reset khi hết 5 phút nhặt ngọc của Độc Nhãn hoặc hết 30 phút tối đa.
  * **Biên dịch**: `javac` biên dịch mới 100% thành công không có bất kỳ lỗi nào vào `build/classes`.

* **[Tối Ưu Hóa NPC Lính Canh & Phó Bản Doanh Trại Độc Nhãn]**:
  * **Vá 3 nguy cơ NPE tại `LinhCanh.java`**:
    - Thêm kiểm tra an toàn `player.getSession() == null || !player.getSession().actived` khi kiểm tra mở thành viên.
    - Thêm kiểm tra an toàn `player.clanMember == null || player.clanMember.getNumDateFromJoinTimeToToday() < RedRibbonHQ.N_DAY_JOIN_CLAN`.
    - Bảo vệ biến tạm `player.clan.playerOpenDoanhTrai != null` tránh văng lỗi crash menu NPC sau khi server restart/bảo trì.
  * **Tối ưu trải nghiệm (UX Ordering)**: Đảo điều kiện xét lượt đi trong ngày lên trước điều kiện đếm đồng đội đứng gần.
  * **Đồng bộ hằng số tọa độ**: Đưa `MIN_X_JOIN = 1285` và `MAX_X_JOIN = 1645` vào [RedRibbonHQ.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/phoban/RedRibbonHQ.java) và dùng chung trong [LinhCanh.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/npc_list/LinhCanh.java). Thoát sớm `break;` vòng lặp khi đủ số lượng người đứng gần.
  * **Vá NPE trong `RedRibbonHQ.openDoanhTrai`**: Thêm kiểm tra null cho `pl.clanMember` và `pl.location` khi quét người chơi cùng bang.
  * **Biên dịch & Đóng gói**: Chạy `compile_check.bat` thành công 100% (`[COMPILE_SUCCESS]`), cập nhật sạch sẽ vào `NroBaKhi.jar`.

* **[Kiểm Tra & Vá 3 Lỗi Logic Trong Các File Service Vừa Quy Hoạch Vào map/]**:
  * **Vá Issue 1 (`service/EffectMapService.java`)**:
    - Sửa byte đầu tiên của gói tin `Message(113)` trong `sendEffectMapToPlayer` từ `writeByte(id)` thành `writeByte(loop)`, khớp với tham số `loop` truyền vào và đồng bộ với cấu trúc gói tin của `sendEffectMapToAllInMap`.
  * **Vá Issue 2 (`timed_map/BlackBallWarService.java`)**:
    - Xóa bỏ 2 dòng gọi nhầm `setHp`/`setMp` trong hàm `xDame(Player, byte)` để tránh bug tự động hồi máu/KI bất thường khi người chơi chỉ mua bùa phù hộ Sức đánh.
    - Bọc kiểm soát biên chống tràn số `Math.min((long) player.nPoint.hp * x, (long) player.nPoint.hpMax)` trong hàm `xHPKI` để đảm bảo an toàn tuyệt đối theo Điều 15 của `GEMINI.md`.
  * **Vá Issue 3 (`timed_map/MajinBuu14HService.java`)**:
    - Thêm điều kiện `if (zone.map != null && zone.map.mapId == 128 && (zone.maBuHolds == null || zone.maBuHolds.isEmpty()))` trong hàm `addMapMaBu2H(int id, Zone zone)`. Tránh bug nhân đôi số lượng kén giam từ 4 slot lên 8 slot do `Zone.java` constructor đã khởi tạo sẵn 4 slot `MaBuHold` cho Map 128.
  * **Biên dịch & Đóng gói**:
    - Script `compile_check.bat` tự động kiểm tra hoàn tất, trả về `[COMPILE_SUCCESS]`, đóng gói thành công `NroBaKhi.jar`.

* **[Quy Hoạch Toàn Diện Hệ Thống Map: Đồng Bộ phoban & map.service]**:
  * **Đóng gói trọn gói Phó bản bang hội (`nro.models.map.phoban`)**:
    - Chuyển 4 Service phó bản (`TreasureUnderSeaService`, `DestronGasService`, `RedRibbonHQService`, `SnakeWayService`) từ `services_dungeon` sang `nro.models.map.phoban`.
    - Thư mục `phoban` hiện tại đóng gói trọn gói 100% cả Model lẫn Service của 4 phó bản (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`).
  * **Quy tụ Dịch vụ Bản đồ (`nro.models.map.service`)**:
    - Chuyển `EffectMapService.java` (hiệu ứng thời tiết/bão trên map) từ `services_func` sang `nro.models.map.service` (đứng chung cùng `MapService`, `ChangeMapService`, `ItemMapService`, `NpcService`).
  * **Đồng bộ mã nguồn**: Cập nhật sạch sẽ imports trong `BotAttackplayer.java`, `Map.java`, `TrapMap.java`, `LinhCanh.java`, `MrPoPo.java`, `QuyLaoKame.java`, `ThanVuTru.java`, `SkillService.java`.
  * **Biên dịch**: `javac` biên dịch mới 100% thành công không có bất kỳ lỗi nào vào `build/classes`.

* **[Đóng Gói Module Trọn Gói timed_map: Vừa Quản Lý Map Vừa Quản Lý Hoạt Động]**:
  * **Kiến trúc Feature Module tập trung**:
    - Thư mục `nro.models.map.timed_map` trở thành module độc lập trọn gói cho toàn bộ 16 Map mở theo giờ, tích hợp cả vòng đời Map lẫn logic nghiệp vụ hoạt động của người chơi:
      1. `TimedMapManager.java`: Tổng điều phối trung tâm toàn bộ 16 Map (mở/đóng cửa, kiểm tra & trục xuất tức thì).
      2. `BlackBallWarService.java` & `BlackBallWar.java`: Trọn gói sự kiện Sao Đen 20h–21h (Maps 85..91: zones, giữ ngọc 300s, rơi ngọc, nhận thưởng bang).
      3. `MajinBuu14HService.java` & `MajinBuu14H.java`: Trọn gói sự kiện Mabư 14h–15h (Maps 127..128: 7 phòng, cột trói MaBuHold).
      4. `MajinBuuService.java`: Trọn gói sự kiện Mabư 12h–13h (Maps 114..120: các tầng ải, diệt Mabư, Babiđây thôi miên & Ôsin giải bùa).
    - Map 126 (Hirudegarn 22h–23h): Là map Boss thế giới thông thường, được điều phối trực tiếp bởi `TimedMapManager` và NPC `Tapion`, không sinh thêm class/thread thừa thãi.
    - Đã dọn dẹp triệt để các file thừa (`HirudegarnWar.java`, `MajinBuu12H.java`).
  * **Đồng bộ hóa**: Cập nhật sạch sẽ imports trên toàn bộ server (`Map`, `Zone`, `Player`, `Mabu`, `ChangeMapService`, `Osin`, `Rong1Sao`, `Controller`, `InventoryService`, `TimeUtil`).
  * **Biên dịch**: `javac` biên dịch mới 100% thành công không có bất kỳ lỗi nào vào `build/classes`.

* **[Khởi Tạo TimedMapManager & Chuẩn Hóa Cơ Chế Trục Xuất 16 Map Mở Theo Giờ]**:
  * **Kiến trúc mới**: Tạo package mới `nro.models.map.timed_map` và lớp điều phối tập trung `TimedMapManager.java` quản lý toàn bộ 16 Map mở theo giờ (Mabu 12h: `114..120`, Mabu 14h: `127, 128`, Sao Đen 20h: `85..91`, Hirudegarn 22h: `126`).
  * **Hành vi thống nhất**:
    - Khi người chơi vào map hoặc đang đứng trong map ngoài khung giờ mở: Lập tức gửi thông báo popup/chat màu đỏ chính xác thời gian mở tiếp theo và trục xuất người chơi bằng phi thuyền về khu an toàn (Nhà / Trạm tàu / Thành phố Vegeta).
    - Cờ `ALLOW_ADMIN_STAY = false` áp dụng chuẩn xác cho cả Admin để kiểm tra tính năng trực tiếp.
  * **Đồng bộ hóa các file**:
    - `TimeUtil.java`: Thêm hàm `isHirudegarnOpen()` (`hour >= 22 && hour < 23`).
    - `Tapion.java`: Sửa điều kiện vào Map 126 dùng `TimeUtil.isHirudegarnOpen()`, loại bỏ bug mở từ 1h-23h.
    - `ChangeMapService.java`: Tích hợp `TimedMapManager.gI().checkAndKickIfClosed(player)` tại `finishLoadMap` để chặn ngay khi người chơi vừa đổi map vào (kể cả dùng lệnh admin `m...`).
    - `Player.java`: Tích hợp `checkAndKickIfClosed(this)` trong nhịp `update()` 1 giây để trục xuất tự động khi đồng hồ điểm hết giờ sự kiện.
    - `MajinBuuService.java` & `MajinBuu14H.java` & `BlackBallWar.java`: Ủy quyền toàn bộ cơ chế trục xuất về `TimedMapManager`, loại bỏ đoạn đếm ngược 30 giây rườm rà.
  * **Biên dịch**: `javac` biên dịch mới 100% không lỗi vào `build/classes`.

* **[Cập Nhật Thời Gian Mở Cửa Mabu 14h Trong TimeUtil.java]**:
  * **Thay đổi**: Sửa hàm `TimeUtil.isMabu14HOpen()` từ khung giờ cũ `hour >= 22 && hour < 23` (22h) trở về chuẩn **14h00 – 15h00** (`hour >= 14 && hour < 15`) theo đúng tên gọi và yêu cầu của người dùng.
  * **Biên dịch**: `javac` biên dịch mới 100% không lỗi vào `build/classes`.

* **[Dọn Dẹp Dead Code isskh Trong MapService.java]**:
  * **Thay đổi**: Đã xóa bỏ hàm chết/trùng lặp `isskh(int mapId)` tại dòng 460 của `MapService.java` vì hoàn toàn không có nơi nào trong toàn bộ dự án gọi tới. Logic chuẩn rơi trang bị Kích Hoạt (SKH) của 3 hành tinh (`1, 2, 3`, `8, 9, 11`, `15, 16, 17`) đã được đảm nhiệm thống nhất bởi `isMapUpSKH(int mapId)` trong `Mob.java`.
  * **Biên dịch**: `javac` biên dịch mới 100% không lỗi vào `build/classes`.

* **[Rút Gọn & Tối Ưu isMapUpPorata Trong MapService.java]**:
  * **Thay đổi**: Chuyển `mapId == 156 || mapId == 157 || mapId == 158 || mapId == 159` thành dạng khoảng liên tục `mapId >= 156 && mapId <= 159` (4 map Thánh địa Kaio Đông, Tây, Nam, Bắc up Bông tai Porata cấp 2).
  * **Biên dịch**: `javac` biên dịch mới 100% không lỗi vào `build/classes`.

* **[Đồng Bộ Hóa Hàm isMapTuongLai & Loại Bỏ Map 95, 101]**:
  * **Vấn đề**: Hàm `isMapTuongLai(Map map)` ở dòng 398 viết lười `mapId >= 92 && mapId <= 103` vô tình tính cả Map 95 và 101 (2 map rỗng/dummy không thuộc thế giới Tương Lai), lệch pha với hàm `isMapTuongLai(int mapId)` ở dòng 467.
  * **Khắc phục**: Sửa dòng 398 ủy quyền trực tiếp `return map != null && isMapTuongLai(map.mapId);` theo chuẩn DRY, loại trừ triệt để 95 và 101.
  * **Biên dịch**: Đóng gói thành công `NroBaKhi.jar` (`[COMPILE_SUCCESS]`).

* **[Sửa Lỗi Logic isMapMaBu Trong MapService.java]**:
  * **Vấn đề**: Hàm `MapService.isMapMaBu(int mapId)` trước đây viết `mapId >= 114 && mapId <= 120` đã vô tình tính luôn Map 116 (Thánh địa Kaio). Dẫn tới người chơi vào Map 116 bị ép cờ PK Ma Bử, điểm danh sự kiện, chết hồi sinh về Map 114, hoặc bị chặn capsule/dịch chuyển bạn bè.
  * **Khắc phục**: Sửa thành `return mapId >= 114 && mapId <= 120 && mapId != 116;` (chỉ gồm đúng 6 map Ma Bử 12h: 114, 115, 117, 118, 119, 120).
  * **Biên dịch**: `javac` biên dịch thành công 100% không lỗi vào `build/classes`.

* **[Sửa Triệt Để Lỗi NPE yPhysicInTop Khi Vào Map 116 & Bù Đầy Đủ Dữ Liệu TileMap]**:
  * **Nguyên nhân gốc rễ**: 
    - Map 114 (Cổng phi thuyền) và Map 115 (Phòng chờ) thuộc sự kiện Ma Bử 12h. Map tiếp theo của chuỗi Ma Bử trong NRO là **Map 117 (Cửa ải 1)**, **Map 118**, **Map 119**, **Map 120** (bỏ qua ID 116).
    - Map 116 trong CSDL (`map_template`) là "Thánh địa Kaio" (nhân bản từ Map 50, `tile_id = 10`), nhưng thư mục `data/map/tile_map_data/` bị thiếu file `116` (và cả file `78`).
    - Khi server nạp Map 116, do thiếu file trên đĩa cứng nên `this.tileMap` bị `null`. Khi Admin gõ lệnh `m116`, hàm `Map.yPhysicInTop()` gọi `tileMap[0].length` gây `java.lang.NullPointerException`.
  * **Giải pháp khắc phục**:
    - Khởi tạo file dữ liệu nhị phân `data/map/tile_map_data/116` (sao chép chuẩn từ `tile_map_data/50` cùng `tile_id = 10`) và `data/map/tile_map_data/78` (từ `tile_map_data/0`). Kiểm tra lại 100% (172/172) map trong DB đều đã có đầy đủ `tile_map_data`.
    - Bổ sung chốt chặn an toàn phòng thủ `if (tileMap == null || tileMap.length == 0 || tileMap[0] == null) return y;` trong [Map.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/map/Map.java#L398) tại `yPhysicInTop()`, `moveXY()`, và khởi tạo `this.tileMap` khi nạp từ file trong `readTileMap()`.
    - Tối ưu [Manager.java](file:///d:/NgocRongBaKhi/ToolCaiGameV3/Source/NroBaKhi/src/nro/models/server/Manager.java#L1119): Kiểm tra `file.exists()`, dùng `readUnsignedByte()` và bắt lỗi có log tránh ngoại lệ `FileNotFoundException`.
    - Đã chạy kiểm tra biên dịch tự động thành công 100% (`[COMPILE_SUCCESS]`), đóng gói sạch vào `NroBaKhi.jar`.

* **[Tối Ưu Single-Pass O(N) Thuật Toán Phân Bổ Khu & Đồng Bộ ChangeMapService]**:
  * **Tối ưu `MapService.getZone(mapId)`**:
    - Chuyển đổi từ thuật toán 3 vòng lặp ($O(3N)$) sang **1 vòng lặp duy nhất ($O(N)$ Single-Pass)**: Kết hợp kiểm tra ngưỡng $\approx 66\%$ (Early Return rót từ Khu 0) và theo dõi `leastLoadedZone` (khu có số người ít nhất) ngay trong cùng 1 lần duyệt.
    - Loại bỏ hoàn toàn phép tính số thực `Math.round(zone.maxPlayer * 0.66f)`, thay bằng phép tính số nguyên `Math.max(1, (zone.maxPlayer * 2 + 1) / 3)` tối ưu chu kỳ CPU.
    - Lưu biến cục bộ `int count = zone.getNumOfPlayers()` để tránh gọi lặp lại `.size()` trên `CopyOnWriteArrayList` và loại bỏ race condition giữa các luồng.
  * **Đồng bộ và dọn lỗi `ChangeMapService.getMapCanJoin(player, mapId)`**:
    - Loại bỏ logic cũ duyệt lặp và trả về `null` khi tất cả các khu đều $\ge 7$ người (gây lỗi cho Đại Hội Võ Thuật và Dịch chuyển Yardrat).
    - Ủy quyền trực tiếp cho `MapService.gI().getZone(mapId)` với thuật toán Single-Pass chuẩn, bổ sung null-check đầy đủ `player != null && player.zone != null && player.zone.map != null`.
  * **Kiểm tra biên dịch**: Tất cả các file `.class` đã được `javac` biên dịch mới 100% không lỗi vào `build/classes`.

* **[Triển Khai Thuật Toán Rót Nước Từng Tầng (Waterfall 66%) Cho `MapService.getZone()`]**:
  * **Vấn đề cũ**: Hàm `getZone(mapId)` trước đây chọn ngẫu nhiên một khu trống, dẫn tới người chơi bị rải rác mỗi người một khu khiến server trông vắng vẻ (Dead Game feel), và khi tất cả các khu đều đầy thì dồn hết người chơi vào Khu 0 gây quá tải cục bộ.
  * **Thuật toán mới**:
    - **Bước 1**: Rót lần lượt từ Khu 0 trở đi cho khu đầu tiên có số lượng người `< 66%` dung lượng (`< Math.round(maxPlayer * 0.66f)`, tương đương `< 10/15` người). Giúp 10 người đầu tiên luôn gặp nhau ở Khu 0 tạo không khí nhộn nhịp, đồng thời để lại 33% (5 chỗ trống) cho bạn bè/bang hội đổi khu vào chơi chung.
    - **Bước 2**: Khi toàn bộ các khu đều đã `>= 66%`, tìm khu chưa đầy 100% (`< maxPlayer`) có số người ít nhất hiện tại (Least Loaded) để rót nốt chỗ trống.
    - **Bước 3**: Khi toàn bộ các khu đều đã full 100% (`15/15`), tiếp tục đưa vào khu ít người nhất để san sẻ đều tải, triệt tiêu hoàn toàn hiện tượng quá tải ở Khu 0.
  * Đã kiểm tra biên dịch mã nguồn Java thành công 100% vào `build/classes`. Khi người dùng tắt server đang chạy, script `compile_check.bat` sẽ tự động đóng gói cập nhật vào `NroBaKhi.jar`.

* **[Sửa Lỗi EOFException & Hoàn Thiện Tinh Chỉnh Hệ Thống Map 10/10]**:
  * **Khắc phục triệt để `java.io.EOFException` trong `readTileMap`**:
    - Phát hiện và loại bỏ lệnh đọc thừa byte `dis.readByte()` ở đầu file nhị phân `tile_map_data/<mapId>` trong `Map.java` và `MapService.java` (gây lệch byte header `tmw`, `tmh` dẫn tới lỗi EOF khi map có tile[0][0] khác 0 như Map 21, 22, 24, 25, 30, 39, 40, 138, 183).
    - Tối ưu **0-Disk-Read** cho `Map.readTileMap`: Nạp thẳng ma trận `maps` và kích thước `tmw, tmh, pxw, pxh` từ `this.tileMap` đã có sẵn trên RAM do `Manager.initMap()` nạp vào constructor.
    - Sửa `MapService.readTileMap` đọc đúng `readUnsignedByte()` và bắt lỗi có ghi log qua `Logger.logException`.
  * **Hoàn thiện triệt để 4 điểm tinh chỉnh cuối cùng**:
    - Cả 4 phó bản (`BanDoKhoBau`, `DestronGas`, `RedRibbonHQ`, `SnakeWay`) đã gọi đầy đủ các hàm tiện ích của lớp cha `AbstractClanDungeon`: `areAllCharactersDead()`, `areAllMobsDead()`, `clearAllItems()`, và bọc kiểm tra `if (this.clan != null)` trong `dispose()`.
    - Sửa tràn số nguyên trong `SnakeWay.init()`: dùng `Math.min((long) level * ..., Integer.MAX_VALUE)`.
    - Xóa câu lệnh `if` kiểm tra map nhà lặp lồng nhau 2 lần trong `ChangeMapService.finishLoadMap()`.
    - Bổ sung `Logger.logException` cho các khối catch trong `NpcService.createTutorial()` và các hàm tạo Boss phó bản; xóa import thừa `Executors` trong `BlackBallWar.java`.
  * Biên dịch ngầm tự động thành công 100% (`[COMPILE_SUCCESS]`), đóng gói sạch vào `NroBaKhi.jar`.
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
