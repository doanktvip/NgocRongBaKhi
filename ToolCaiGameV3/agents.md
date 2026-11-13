# AGENTS.md — Quy Chuẩn Kỹ Thuật & Bản Đồ Điều Hướng Dự Án NgocRongBaKhi

> **QUY TẮC BẮT BUỘC DÀNH CHO AI AGENT:**
> 1. **KHÔNG đọc toàn bộ file Java hàng ngàn dòng**: Trước khi đọc bất kỳ file nào, BẮT BUỘC tra cứu [codebase_map.md](file:///d:/NgocRongBaKhi/ToolCaiGameV3/codebase_map.md) để lấy đúng tên class và phạm vi dòng (StartLine .. EndLine).
> 2. **Tự động định vị từ khóa Game sang File**: Người dùng chỉ cần nói từ khóa tính năng thông thường (rơi đồ, đập đồ, đệ tử, bùa, boss, trade, web), AI tra cứu ngay **Mục 0 trong codebase_map.md** để tìm đúng file đích mà không cần người dùng chỉ định.
> 3. **Tra cứu hằng số ID**: Tuyệt đối không đọc lại ConstTask.java, ConstItem.java, BossesData.java. BẮT BUỘC tra cứu trực tiếp tại [GAME_CONSTANTS_INDEX.md](file:///d:/NgocRongBaKhi/ToolCaiGameV3/GAME_CONSTANTS_INDEX.md) (chứa 100% đầy đủ 251 option, 150 boss, 158 map, 242 npc, 1225 item).
> 4. **Mở rộng tính năng**: Tuân theo 5 quy trình chuẩn từng bước trong [WORKFLOW_RECIPES.md](file:///d:/NgocRongBaKhi/ToolCaiGameV3/WORKFLOW_RECIPES.md).
> 5. **Mạng & Database**: Tra cứu cấu trúc gói tin tại [packet_protocol.md](file:///d:/NgocRongBaKhi/ToolCaiGameV3/packet_protocol.md) và cấu trúc mảng JSON 43 bảng tại [db_json_schema.md](file:///d:/NgocRongBaKhi/ToolCaiGameV3/db_json_schema.md).

---

## 1. Bản Đồ Ngữ Cảnh Hệ Thống (System Context Map)

### 1.1. Cấu trúc tổng thể
* **Game Server (`ToolCaiGameV3/Source/NroBaKhi`)**:
  * Ngôn ngữ & Runtime: Java (sử dụng `Thread.ofVirtual()` cho kết nối mạng `Session`, `Sender`, `Collector`).
  * Cơ sở dữ liệu: MySQL kết nối qua `HikariCP-5.1.0` (`nro.models.data.LocalManager`).
  * Đầu vào gói tin (Entry Point): `nro.models.network.Collector` đọc `Message` -> đẩy vào `nro.models.server.Controller.onMessage(ISession, Message)`.
  * Vòng lặp thế giới (World Tick): `nro.models.server.Manager` điều phối cập nhật các `Zone` theo chu kỳ.
* **Web Portal & Admin (`ToolCaiGameV3/Source/WebTeamobi`)**:
  * Ngôn ngữ: PHP thuần kết nối trực tiếp vào cùng cơ sở dữ liệu MySQL (`nrobakhi`).
  * Giao tiếp Web -> Server: Thông qua bảng trung gian trong MySQL (ví dụ: `shop_reload_request` được `ShopDAO.pollReloadRequests()` và `GiftCodeManager.pollReloadRequests()` quét định kỳ mỗi giây).

### 1.2. Danh sách các "God Classes" (Điểm nóng Nợ kỹ thuật cần thu hẹp dần)
Khi làm việc với các tệp dưới đây, **NGHIÊM CẤM** viết thêm logic nghiệp vụ dài dòng vào trực tiếp. Bắt buộc phải tách hàm/lớp mới ra package chuyên biệt rồi chỉ gọi 1 dòng điều phối:
1. `nro/models/consts/ConstTask.java` (~6.356 dòng) — Chứa dữ liệu kịch bản nhiệm vụ hardcode.
2. `nro/models/services/Service.java` (~2.972 dòng) — Ôm đồm gửi gói tin toàn server, chat, thông báo, đồng bộ map, đổi mật khẩu, điểm sự kiện.
3. `nro/models/boss/BossesData.java` (~2.762 dòng) — Chứa toàn bộ chỉ số, kỹ năng, lời thoại của Boss hardcode.
4. `nro/models/player/NPoint.java` (~2.162 dòng) — Tính toán toàn bộ chỉ số HP, KI, Sức đánh, Chí mạng, Giáp, Nội tại, Cải trang.
5. `nro/models/player/Player.java` (~1.908 dòng) — Thực thể người chơi kiêm vòng lặp cập nhật.
6. `nro/models/services/RewardService.java` (~1.881 dòng) & `TaskService.java` (~1.654 dòng).
7. `nro/models/database/MrBlue.java` (~1.355 dòng) & `PlayerDAO.java` (~1.137 dòng) — Đăng nhập, parse/serialize mảng JSON lồng nhau thủ công.
8. `nro/models/server/Controller.java` (~952 dòng) — `switch(cmd)` khổng lồ xử lý hàng trăm mã lệnh mạng.

---

## 2. Năm Thói Quen Kỹ Thuật Bắt Buộc (5 Core Technical Habits)

### Thói quen 1: Tuân thủ Clean Code & SOLID
1. **Quy tắc đặt tên có ý nghĩa (Meaningful Naming)**:
   * Không đặt tên biến vô nghĩa kiểu `b`, `i1`, `plChanged`, `st`, `cuantity1`, `isTopSukien1Changed`.
   * Đặt tên phản ánh đúng bản chất nghiệp vụ: `movementType`, `targetPlayer`, `tickStartTimeMs`, `exchangeAmountVnd`.
   * Hằng số (`Const*`) hoặc `enum` phải thay thế hoàn toàn các con số ma thuật (Magic Numbers). Ví dụ: thay vì viết `if (item.template.id == 457)` hay `case -100:`, phải khai báo `ItemId.THOI_VANG = 457` và `Cmd.CONSIGN_SHOP = -100`.
2. **Giới hạn độ dài hàm & Sử dụng Guard Clauses**:
   * Mỗi phương thức (method) **không vượt quá 30–40 dòng code**.
   * Độ sâu lồng nhau (`if / for / switch` lồng nhau) **tối đa 3 cấp**. Sử dụng *Guard Clauses* (kiểm tra điều kiện sai và `return` sớm ở đầu hàm) thay vì bọc toàn bộ hàm trong `if (player != null) { if (player.zone != null) { ... } }`.
3. **Áp dụng 5 nguyên tắc SOLID vào thực tế source NRO**:
   * **S (Single Responsibility)**: Mỗi lớp chỉ có 1 lý do để thay đổi. Không viết câu lệnh SQL (`PreparedStatement`) trực tiếp bên trong `Service.java` hay `ClanService.java` — mọi truy vấn SQL phải nằm trong lớp `*Repository` hoặc `*DAO`.
   * **O (Open/Closed)**: Khi thêm 1 vật phẩm mới có cách sử dụng riêng (trong `UseItem.java` hoặc `GiftBoxHandler.java`) hoặc thêm 1 chỉ số cải trang mới (trong `NPoint.java`), không sửa hàm `switch-case` dài 1.000 dòng. Hãy đăng ký một `IItemUseHandler` hoặc `IStatModifier` mới vào `Map<Integer, Handler>`.
   * **L (Liskov Substitution)**: Các lớp kế thừa từ `Player` như `Boss`, `Pet`, `Bot`, `NonInteractiveNPC` không được ép kiểu (`instanceof`) tràn lan trong `Zone.java`. Hành vi khác biệt phải được đa hình hóa qua phương thức ghi đè (`@Override`).
   * **I (Interface Segregation)**: Tách nhỏ các interface lớn thành các hợp đồng chuyên biệt (`ICombatant`, `ITradeable`, `IPersistable`).
   * **D (Dependency Inversion)**: Các lớp xử lý nghiệp vụ cấp cao không phụ thuộc chặt vào cách lưu trữ JSON chuỗi thủ công của `org.json.simple.JSONArray`.

---

### Thói quen 2: Tái cấu trúc (Refactoring) Liên tục theo Chiến lược "Strangler Fig"
Không bao giờ dừng dự án 1 tháng chỉ để viết lại code. Áp dụng quy trình 3 bước mỗi ngày:

1. **Tách `Controller.java` thành các Packet Command Handlers**:
   * Mỗi khi bạn cần sửa hoặc kiểm tra một `case` trong `Controller.onMessage(ISession, Message)` (ví dụ `case -100:` cho Chợ Ký Gửi, `case -86:` cho Giao dịch, `case 127:` cho Radar):
   * Tạo interface `IPacketHandler { void handle(MySession session, Player player, Message msg) throws IOException; }`.
   * Chuyển toàn bộ đoạn code trong `case` đó sang lớp riêng (ví dụ `nro.models.network.handlers.ConsignShopPacketHandler`) và đăng ký vào `Map<Byte, IPacketHandler>`.
2. **Chia nhỏ `Service.java` theo Miền nghiệp vụ (Domain Services)**:
   * Khi sửa bất kỳ hàm nào trong `Service.java`, hãy di chuyển hàm đó sang dịch vụ con tương ứng: `ChatNotifyService` (thông báo, chat), `MapSyncService` (gửi vị trí, xuất hiện/biến mất), `AccountSecurityService` (đổi mật khẩu, khóa tài khoản).
3. **Chuẩn hóa DTO & JSON Serialization**:
   * Thay thế dần việc ghép chuỗi `JSONArray.add()` theo chỉ số cứng (`dataArray.get(0)`, `dataArray.get(1)`) trong `PlayerDAO.java` và `MrBlue.java` bằng đối tượng POJO/DTO sử dụng thư viện `Gson` (đã có sẵn `gson-2.8.2.jar` trong thư mục `lib/`).

---

### Thói quen 3: Tối ưu Hiệu suất & Xử lý Đồng thời (Concurrency & Performance)
1. **Bộ giới hạn tốc độ gói tin (Token Bucket Rate Limiter)**:
   * Gắn một bộ đếm **Token Bucket** vào mỗi `MySession`.
   * Phân loại giới hạn theo nhóm lệnh: Nhóm di chuyển (`cmd = -7`) tối đa 15 gói/giây; Nhóm tấn công/nhặt đồ tối đa 8 gói/giây; Nhóm giao dịch/mua bán/ký gửi (`cmd = -100, -86`) tối đa 2 gói/giây. Nếu vượt ngưỡng, từ chối xử lý ngay tại `Collector` trước khi chạm tới logic game.
2. **Đồng bộ Vòng lặp Thế giới (Single-Tick Authority) & Xóa bỏ `new Thread()` tự phát**:
   * **Cấm tuyệt đối** việc gọi `new Thread(() -> { Thread.sleep(...); }).start()` bên trong logic kỹ năng (`SkillService`), tạo đệ tử (`PetService`) hay gọi Bot (`Input`). Mọi tác vụ hẹn giờ trễ phải đẩy vào một `ScheduledExecutorService` dùng chung hoặc xử lý theo biến thời gian (`lastTime...`) trong vòng lặp `Zone.update()`.
   * Không để cả `Player.run()` và `Zone.update()` cùng gọi `player.update()`. Gom toàn bộ cập nhật trạng thái thực thể trong khu vực về duy nhất luồng của `Zone`.
   * Mọi thao tác mua bán trên `ConsignShopService` phải khóa nguyên tử (`synchronized (consignItem)` hoặc `AtomicBoolean.compareAndSet`) để triệt tiêu lỗi Race Condition gây dup đồ.
3. **Chia lưới không gian AOI (Area of Interest - 2D Grid Partitioning)**:
   * Đối với các bản đồ rộng hoặc đông người chơi/quái vật: Chia chiều ngang bản đồ (`mapWidth`) thành các ô lưới (Grid Cells, ví dụ mỗi ô `240px x 240px`).
   * Khi nhân vật di chuyển hoặc tung chiêu, chỉ phát gói tin (Broadcast) và kiểm tra va chạm với các thực thể nằm trong ô hiện tại và các ô lân cận thay vì duyệt toàn bộ `zone.getPlayers()`.
4. **Cơ chế Lưu trữ Write-Behind Caching (Dirty Flags & Upsert)**:
   * **Tuyệt đối cấm** mẫu lệnh `DELETE FROM table` rồi `INSERT` lại toàn bộ như trong `ConsignShopManager.save()`.
   * Gắn cờ `isDirty = true` cho dữ liệu thay đổi trên RAM (`ConsignItem`, `Clan`, `Player`). Một luồng nền (`WriteBehindPersistenceService`) sẽ định kỳ (mỗi 30–60 giây) gom các bản ghi có `isDirty == true` và thực hiện `INSERT ... ON DUPLICATE KEY UPDATE` theo lô (`executeBatch()`).

---

### Thói quen 4: Áp dụng Design Patterns (GoF) Đúng Bài Toán
Chỉ áp dụng Design Pattern khi nó trực tiếp giải quyết sự phình to hoặc rối rắm của code hiện tại:

| Mẫu thiết kế (Pattern) | Bài toán thực tế trong `NgocRongBaKhi` | Cách áp dụng cụ thể |
| :--- | :--- | :--- |
| **Command Pattern** | `Controller.java` (952 dòng) và `Input.java` (759 dòng) dùng `switch-case` khổng lồ. | Mỗi mã lệnh mạng (`cmd`) hoặc mỗi loại ô nhập liệu (`typeInput`) là một lớp thực thi `ICommand` độc lập, hỗ trợ gắn sẵn kiểm tra điều kiện (đã kích hoạt, đang giao dịch, khóa bảo vệ) qua Decorator/Middleware. |
| **Strategy Pattern** | `UseItem.java`, `GiftBoxHandler.java` (1.071 dòng), `SkillService.java` (1.330 dòng). | Mỗi loại kỹ năng hoặc nhóm vật phẩm là một chiến lược (`ISkillCastStrategy`, `IItemUseStrategy`). Thêm kỹ năng mới chỉ cần tạo 1 class mới. |
| **Factory + Builder** | `BossesData.java` (2.762 dòng) và `NpcFactory.java` (750 dòng). | Đọc thông số Boss từ JSON/Database và dùng `BossFactory` khởi tạo đối tượng `Boss` theo `bossId` thay vì khai báo hàng trăm biến `public static final BossData` trong code. |
| **Observer / Event Bus** | Khi giết 1 con quái/Boss, code hiện tại gọi chéo trực tiếp sang `TaskService`, `AchievementService`, `RewardService`, `ClanService`, `EventService`. | Phát ra một sự kiện `MonsterKilledEvent(player, mob)`. Các hệ thống Nhiệm vụ, Thành tựu, Sự kiện tự đăng ký lắng nghe (Subscribe) giúp tách rời hoàn toàn sự phụ thuộc chéo. |

---

### Thói quen 5: Tài liệu hóa & Quản lý Ngữ cảnh (Documentation & AI Context)
1. **Duy trì tệp `AGENTS.md` ở thư mục gốc**:
   * Cập nhật tệp này mỗi khi thay đổi kiến trúc mạng, cơ chế lưu DB hoặc thêm quy tắc mới. Khi dùng AI để code, luôn đính kèm `@AGENTS.md` vào ngữ cảnh để AI viết code tuân thủ đúng kiến trúc mới thay vì bắt chước code cũ nhiều lỗi.
2. **Sổ đăng ký Mã lệnh Mạng (`docs/PACKET_PROTOCOL.md`)**:
   * Mọi `cmd` (byte) trao đổi giữa Client 2D và Server phải được ghi chú rõ: Mã lệnh, Chiều gửi (Client->Server hay Server->Client), Cấu trúc byte (`readByte`, `readShort`, `readInt`, `readUTF`) và Tần suất tối đa cho phép.
3. **Nhật ký Quyết định Kiến trúc (ADR - Architecture Decision Records)**:
   * Khi thay đổi cấu trúc bảng SQL hoặc luồng đồng bộ giữa `WebTeamobi` và `NroBaKhi`, ghi chú ngắn gọn vào đầu file hoặc thư mục `docs/` để tránh việc sửa code PHP làm ghi đè dữ liệu trên RAM của Java Server.

---

## 3. Quy Trình Kiểm Tra Trước Khi Commit / Build (Pre-Commit Checklist)

Trước khi bấm `build.bat` xuất file `NroBaKhi.jar` hoặc đưa code lên VPS, hãy tự kiểm tra 6 câu hỏi sau:
- [ ] **1. Hàm vừa viết có vượt quá 40 dòng hoặc có Magic Number nào chưa đặt hằng số không?**
- [ ] **2. Có câu lệnh `new Thread()` thủ công nào vừa được thêm vào không?** (Nếu có -> đổi sang `ScheduledExecutorService` hoặc xử lý trong Tick của `Zone`).
- [ ] **3. Thao tác cộng/trừ Vàng, Ngọc, Vật phẩm vừa viết có khả năng bị gọi đồng thời từ 2 luồng không?** (Đã kiểm tra khóa `synchronized` và điều kiện số lượng `quantity > 0` chưa?).
- [ ] **4. Có câu lệnh SQL nào chạy trực tiếp trên luồng xử lý gói tin chính làm nghẽn mạng không?** (Nếu là thao tác lưu định kỳ -> đẩy sang Write-Behind Queue).
- [ ] **5. Nếu sửa từ phía Web PHP (`WebTeamobi`), nhân vật đang Online trong game có bị ghi đè mất dữ liệu khi thoát game không?**
- [ ] **6. Nếu sửa một trong 8 file "God Class", mình đã tách được ít nhất 1 hàm/nhóm logic ra class riêng chưa?**
