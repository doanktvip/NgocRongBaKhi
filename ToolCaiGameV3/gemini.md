# GEMINI.md — Bộ Quy Tắc Thực Thi Bắt Buộc Cho Antigravity / AI Agent (NgocRongBaKhi)

> **CHỈ THỊ TỐI CAO (HARD CONSTRAINTS):**
> Bạn đang làm việc trên dự án **NgocRongBaKhi** (`ToolCaiGameV3/Source/NroBaKhi` - Java Server và `ToolCaiGameV3/Source/WebTeamobi` - PHP Web/Admin).
> Mọi hành động của bạn trong mọi lượt (turn) PHẢI tuân thủ nghiêm ngặt **12 Điều Khoản Thực Thi & Chốt Chặn Kỹ Thuật** dưới đây. Không có ngoại lệ.

---

## PHẦN I: 5 QUY TẮC QUẢN LÝ NGỮ CẢNH & PHẠM VI TASK

### 1. Quy Tắc Nhớ Ngữ Cảnh & Cấm Quét Lại Dự Án (Zero-Scan Memory Protocol)
Trước khi sử dụng bất kỳ lệnh tìm kiếm (`grep`, `find`, `ls`) hay đọc toàn bộ file code lớn nào, bạn **BẮT BUỘC** phải tra cứu 4 tệp ngữ cảnh dưới đây theo thứ tự:
1. **`TASK_MEMORY.md`** *(Đọc đầu tiên và cập nhật cuối cùng ở mỗi task)*: Kiểm tra trạng thái công việc hiện tại, những file vừa sửa ở các phiên trước, và các quyết định kỹ thuật đã chốt để không làm lại hoặc phá hỏng code cũ.
2. **`CODEBASE_MAP.md`**: Tra cứu vị trí chính xác của 543 file Java, 33 file PHP và **khoảng dòng (`L...–L...`)** của từng hàm trong các file lớn (`Service.java`, `NPoint.java`, `Player.java`, `TaskService.java`, `SkillService.java`, `RewardService.java`, `InventoryService.java`, `ItemService.java`, `ChangeMapService.java`, `Boss.java`).
   * **CẤM** đọc từ dòng 1 đến hết file đối với các file trên 500 dòng. Chỉ được phép đọc đúng khoảng dòng (`StartLine`..`EndLine`) đã ghi trong `CODEBASE_MAP.md`.
3. **`DB_JSON_SCHEMA.md`**: Tra cứu cấu trúc 43 bảng SQL trong `nrobakhi.sql` và **thứ tự index `[0]`, `[1]`, `[2]`...** của toàn bộ 39 cột mảng JSON trong bảng `player` và `clan`.
   * **CẤM** đọc lại `nrobakhi.sql` (12MB), `PlayerDAO.java` hay `MrBlue.java` chỉ để tìm xem một cột JSON lưu giá trị gì ở vị trí nào.
4. **`PACKET_PROTOCOL.md`**: Tra cứu toàn bộ 58 mã lệnh mạng (`cmd`) trong `Controller.java`, cấu trúc byte đọc/ghi (`readByte`, `readShort`, `readInt`, `readUTF`) và dịch vụ đích.

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
   * Code viết ra phải biên dịch được ngay với Ant (`build.xml` / `build.bat`) và JDK hiện hành (hỗ trợ `Thread.ofVirtual()`).

### 4. Các Ràng Buộc An Toàn Đa Luồng & Database (Safety Guardrails)
1. **An toàn Đa luồng (Thread Safety)**:
   * Không bao giờ tạo `new Thread(() -> { Thread.sleep(...); }).start()` mới trong các hàm kỹ năng, pet hay sự kiện. Sử dụng `ScheduledExecutorService` hoặc biến đếm thời gian (`lastTime...`) trong nhịp `update()`.
   * Mọi thao tác mua bán, nhận tiền, hủy đồ trong `ConsignShopService` hoặc `Trade` phải có khóa đồng bộ `synchronized` và kiểm tra `quantity > 0`.
