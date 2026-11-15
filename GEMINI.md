# GEMINI.md — Bộ Quy Tắc Thực Thi Bắt Buộc Cho Antigravity / AI Agent (NgocRongBaKhi)

> **CHỈ THỊ TỐI CAO (HARD CONSTRAINTS):**
> Bạn đang làm việc trên dự án **NgocRongBaKhi** (`ToolCaiGameV3/Source/NroBaKhi` - Java Server và `ToolCaiGameV3/Source/WebTeamobi` - PHP Web/Admin).
> Mọi hành động của bạn trong mọi lượt (turn) PHẢI tuân thủ nghiêm ngặt **20 Điều Khoản Thực Thi & Chốt Chặn Kỹ Thuật Toàn Diện** dưới đây. Không có ngoại lệ.

---

## PHẦN I: 5 QUY TẮC QUẢN LÝ NGỮ CẢNH & PHẠM VI TASK

### 1. Quy Tắc Nhớ Ngữ Cảnh & Cấm Quét Lại Dự Án (Zero-Scan Memory Protocol)
Trước khi sử dụng bất kỳ lệnh tìm kiếm (`grep`, `find`, `ls`) hay đọc toàn bộ file code lớn nào, bạn **BẮT BUỘC** phải tra cứu 4 tệp ngữ cảnh dưới đây theo thứ tự:
1. **`ToolCaiGameV3/task_memory.md`** *(Đọc đầu tiên và cập nhật cuối cùng ở mỗi task)*: Kiểm tra trạng thái công việc hiện tại, những file vừa sửa ở các phiên trước, và các quyết định kỹ thuật đã chốt để không làm lại hoặc phá hỏng code cũ.
2. **`ToolCaiGameV3/codebase_map.md`**: Tra cứu vị trí chính xác của 543 file Java, 33 file PHP và **khoảng dòng (`L...–L...`)** của từng hàm trong các file lớn (`Service.java`, `NPoint.java`, `Player.java`, `TaskService.java`, `SkillService.java`, `RewardService.java`, `InventoryService.java`, `ItemService.java`, `ChangeMapService.java`, `Boss.java`).
   * **CẤM** đọc từ dòng 1 đến hết file đối với các file trên 500 dòng. Chỉ được phép đọc đúng khoảng dòng (`StartLine`..`EndLine`) đã ghi trong `codebase_map.md`.
   * **Mục 0 (Ma trận tính năng)**: Tra cứu nhanh từ khóa tiếng Việt dân dã (*"rơi đồ"*, *"đập đồ"*, *"đệ tử"*, *"dùng bùa"*, *"boss"*, *"giao dịch"*) sang file và method đích.
3. **`ToolCaiGameV3/GAME_CONSTANTS_INDEX.md`**: Tra cứu toàn bộ ID Hằng số (251 Option ID, 150 Boss ID, 158 Map ID, 242 NPC/Menu ID, 1.225 Item Template).
   * **CẤM** đọc lại `ConstTask.java` (6.356 dòng), `BossesData.java` (2.762 dòng) hay `ConstItem.java` (1.264 dòng).
4. **`ToolCaiGameV3/DB_JSON_SCHEMA.md`**: Tra cứu cấu trúc 43 bảng SQL trong `nrobakhi.sql` và **thứ tự index `[0]`, `[1]`, `[2]`...** của toàn bộ 39 cột mảng JSON trong bảng `player` và `clan`.
   * **CẤM** đọc lại `nrobakhi.sql` (12MB), `PlayerDAO.java` hay `MrBlue.java` chỉ để tìm xem một cột JSON lưu giá trị gì ở vị trí nào.
5. **`ToolCaiGameV3/PACKET_PROTOCOL.md`**: Tra cứu toàn bộ 58 mã lệnh mạng (`cmd`) trong `Controller.java`, cấu trúc byte đọc/ghi (`readByte`, `readShort`, `readInt`, `readUTF`) và dịch vụ đích.
6. **`ToolCaiGameV3/WORKFLOW_RECIPES.md`**: Tuân thủ đúng công thức checklist từng bước khi thêm Boss, NPC, UseItem, Admin Command, Hot-Reload.

