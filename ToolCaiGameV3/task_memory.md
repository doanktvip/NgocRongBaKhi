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
