# DEV_CHEAT_SHEET.md — Sổ Tay Lệnh Chat & Menu Test Dành Cho Developer / Admin (NgocRongBaKhi)

> **Mục đích**: Giúp Lập trình viên và Admin có thể **kiểm thử nhanh tính năng trong 10 giây** ngay trong game sau khi AI vừa code xong, thay vì phải cày cuốc hay chờ đợi thời gian đếm ngược.
> Tất cả các lệnh dưới đây được gõ trực tiếp vào **khung Chat trong game** (yêu cầu nhân vật có quyền Admin / `role = 99` hoặc tài khoản dev).

---

## ⚡ 1. Các Lệnh Chat Admin Thần Tốc (`Command.java`)

| Lệnh Chat | Tác dụng | Trường hợp sử dụng khi Vibe Coding |
| :--- | :--- | :--- |
| **`a`** | **Mở Menu Admin Mèo Đen Tổng Hợp** | Menu tổng quản: Buff full 7 viên Ngọc Rồng, gọi Đệ Tử, gọi Boss, tìm người chơi, đếm số Session/Luồng, kích hoạt Bảo trì. |
| **`m [mapId]`** | **Dịch chuyển tức thời đến Map ID** | Nhảy đến bất kỳ Map nào trong tích tắc (Ví dụ: `m 5` = Đảo Kame, `m 0` = Làng Aru, `m 102` = Doanh trại, `m 113` = Khí gas hủy diệt). |
| **`item`** | **Mở Form tạo / tặng vật phẩm** | Nhập ID Item, số lượng và các chỉ số Option tùy ý để add trực tiếp vào túi. |
| **`getitem`** | **Mở Form lấy vật phẩm mẫu** | Lấy trang bị hoặc vật phẩm mẫu có sẵn trong database. |
| **`hs`** | **Hồi chiêu kỹ năng ngay lập tức** | Reset toàn bộ thời gian hồi chiêu (cooldown = 0) để test liên tục các chiêu thức mà không phải chờ. |
| **`toado`** | **Xem tọa độ hiện tại (X - Y)** | Đo tọa độ đứng của nhân vật để gắn vị trí cố định cho Boss mới, NPC mới hoặc điểm nhặt vật phẩm. |
| **`brl`** | **Xem danh sách Boss Broly** | Kiểm tra danh sách trạng thái của các Boss Broly đang hoạt động trên các khu vực. |
| **`d`** | **Hạ vị trí xuống 10 pixel** | Đẩy nhân vật dịch xuống dưới một chút (`y + 10`) khi bị kẹt địa hình. |
| **`1`** | **Mở Menu Bot Test** | Gọi Bot tự động: Bot Pem Quái, Bot Bán Item, Bot Săn Boss, Bot Attack Player. |
| **`2`** | **Spawn Bot PK Solo** | Sinh ra một Bot mang Cải trang đối kháng trực tiếp để test sát thương PK và kỹ năng. |

---

## 🥋 2. Các Lệnh Điều Khiển Đệ Tử (Pet Chat Commands)

> Chủ nhân đứng cạnh Đệ tử và gõ các câu thoại sau vào khung chat:

| Câu Chat | Tác dụng |
| :--- | :--- |
| **`di theo`** hoặc **`follow`** | Chuyển Đệ tử sang trạng thái bay đi theo sau chủ nhân. |
| **`bao ve`** hoặc **`protect`** | Đệ tử đứng bảo vệ chủ nhân, chỉ đánh mục tiêu nào tấn công chủ nhân. |
| **`tan cong`** hoặc **`attack`** | Đệ tử tự động phát hiện và lao vào tấn công quái xung quanh. |
| **`ve nha`** hoặc **`go home`** | Đệ tử tự động biến về nhà nghỉ ngơi (giảm tiêu hao thức ăn/thể lực). |
| **`bien hinh`** | Kích hoạt hiệu ứng hóa khỉ hoặc biến hình của Đệ tử. |
| **`sach tuyet ky`** | Mở giao diện nâng cấp / học Sách Tuyệt Kỹ cho Đệ tử. |

---

## 🛠️ 3. Quy Trình Vibe Test Chuẩn (3 Bước)
1. **Sau khi AI code xong**: AI chạy `compile_check.bat` ngầm để xác nhận biên dịch thành công.
2. **Khởi động server**: Bạn chạy `ToolCaiGameV3/Source/NroBaKhi/run.bat` (hoặc `restart_server.bat`).
3. **Vào game test**: Dùng các lệnh `a`, `item [id]`, `m [mapId]`, `hs` ở bảng trên để kiểm tra kết quả ngay trong 10 giây!