### 2. Quy Tắc Cấm Bịa Task & Cấm Tự Ý Mở Rộng Phạm Vi (Zero-Invention & Strict Scope)
1. **Chỉ làm đúng 100% yêu cầu được giao (Exact Task Execution)**:
   * Người dùng yêu cầu sửa hàm X hoặc thêm tính năng Y -> Chỉ chạm vào đúng các dòng code phục vụ cho X hoặc Y.
   * **TUYỆT ĐỐI CẤM** tự ý đẻ thêm "task phụ", tự ý đổi tên biến/hàm không liên quan, tự ý format lại toàn bộ file làm nhiễu `git diff`, hoặc tự ý xóa code đang bị comment nếu không được yêu cầu.
2. **Không suy diễn tính năng (No Hallucinated Features)**:
   * Không tự bịa ra bảng SQL mới, cột mới, mã lệnh `cmd` mới, hoặc ID vật phẩm/option không tồn tại trong database.
   * Nếu thiếu thông tin bắt buộc (ví dụ: người dùng bảo *"thêm hộp quà mới"* nhưng chưa cho `item_template.id` hoặc danh sách phần thưởng), hãy **hỏi ngắn gọn 1 câu duy nhất** hoặc tra cứu ID có sẵn rồi thực hiện, không tự bịa số ID ngẫu nhiên.

### 3. Quy Tắc "Thực Thi Trước — Không Thuyết Giáo" (Execution Over Lecturing)
1. **Cấm từ chối khéo hoặc đề xuất giải pháp lý thuyết thay vì làm task**:
   * Khi người dùng giao một task trên nền code cũ của NRO (ví dụ: *"Thêm chỉ số cho cải trang trong NPoint.java"* hoặc *"Thêm chức năng vào Web Admin player.php"*), bạn **PHẢI viết code hoàn thành trọn vẹn task đó ngay trên cấu trúc hiện tại của dự án**.
   * **CẤM** trả lời kiểu: *"Cách làm hiện tại của dự án chưa tối ưu, tôi đề xuất bạn nên chuyển sang dùng Spring Boot / Hibernate / microservices thay vì làm việc này..."*.
2. **Làm cho chạy được ngay (Working Code First)**:
   * Code viết ra phải biên dịch được ngay với `compile_check.bat` và JDK 21 hiện hành.

### 4. Các Ràng Buộc An Toàn Đa Luồng & Database (Safety Guardrails)
1. **An toàn Đa luồng (Thread Safety)**:
   * Không bao giờ tạo `new Thread(() -> { Thread.sleep(...); }).start()` mới trong các hàm kỹ năng, pet hay sự kiện. Sử dụng `ScheduledExecutorService` hoặc biến đếm thời gian (`lastTime...`) trong nhịp `update()`.
   * Mọi thao tác mua bán, nhận tiền, hủy đồ trong `Trade`, `ShopService`, `ConsignShopService`, `InventoryService` phải được bảo vệ bởi khối `synchronized` trên chính thực thể (`Player`, `Item`, `ConsignItem`) để chống Bug Dup đồ / Dup tiền.
2. **Nguyên vẹn Dữ liệu Database**:
   * Tuyệt đối không chạy lệnh `DELETE FROM ...` trên các bảng dữ liệu đang vận hành trừ khi có điều kiện `WHERE id = ...` xác định.
   * Khi lưu dữ liệu dạng JSON vào `player`, bắt buộc phải kiểm tra mảng đủ số lượng phần tử (`size >= N`) trước khi gọi `get(index)` hoặc `set(index, value)`.

### 5. Quy Tắc Tái Cấu Trúc Bằng Facade & Handler (Anti-God-Class Refactoring Rule)
* Khi thêm logic cho `UseItem.java`, bắt buộc phải đưa vào 5 lớp Handler con trong package `nro.models.services_func.useitem`:
  * `ConsumableHandler.java`: Bùa, đậu thần, nước phép, thẻ cào.
  * `GiftBoxHandler.java`: Hộp quà, rương gỗ, capsule sự kiện.
  * `MiniPetHandler.java`: Thú cưng theo sau.
  * `SkillAndPetHandler.java`: Kỹ năng đệ tử, cải trang.
  * `TeleportAndRadarHandler.java`: Capsule bay map, rada dò tìm.

---

## PHẦN II: 7 CHỐT CHẶN KỸ THUẬT & KIỂM TRA TỰ ĐỘNG

### 6. Tự Động Kiểm Tra Biên Dịch (Autonomous Compile Check)
* Sau khi chỉnh sửa code Java, AI **BẮT BUỘC** phải tự động chạy script kiểm tra biên dịch ngầm:
  `cmd /c "cd /d ToolCaiGameV3\Source\NroBaKhi && compile_check.bat"`
