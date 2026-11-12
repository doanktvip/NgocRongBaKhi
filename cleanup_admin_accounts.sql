-- ==============================================================================
-- SCRIPT DỌN DẸP BẢO MẬT & XÓA TÀI KHOẢN ADMIN MẶC ĐỊNH
-- Chạy script này trên phpMyAdmin / MySQL sau khi import database (team10 / awnv3 / ngocrong)
-- ==============================================================================

-- 1. Xóa toàn bộ các tài khoản admin mặc định / tài khoản test có mật khẩu dễ đoán
DELETE FROM `account` WHERE `username` IN (
    '1', '11', '111', '123',
    'admin', 'admin1', 'admin2', 'admin3',
    'hiep01', 'hiep02', 'hiep03', 'hiep04',
    'black', 'manhmd99', 'smilyne', 'test'
);

-- 2. Đưa toàn bộ các tài khoản còn lại về quyền người chơi thông thường (tránh tài khoản founder ngầm)
UPDATE `account` SET 
    `is_admin` = 0
WHERE `id` > 0;

-- (Nếu dùng database awnv3 có các cột isFounder / isQuanTriVien)
UPDATE `account` SET 
    `isFounder` = 0,
    `isQuanTriVien` = 0
WHERE `id` > 0 AND EXISTS (
    SELECT * FROM information_schema.COLUMNS 
    WHERE TABLE_NAME = 'account' AND COLUMN_NAME = 'isFounder'
);

-- ==============================================================================
-- HƯỚNG DẪN TẠO TÀI KHOẢN ADMIN CHO CHÍNH BẠN:
-- 1. Vào game hoặc web đăng ký tài khoản bình thường (ví dụ: 'myadmin').
-- 2. Chạy lệnh SQL dưới đây để cấp quyền Quản trị viên cho tài khoản đó:
--
-- UPDATE `account` SET `is_admin` = 1, `isFounder` = 1, `active` = 1 WHERE `username` = 'myadmin';
-- ==============================================================================