2. **An toàn Cơ sở dữ liệu (Database Safety)**:
   * Luôn dùng `try (Connection con = LocalManager.getConnection(); PreparedStatement ps = ...)` để tự động đóng kết nối trả về HikariCP Pool.
   * Không bao giờ dùng lệnh `DELETE FROM <table_name>` xóa trắng bảng rồi `INSERT` lại.
3. **Kiểm tra Biên & Chống Gian Lận (Input Validation)**:
   * Khi đọc gói tin từ `_msg.reader()`, luôn kiểm tra giới hạn mảng (`index >= 0 && index < list.size()`), số lượng dương (`quantity > 0`), trạng thái sống (`!player.isDie()`), không trong giao dịch (`!TransactionService.gI().check(player)`), và đã mở khóa bảo vệ (`!player.baovetaikhoan`).

### 5. Giao Thức Cập Nhật Bộ Nhớ Sau Mỗi Task (Post-Task Memory Sync)
Ngay sau khi hoàn thành việc sửa code cho một task:
1. Mở tệp **`TASK_MEMORY.md`** và ghi thêm 1 mục ngắn gọn (dưới 5 dòng) vào phần `Lịch Sử Thực Thi Gần Nhất` (Task vừa hoàn thành, file và số dòng đã sửa).
2. Nếu có thêm hàm mới vào file lớn hoặc thêm cột/bảng mới, cập nhật dòng tương ứng vào `CODEBASE_MAP.md` hoặc `DB_JSON_SCHEMA.md`.

---

## PHẦN II: 7 CHỐT CHẶN CHỐNG CÁC "BỆNH KINH NIÊN" CỦA AI AGENT

### 6. Cấm Cắt Cụ Code & Ghi Đè Mất Hàm (Anti-Lazy Truncation Rule)
1. **Cấm tuyệt đối các dòng comment rút gọn**:
   * **NGHIÊM CẤM** viết các dòng như `// ... các phương thức khác giữ nguyên ...`, `// existing code here`, `// TODO: implement later`, hoặc `/* ... */` khi chỉnh sửa bất kỳ tệp nào.
2. **Chỉ sửa theo khối dòng (Surgical Diff / Partial Edit)**:
   * Đối với mọi tệp có sẵn trên **150 dòng**, **CẤM** dùng lệnh ghi đè toàn bộ tệp (`Overwrite = true`). Bắt buộc sử dụng công cụ chỉnh sửa từng phần (Replace/Edit theo khoảng dòng) để bảo toàn 100% các phương thức khác trong tệp.
   * Trước khi kết thúc task, kiểm tra số dòng (`wc -l`) của tệp trước và sau khi sửa để đảm bảo không vô tình xóa mất bất kỳ hàm nào.

### 7. Hợp Đồng Đồng Bộ Cặp File Ràng Buộc (Paired Files Synchronization Contract)
Trong kiến trúc `NgocRongBaKhi`, dữ liệu được tuần tự hóa và chia sẻ giữa nhiều tầng. Khi bạn sửa **File Nguồn (Cột A)**, bạn **BẮT BUỘC** phải kiểm tra và cập nhật đồng bộ **File Đối Xứng (Cột B)**:

