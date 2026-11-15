# SQL_PATCHES.md — Sổ Theo Dõi Bản Vá Cơ Sở Dữ Liệu (Database Migration Ledger)

> **Hướng dẫn cho Lập trình viên & AI Agent:**
> 1. Mỗi khi phát triển tính năng mới cần thêm `item_template`, `item_option_template`, `shop`, `npc`, `giftcode` hoặc sửa đổi cấu trúc bảng (`ALTER TABLE`), **BẮT BUỘC ghi lại câu lệnh SQL vào tệp này**.
> 2. Khi cài đặt lại Database hoặc đưa bản cập nhật lên máy chủ VPS / Production, chỉ cần mở tệp này và chạy các khối lệnh SQL mới nhất.
> 3. Tuyệt đối không để câu lệnh SQL bị thất lạc trong các đoạn chat.

---

## 📌 Mẫu Ghi Nhận Bản Vá (Template)
```sql
-- [NGÀY_THỰC_HIỆN] - [TÊN_TÍNH_NĂNG / TASK]
-- Tác giả: [Dev / AI Agent]
-- Mục đích: [Mô tả ngắn gọn mục đích câu lệnh]

-- Lệnh thực thi:
...

-- Lệnh hoàn tác (Rollback nếu có):
...
```

---

## 📜 Danh Sách Bản Vá Đã Thực Hiện

### Patch 001: Khởi Tạo Hệ Thống Sổ Vá Dữ Liệu
* **Ngày thực hiện**: 2026-11-14
* **Mục đích**: Đồng bộ cơ chế quản lý dữ liệu cho Server `NroBaKhi` và Web Admin `WebTeamobi`.
* **Trạng thái**: Cơ sở dữ liệu gốc đang đồng bộ với bản chụp `nrobakhi.sql` (43 bảng).