* Chỉ thông báo hoàn thành cho người dùng khi lệnh trên trả về `[COMPILE_SUCCESS]`. Nếu phát sinh lỗi biên dịch, phải tự động sửa ngay trong phiên làm việc.

### 7. Ghi Nhận Bản Vá SQL (SQL Migration Tracking)
* Bất kỳ tính năng mới nào cần thêm dòng dữ liệu (`INSERT INTO item_template`, `INSERT INTO item_option_template`, `ALTER TABLE...`), AI **BẮT BUỘC** phải ghi lại câu lệnh SQL vào tệp:
  `ToolCaiGameV3/SQL_PATCHES.md`
  kèm ngày giờ, mục đích và lệnh rollback (nếu có).

### 8. Cập Nhật Bộ Nhớ Thực Thi (Persistent Memory Protocol)
* Sau mỗi task hoàn thành, AI **BẮT BUỘC** phải cập nhật lại:
  `ToolCaiGameV3/task_memory.md` (chuyển task sang mục `Completed Tasks Log`, ghi rõ file đã sửa và lưu ý kiến trúc mới).

### 9. Cấm Nuốt Ngoại Lệ (No Silent Catch & Root-Cause Fix)
* **TUYỆT ĐỐI CẤM** bọc `try { ... } catch (Exception ignored) {}` để che giấu lỗi.
* Phải tìm nguyên nhân gốc rễ và kiểm tra điều kiện an toàn (`if (player == null || player.zone == null) return;`).

### 10. Danh Sách Trắng 14 Thư Viện `.jar` (Strict Classpath Whitelist)
* Chỉ dùng JDK 21 chuẩn và đúng 14 file `.jar` trong `ToolCaiGameV3/Source/NroBaKhi/lib/`:
  `HikariCP-5.1.0.jar`, `apache-commons-lang.jar` (2.x), `gson-2.8.2.jar`, `json-simple-1.1.jar`, `java-json.jar`, `lombok.jar`, `mysql-connector-java8-5.1.23.jar`, `bson-5.1.3.jar`, `mongodb-driver-core-5.1.3.jar`, `mongodb-driver-sync-5.1.3.jar`, `log4j-1.2.17.jar`, `slf4j-api-2.0.0-alpha1.jar`, `slf4j-simple-2.0.0-alpha1.jar`.
* CẤM import `org.apache.commons.lang3.*` hoặc thư viện ngoài.

### 11. Bảo Toàn Giao Thức Nhị Phân Của Client 2D (Immutable Binary Packet Contract)
* Cấm đổi kiểu dữ liệu (`writeByte` $\leftrightarrow$ `writeShort` $\leftrightarrow$ `writeInt` $\leftrightarrow$ `writeUTF`) hay đảo thứ tự ghi trong gói tin `Message`.
* Bảo toàn phân nhánh theo phiên bản `session.version`.

### 12. Bảo Toàn Chuẩn UTF-8 (Strict UTF-8 Encoding Without BOM)
* Mọi file ghi ra phải chuẩn UTF-8 không có BOM để tránh lỗi `\ufeff` khi biên dịch với `javac`.

---

## PHẦN III: 8 QUY TẮC KỸ THUẬT CHUYÊN SÂU CHO SERVER GAME NGỌC RỒNG & WEB ADMIN

### 13. Cấm Cắt Cụt Code & Ghi Đè Mất Hàm (Anti-Lazy Truncation Rule)
* **CẤM** viết các comment rút gọn lười biếng: `// ... giữ nguyên ...`, `// existing code`, `/* ... */`.
* Với file trên 150 dòng, bắt buộc sửa theo khối dòng (Surgical Edit), tuyệt đối không ghi đè toàn bộ file làm mất các method khác.

### 14. Hợp Đồng Đồng Bộ Cặp File Ràng Buộc (Paired Files Synchronization Contract)
* Khi sửa một file nguồn thuộc tầng lưu trữ hoặc khởi tạo, bắt buộc đồng bộ file đối xứng:
  * `PlayerDAO.java` $\leftrightarrow$ `MrBlue.java` & `WebTeamobi/admin/player.php` & `DB_JSON_SCHEMA.md` (39 cột JSON).
  * `BossesData.java` $\leftrightarrow$ `BossID.java` & `BossManager.java` (switch case `createBoss`).
  * `ConstNpc.java` $\leftrightarrow$ `NpcFactory.java` (`createNPC`).
  * `ItemTime.java` $\leftrightarrow$ `ItemTimeService.java` & `PlayerDAO.java` & `MrBlue.java`.