| Nếu bạn sửa File Nguồn (Cột A) | BẮT BUỘC kiểm tra & đồng bộ File Đối Xứng (Cột B) | Lý do kỹ thuật |
| :--- | :--- | :--- |
| **`PlayerDAO.java`** (`createNewPlayer`, `updatePlayer`) | $\leftrightarrow$ **`MrBlue.java`** (`loadPlayer`) & **`WebTeamobi/admin/player.php`** & **`DB_JSON_SCHEMA.md`** | Mọi thay đổi về thứ tự hoặc số lượng phần tử trong 39 cột mảng JSON (`data_point`, `data_item_time`, `data_item_event`, `pet`...) nếu chỉ sửa ở `PlayerDAO` mà quên sửa ở `MrBlue` sẽ gây lệch Index làm hỏng dữ liệu nhân vật khi đăng nhập. |
| **`ItemTime.java`** (Thêm/bớt hiệu ứng thời gian) | $\leftrightarrow$ **`ItemTimeService.java`**, **`PlayerDAO.java`** (`L600–L632`) & **`MrBlue.java`** (`L580–L685`) | Đảm bảo thời gian còn lại của vật phẩm bổ trợ được gửi đúng Icon xuống Client, lưu đúng vị trí mảng và khôi phục đúng khi đăng nhập lại. |
| **`BossesData.java`** / Class Boss mới | $\leftrightarrow$ **`BossID.java`** & **`BossManager.java`** (`loadBoss()`, `createBoss()`) | Nếu tạo `BossData` mới mà không khai báo hằng số trong `BossID.java` và không đăng ký trong `switch (bossId)` của `BossManager.createBoss()`, Boss sẽ không bao giờ xuất hiện trong game. |
| **`ConstNpc.java`** / Class NPC mới trong `npc_list/` | $\leftrightarrow$ **`NpcFactory.java`** (`createNPC()` `L106`) | Mọi NPC mới phải được ánh xạ `tempId` trong `NpcFactory.createNPC()` thì mới tương tác mở menu được. |
| **`UseItem.java`** (Thêm vật phẩm sử dụng mới) | $\leftrightarrow$ 5 lớp Handler trong **`nro.models.services_func.useitem.*`** (`GiftBoxHandler`, `ConsumableHandler`, `SkillAndPetHandler`, `TeleportAndRadarHandler`, `MiniPetHandler`) | `UseItem.java` đã được tách nhỏ, mọi logic xử lý chi tiết phải đặt đúng vào Handler con tương ứng theo loại vật phẩm. |
| **`WebTeamobi/admin/*.php`** (Sửa tiền, đồ, chỉ số nhân vật từ Web) | $\leftrightarrow$ Kiểm tra trạng thái Online trong **`Client.gI().getPlayerByUser()`** hoặc bảng Reload Request (`ShopDAO` / `GiftCodeManager`) | Tránh lỗi Web cập nhật trực tiếp vào MySQL trong khi nhân vật đang Online trên RAM của Java Server, dẫn đến bị ghi đè mất dữ liệu khi nhân vật thoát game. |

### 8. Cấm Che Giấu Lỗi Bằng `try-catch` Rỗng (Anti-Symptom Masking Rule)
1. **Cấm nuốt ngoại lệ (No Silent Catch)**:
   * Khi người dùng yêu cầu sửa lỗi (`NullPointerException`, `IndexOutOfBoundsException`, `SQLException`, `ConcurrentModificationException`...), **TUYỆT ĐỐI CẤM** xử lý theo kiểu bọc `try { ... } catch (Exception ignored) {}` để tắt thông báo lỗi.
2. **Giải quyết tận gốc (Root-Cause Fix)**:
   * Phải truy tìm nguyên nhân tại sao biến đó bị `null`, tại sao danh sách bị truy cập đồng thời hoặc tại sao mảng JSON thiếu phần tử.
   * Sử dụng kiểm tra điều kiện tường minh (`if (player == null || player.zone == null) return;`) ở đầu hàm và nếu có khối `catch`, bắt buộc phải ghi log qua `Logger.logException(TenClass.class, e, "Mô tả ngữ cảnh lỗi")`.

### 9. Danh Sách Trắng 14 Thư Viện `.jar` & Cấm Ảo Giác API (Strict Classpath Whitelist)
Dự án `NroBaKhi` biên dịch bằng **Apache Ant (`build.xml`)**, KHÔNG sử dụng Maven hay Gradle. Bạn **CHỈ ĐƯỢC PHÉP** `import` các lớp thuộc JDK chuẩn và đúng **14 thư viện `.jar` có sẵn** trong thư mục `ToolCaiGameV3/Source/NroBaKhi/lib/`:
1. `HikariCP-5.1.0.jar` (`com.zaxxer.hikari.*`)
2. `apache-commons-lang.jar` (`org.apache.commons.lang.*` — **Chú ý: Đây là bản 2.x, CẤM import `org.apache.commons.lang3.*`**)
3. `gson-2.8.2.jar` (`com.google.gson.*`)
4. `json-simple-1.1.jar` (`org.json.simple.*`)
5. `java-json.jar` (`org.json.*`)
6. `lombok.jar` (`lombok.*`)
7. `mysql-connector-java8-5.1.23.jar` (`com.mysql.jdbc.*`, `java.sql.*`)
8. `bson-5.1.3.jar`, `mongodb-driver-core-5.1.3.jar`, `mongodb-driver-sync-5.1.3.jar` (`org.bson.*`, `com.mongodb.*`)
9. `log4j-1.2.17.jar`, `slf4j-api-2.0.0-alpha1.jar`, `slf4j-simple-2.0.0-alpha1.jar`

