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

* **[Khởi tạo Hệ thống Chỉ mục AI]**:
  * Đã quét toàn bộ dự án `NgocRongBaKhi` và tạo bộ tài liệu `AGENTS.md`, `GEMINI.md`, `PACKET_PROTOCOL.md`, `DB_JSON_SCHEMA.md`, `CODEBASE_MAP.md`, `TASK_MEMORY.md`.
  * Chưa thay đổi bất kỳ dòng code logic nào trong `Source/NroBaKhi` hay `Source/WebTeamobi`.