### 15. Kiểm Soát Biên & Chống Tràn Số Âm (Integer Overflow & Value Boundary Guard)
* Vàng (`gold`), Tiềm năng/Sức mạnh (`power`, `tiemNang`), Máu/Ki/Sát thương (`hp`, `ki`, `dame`) rất dễ vượt qua ngưỡng $2.147.483.647$ (`Integer.MAX_VALUE`).
* Bắt buộc kiểm tra trần `Inventory.LIMIT_GOLD` (2 tỷ) trước khi cộng dồn, tránh tràn sang số âm (`Negative Gold Bug`). Dùng kiểu `long` cho tích lũy tiềm năng/sức mạnh. Mọi hàm trừ tiền/ngọc phải kiểm tra `current >= cost`.

### 16. Độc Quyền Bộ Nhớ RAM & An Toàn Thao Tác Web/DB (RAM Precedence & Offline Mutation Guard)
* Dữ liệu nhân vật Online nằm hoàn toàn trên RAM Java Server (`Player`). Khi thoát map hoặc thoát game, RAM sẽ ghi đè ngược xuống MySQL (`PlayerDAO.updatePlayer`).
* Khi sửa dữ liệu qua Web Admin (`WebTeamobi`) hoặc SQL: **BẮT BUỘC** kiểm tra người chơi phải Offline (`Client.gI().getPlayerByUser(userId) == null`). Nếu đang Online, phải kick ra trước hoặc nạp qua bảng trung gian có cơ chế polling (`ShopDAO`, `GiftCodeManager`). Tuyệt đối không UPDATE trực tiếp khi player đang Online.

### 17. An Toàn Duyệt Danh Sách Vùng & Chống Crash Vòng Lặp Game (Concurrent Zone Iteration & Memory Guard)
* Trong `Zone.java`, `Map.java`, các danh sách `players`, `mobs`, `items` liên tục thêm/bớt theo nhịp game loop.
* **CẤM** duyệt trực tiếp bằng `for (Player pl : this.players)` vì sẽ ném `ConcurrentModificationException`.
* Bắt buộc duyệt ngược: `for (int i = this.players.size() - 1; i >= 0; i--)` kết hợp check `null`, hoặc duyệt trên bản sao chép.
* Khi thực thể rời map hoặc hủy, phải dọn sạch tham chiếu (`zone.removePlayer`, `dispose()`) để tránh rò rỉ RAM (OOM).

### 18. Tính Toàn Vẹn Khởi Tạo Vật Phẩm & Option Template (Item & Option Integrity Contract)
* Khi sinh item mới (`createNewItem`, rơi đồ boss, quà tặng), `item.itemOptions` **TUYỆT ĐỐI KHÔNG ĐƯỢC NULL** (khởi tạo `new ArrayList<>()` nếu rỗng).
* Tuân thủ ID option chuẩn: Option `93` (HSD ngày), Option `73` (HSD giờ), Option `30` (Không thể giao dịch), Option `86` (Giao dịch sau X ngày), Option `107`/`102` (Sao pha lê). Tra cứu chính xác trong `GAME_CONSTANTS_INDEX.md`.

### 19. Tính Bất Khả Xâm Phạm Của Giao Dịch & Chống Bug Dup (Transaction Atomicity & Anti-Dup)
* Mọi luồng giao dịch (`Trade`), ký gửi (`ConsignShopService`), rút/chuyển đồ phải tuân thủ nguyên tắc **Nguyên tử (Atomic)**: Chỉ trừ tài sản khi bên kia đã nhận thành công 100%. Nếu có lỗi mạng hoặc hủy, hoàn trả nguyên trạng.
* Bọc khối `synchronized` trên chính thực thể (`Player`, `Item`, `ConsignItem`). Kiểm tra điều kiện `!player.isDie()`, `player.zone != null`, `!player.baovetaikhoan` và cấm thực hiện 2 giao dịch đồng thời (`!player.isTrading`).

### 20. Tách Biệt Cấu Hình & Cấm Tuyệt Đối Hardcode (Zero-Hardcode Configuration)
* Tuyệt đối không viết cứng IP, Port `14445`, tài khoản/mật khẩu MySQL, MongoDB trong file `.java` hoặc `.php`.
* Nạp cấu hình Server Java qua `Config.properties` (`Manager.java`), Web qua `WebTeamobi/config.php`. Không để tài khoản test mặc định trong code triển khai.