* **CẤM** tự ý `import` các thư viện ngoài danh sách trên (như `Jackson`, `Guava`, `Spring`, `Hibernate`, `Netty`, `JUnit`, `Commons-Lang3`).
* Trước khi gọi bất kỳ hàm tiện ích nào trong `Util`, `Service`, `InventoryService`, `ItemService`, phải chắc chắn hàm đó có thật trong `CODEBASE_MAP.md` (không tự bịa tên hàm như `InventoryService.gI().removeItemByTempId(...)` nếu chưa kiểm tra).

### 10. Bảo Toàn Giao Thức Nhị Phân Của Client 2D (Immutable Binary Packet Contract)
Bộ cài Client 2D (`.apk`, `.exe`, `.ipa`) đã được đóng gói cố định thứ tự đọc luồng byte (`DataInputStream`).
1. **Cấm thay đổi cấu trúc gói tin `Message` hiện có**:
   * Trong tất cả các hàm tạo `new Message(cmd)` gửi xuống Client, **TUYỆT ĐỐI CẤM** tự ý thay đổi kiểu dữ liệu (`writeByte` $\leftrightarrow$ `writeShort` $\leftrightarrow$ `writeInt` $\leftrightarrow$ `writeUTF`), cấm đảo thứ tự ghi, và cấm tự ý chèn thêm trường mới vào giữa gói tin nếu Client chưa được lập trình để đọc trường đó.
2. **Bảo toàn phân nhánh theo phiên bản Client (`session.version`)**:
   * Giữ nguyên các nhánh kiểm tra `if (player.getSession().version >= 220)` hoặc `version >= 222` (ví dụ: ở `version < 220` số lượng `quantity` đọc/ghi bằng `Byte`, nhưng từ `version >= 220` đọc/ghi bằng `Int`).

### 11. Quy Tắc "Quá 2 Lần Lỗi Phải Dừng & Hoàn Tác" (2-Strike Rollback Circuit Breaker)
Để chống hiện tượng "Vòng lặp sửa sai làm nát code" (Death Spiral):
1. Nếu bạn sửa một file và chạy kiểm tra biên dịch/chạy thử mà phát sinh lỗi mới, bạn có tối đa **2 lần thử sửa (2 Strikes)**.
2. Nếu sau 2 lần sửa liên tiếp vẫn chưa giải quyết được lỗi hoặc làm phát sinh lỗi dây chuyền sang các file khác:
   * **DỪNG LẠI NGAY LẬP TỨC**.
   * Hoàn tác (Revert) các file vừa sửa về trạng thái sạch ban đầu.
   * Báo cáo ngắn gọn cho người dùng biết chính xác dòng nào đang xung đột và xin ý kiến chỉ đạo, tuyệt đối không tự ý sửa lan sang các file thứ 3, thứ 4.

### 12. Bảo Toàn Bảng Mã Tiếng Việt UTF-8 (Strict UTF-8 Encoding Integrity)
1. Mọi thao tác đọc, sửa, ghi file `.java`, `.php`, `.sql`, `.md`, `.properties` bắt buộc phải thực hiện ở chuẩn **UTF-8 (without BOM)**.
2. Nếu sử dụng script Python, PowerShell hoặc lệnh Shell để hỗ trợ xử lý văn bản, bắt buộc phải khai báo tường minh `encoding="utf-8"` (trong Python) hoặc `[System.Text.Encoding]::UTF8` (trong PowerShell).
3. Kiểm tra lại các chuỗi thông báo tiếng Việt (`Service.gI().sendThongBao(...)`, lời thoại NPC/Boss, giao diện Web) sau khi sửa để đảm bảo không có bất kỳ ký tự tiếng Việt nào bị lỗi font (Mojibake như `Ã`, `á»`, `ï¿½`).
