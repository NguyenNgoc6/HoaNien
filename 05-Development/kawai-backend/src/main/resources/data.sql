-- ============================================================
-- KAWAI RESORT & TOUR HUB — COMPREHENSIVE SAMPLE DATA (V3.3)
-- Automatically executed on Spring Boot startup
-- ============================================================

-- ── 1. Roles (10 rows) ───────────────────────────────────────
INSERT INTO Roles (role_id, role_name, permissions) VALUES (1, 'ADMIN', 'MASTER_DATA,AUDIT_LOG,DASHBOARD,RECEPTION_WALKIN,RECEPTION_INHOUSE,RECEPTION_CHECKIN,RECEPTION_CHECKOUT,FNB,FNB_ORDER,FNB_TABLE,FNB_ROOM_SERVICE,FNB_REPORT,TOUR,HOUSEKEEPING,MAINTENANCE,NIGHT_AUDIT,ANALYTICS,REVIEWS,PROMOTIONS,CRM,WORKFLOW');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (2, 'RECEPTIONIST', 'DASHBOARD,RECEPTION_WALKIN,RECEPTION_INHOUSE,RECEPTION_CHECKIN,RECEPTION_CHECKOUT,NIGHT_AUDIT,CRM,REVIEWS');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (3, 'F&B KITCHEN', 'DASHBOARD,FNB,FNB_ORDER,FNB_TABLE,FNB_ROOM_SERVICE,FNB_REPORT');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (4, 'F&B POS', 'DASHBOARD,FNB,FNB_ORDER,FNB_TABLE,FNB_ROOM_SERVICE,FNB_REPORT');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (5, 'HOUSEKEEPING', 'DASHBOARD,HOUSEKEEPING');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (6, 'MAINTAINER', 'DASHBOARD,MAINTENANCE');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (7, 'MANAGER', 'DASHBOARD,ANALYTICS');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (8, 'TOURGUIDE', 'DASHBOARD,TOUR');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (9, 'CUSTOMER VIP', '');
INSERT INTO Roles (role_id, role_name, permissions) VALUES (10, 'CUSTOMER NORMAL', '');

-- ── 2. Accounts (20 rows) ────────────────────────────────────
-- password hash for 'admin123': $2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q
-- password hash for 'staff123': $2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG
INSERT INTO Accounts (account_id, username, password_hash, is_active, role_id, created_at) VALUES 
(1, 'admin', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 1, CURRENT_TIMESTAMP),
(2, 'tphuong', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 3, CURRENT_TIMESTAMP),
(3, 'nmquan', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 3, CURRENT_TIMESTAMP),
(4, 'lelinh', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 2, CURRENT_TIMESTAMP),
(5, 'hoangnam', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(6, 'vanan', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(7, 'phamtuan', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(8, 'thibich', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(9, 'ngocthi', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(10, 'mylinh', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(11, 'hoanganh', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(12, 'vuhung', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 9, CURRENT_TIMESTAMP),
(13, 'nguyenngoc', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 8, CURRENT_TIMESTAMP),
(14, 'ngoclan', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 8, CURRENT_TIMESTAMP),
(15, 'guide3', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 8, CURRENT_TIMESTAMP),
(16, 'housekeep1', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 5, CURRENT_TIMESTAMP),
(17, 'housekeep2', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 5, CURRENT_TIMESTAMP),
(18, 'pos1', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 4, CURRENT_TIMESTAMP),
(19, 'manager1', '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q', TRUE, 7, CURRENT_TIMESTAMP),
(20, 'maintain1', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 6, CURRENT_TIMESTAMP);

-- ── 3. Employees (10 rows) ───────────────────────────────────
INSERT INTO Employees (employee_id, account_id, full_name, gender, cccd, phone, email, salary) VALUES 

(1, 1, 'Nguyễn Quản Trị', 'Nam', '001234567890', '0912000001', 'admin@hoanien.vn', 15000000),
(2, 2, 'Trần Phương', 'Nữ', '001234567891', '0912000002', 'tphuong@hoanien.vn', 10000000),
(3, 3, 'Nguyễn Minh Quân', 'Nam', '001234567892', '0912000003', 'nmquan@hoanien.vn', 10000000),
(4, 4, 'Lê Linh', 'Nữ', '001234567893', '0912000004', 'lelinh@hoanien.vn', 9000000),
(5, 13, 'Nguyễn Ngọc', 'Nam', '001234567894', '0912000005', 'guide@hoanien.vn', 8000000),

(6, 14, 'Ngọc Lan', 'Nữ', '001234567895', '0912000006', 'guide2@hoanien.vn', 8500000),
(7, 15, 'Hoàng Nam', 'Nam', '001234567896', '0912000007', 'guide3@hoanien.vn', 8500000),
(8, 16, 'Lê Văn Tám', 'Nam', '001234567897', '0912000008', 'lvtam@hoanien.vn', 7000000),
(9, 17, 'Nguyễn Thị Hoa', 'Nữ', '001234567898', '0912000009', 'nthoa@hoanien.vn', 7000000),

(10, 20, 'Hoàng Bảo Trì', 'Nam', '001234567899', '0912000010', 'hbtri@hoanien.vn', 7500000);


-- ── 3.5. Membership Tiers (4 rows) ───────────────────────────────────
INSERT INTO membership_tiers (tier_id, tier_name, points_from, points_to, credit_limit, description) VALUES

(1, 'Regular', 0, 999, 5000000.00, 'Hạng thẻ mặc định'),

(2, 'Silver', 1000, 4999, 10000000.00, 'Hạng Bạc'),
(3, 'Gold', 5000, 9999, 20000000.00, 'Hạng Vàng'),
(4, 'Platinum', 10000, 99999, 50000000.00, 'Hạng Bạch kim');

-- ── 4. Customers (15 rows) ───────────────────────────────────
INSERT INTO Customers (customer_id, account_id, full_name, gender, cccd_passport_encrypted, phone, email, loyalty_points, membership_tier_id) VALUES 

(1, 5, 'Nguyễn Xuân Lưu', 'Nam', 'CCCD_101', '090101', 'nam101@test.com', 100, 1),
(2, 6, 'Ngọc Thị', 'Nam', 'CCCD_204', '090204', 'an204@test.com', 200, 2),
(3, 7, 'Phạm Tuấn', 'Nam', 'CCCD_308', '090308', 'tuan308@test.com', 500, 3),
(4, 8, 'Trần Thị Bích', 'Nữ', 'CCCD_104', '090104', 'bich104@test.com', 50, 1),
(5, 9, 'Ngọc Thị', 'Nữ', 'CCCD_106', '090106', 'quang106@test.com', 0, 1),
(6, 10, 'Đỗ Mỹ Linh', 'Nữ', 'CCCD_207', '090207', 'linh207@test.com', 150, 2),

(7, 11, 'Hoàng Anh', 'Nam', 'CCCD_210', '090210', 'anh210@test.com', 300, 3),
(8, 12, 'Vũ Hùng', 'Nam', 'CCCD_311', '090311', 'hung311@test.com', 800, 4),
(9, NULL, 'Lưu Đình Đức', 'Nam', 'CCCD_DEMO1', '0909990001', 'duc@test.com', 0, 1),
(10, NULL, 'Nguyễn Minh Đức', 'Nam', 'CCCD_DEMO2', '0909990002', 'duc2@test.com', 0, 1),
(11, NULL, 'Trần Thị Mai', 'Nữ', 'CCCD_DEMO3', '0909990003', 'mai@test.com', 0, 1),

(12, NULL, 'Phạm Hùng Anh', 'Nam', 'CCCD_DEMO4', '0909990004', 'phanh@test.com', 0, 1),

(13, NULL, 'Nguyễn Thanh Sơn', 'Nam', 'CCCD_DEMO5', '0909990005', 'ntson@test.com', 0, 1),
(14, NULL, 'Vũ Thị Thảo', 'Nữ', 'CCCD_DEMO6', '0909990006', 'vtthao@test.com', 0, 1),
(15, NULL, 'Đoàn Minh Khang', 'Nam', 'CCCD_DEMO7', '0909990007', 'dmkhang@test.com', 0, 1);

-- ── 5. Dependents (10 rows) ──────────────────────────────────
INSERT INTO Dependents (dependent_id, customer_id, dependent_name, birth_date, gender, cccd_passport_encrypted) VALUES 
(1, 1, 'Lê Hoàng Minh', '2018-05-12', 'Nam', NULL),
(2, 1, 'Lê Thị Hồng', '2020-09-20', 'Nữ', NULL),
(3, 2, 'Nguyễn Văn Bình', '2015-03-10', 'Nam', NULL),

(4, 3, 'Phạm Tuấn Hải', '2016-07-15', 'Nam', NULL),

(5, 4, 'Trần An Nhiên', '2019-11-01', 'Nữ', NULL),
(6, 6, 'Nguyễn Mỹ Anh', '2017-02-14', 'Nữ', NULL),
(7, 7, 'Hoàng Minh Khôi', '2014-06-25', 'Nam', NULL),
(8, 8, 'Vũ Gia Bảo', '2013-08-30', 'Nam', NULL),
(9, 12, 'Phạm Ngọc Trâm', '2021-10-05', 'Nữ', NULL),
(10, 13, 'Nguyễn Thanh Hà', '2022-12-25', 'Nữ', NULL);

-- ── 6. Room Categories (10 rows) ─────────────────────────────
-- Columns: category_id, category_name, cover_img_url, base_price, capacity, description,
--          base_adults, base_children, max_adults, max_children, extra_adult_surcharge, extra_child_surcharge, is_active,
--          bed_type, room_size, view_type, has_bathtub, has_balcony, complimentary_services, has_free_breakfast
INSERT INTO Room_Categories (
    category_id, category_name, cover_img_url, base_price, capacity, description,
    base_adults, base_children, max_adults, max_children, extra_adult_surcharge, extra_child_surcharge, is_active,
    bed_type, room_size, view_type, has_bathtub, has_balcony, complimentary_services, has_free_breakfast
) VALUES 
(1,  'Nipa Pool Villa',          'https://images.unsplash.com/photo-1540541338287-41700207dee6', 2500000,  2, 'Villa thanh tịnh bên hồ sen thơm mát.',             2, 0, 3, 1, 500000,  250000, TRUE, '1 Giường King 2m2', 65, 'Hướng hồ bơi', TRUE, TRUE, '2 chai nước suối, Trái cây tươi, Vang đỏ', TRUE),
(2,  'River Pool Villa',         'https://images.unsplash.com/photo-1566073771259-6a8506099945', 3500000,  3, 'Villa cao cấp ven sông Thu Bồn lộng gió.',          2, 0, 3, 2, 600000,  300000, TRUE, '1 Giường King 2m2', 80, 'Hướng sông Thu Bồn', TRUE, TRUE, '4 chai nước suối, Trái cây, Trà chiều', TRUE),
(3,  'Wellness Retreats',        'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4', 8000000,  4, 'Hành trình tĩnh lặng, chăm sóc sức khoẻ toàn diện.', 2, 0, 4, 2, 1000000, 500000, TRUE, '2 Giường King', 120, 'Hướng vườn thiền', TRUE, TRUE, 'Nước detox, Trái cây Organic, Trà thảo mộc', TRUE),
(4,  'Garden View Suite',        'https://images.unsplash.com/photo-1578683010236-d716f9a3f461', 2000000,  2, 'Suite hướng vườn nhiệt đới xanh mướt.',             2, 0, 3, 1, 400000,  200000, TRUE, '1 Giường Queen 1m8', 45, 'Hướng vườn nhiệt đới', FALSE, TRUE, '2 chai nước suối, Trà & Cà phê', TRUE),
(5,  'Presidential Ocean Suite', 'https://images.unsplash.com/photo-1590490360182-c33d57733427', 15000000, 6, 'Hạng phòng cao cấp bậc nhất hướng biển.',           4, 0, 6, 3, 2000000, 1000000, TRUE, '3 Giường King 2m2', 250, 'Hướng biển toàn cảnh', TRUE, TRUE, 'Minibar miễn phí, Rượu Champagne, Bánh ngọt', TRUE),
(6,  'Ocean View Bungalow',      'https://images.unsplash.com/photo-1582719508461-905c673771fd', 3000000,  2, 'Bungalow bãi cát đón gió biển tươi mát.',           2, 0, 3, 1, 600000,  300000, TRUE, '1 Giường King 2m2', 50, 'Hướng biển', TRUE, TRUE, '2 chai nước suối, Trái cây tươi', TRUE),
(7,  'Family Connecting Room',   'https://images.unsplash.com/photo-1568495248636-6432b97bd949', 4500000,  5, 'Phòng thông nhau phù hợp cho cả gia đình.',         2, 2, 4, 4, 500000,  250000, TRUE, '1 Giường King & 2 Giường Đơn', 90, 'Hướng vườn', FALSE, TRUE, '4 chai nước suối, Bánh quy, Trà', TRUE),
(8,  'Superior Mountain View',   'https://images.unsplash.com/photo-1596394516093-501ba68a0ba6', 1800000,  2, 'Phòng hướng núi thanh tịnh bình yên.',              2, 0, 2, 1, 350000,  150000, TRUE, '2 Giường Đơn 1m2', 40, 'Hướng núi đồi', FALSE, FALSE, '2 chai nước suối, Trà & Cà phê', TRUE),

(9,  'Luxury Penthouse',         'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688', 12000000, 4, 'Căn hộ tầng mái đẳng cấp ngắm toàn cảnh resort.',  2, 0, 4, 2, 1500000, 750000, TRUE, '2 Giường King siêu lớn', 180, 'Toàn cảnh Resort', TRUE, TRUE, 'Rượu vang cao cấp, Trái cây nhập khẩu, Minibar', TRUE),
(10, 'Cozy Studio Room',         'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af', 1500000,  2, 'Phòng Studio nhỏ gọn, đầy đủ tiện nghi.',          2, 0, 2, 1, 300000,  150000, TRUE, '1 Giường Queen 1m8', 35, 'Hướng đường phố', FALSE, FALSE, '2 chai nước suối, Cà phê hòa tan', FALSE);

-- ── 7. Room Surcharges (10 rows) ─────────────────────────────
INSERT INTO Room_Surcharges (surcharge_id, category_id, surcharge_type, age_from, age_to, price_modifier, is_active) VALUES 
(1, 1, 'EXTRA_ADULT_BED', 12, 100, 500000, TRUE),
(2, 1, 'CHILD_WITH_BED', 6, 11, 250000, TRUE),
(3, 1, 'CHILD_WITHOUT_BED', 0, 5, 0, TRUE),
(4, 2, 'EXTRA_ADULT_BED', 12, 100, 600000, TRUE),
(5, 2, 'CHILD_WITH_BED', 6, 11, 300000, TRUE),
(6, 3, 'EXTRA_ADULT_BED', 12, 100, 1000000, TRUE),
(7, 3, 'CHILD_WITH_BED', 6, 11, 500000, TRUE),
(8, 4, 'EXTRA_ADULT_BED', 12, 100, 400000, TRUE),
(9, 5, 'EXTRA_ADULT_BED', 12, 100, 2000000, TRUE),
(10, 6, 'EXTRA_ADULT_BED', 12, 100, 600000, TRUE);

-- ── 8. Rooms (50 rows) ───────────────────────────────────────
INSERT INTO Rooms (room_id, room_number, category_id, room_status, current_booking_detail_id) VALUES 
(1, '101', 1, 'Occupied', 1),
(2, '102', 1, 'Vacant_Clean', NULL),
(3, '103', 1, 'Occupied', 2),
(4, '104', 1, 'Vacant_Clean', NULL),
(5, '105', 1, 'Occupied', 3),
(6, '106', 4, 'Vacant_Clean', NULL),
(7, '107', 4, 'Vacant_Clean', NULL),
(8, '108', 4, 'Vacant_Clean', NULL),
(9, '109', 4, 'Vacant_Clean', NULL),
(10, '110', 4, 'Vacant_Clean', NULL),
(11, '201', 2, 'Vacant_Clean', NULL),
(12, '202', 2, 'Vacant_Clean', NULL),
(13, '203', 2, 'Vacant_Clean', NULL),
(14, '204', 2, 'Vacant_Clean', NULL),
(15, '205', 2, 'Occupied', 7),
(16, '206', 10, 'Vacant_Clean', NULL),
(17, '207', 10, 'Vacant_Clean', NULL),
(18, '208', 10, 'Occupied', 8),
(19, '209', 10, 'Vacant_Clean', NULL),
(20, '210', 10, 'Vacant_Clean', NULL),
(21, '301', 7, 'Vacant_Clean', NULL),
(22, '302', 7, 'Vacant_Clean', NULL),
(23, '303', 7, 'Vacant_Clean', NULL),
(24, '304', 7, 'Vacant_Clean', NULL),
(25, '305', 7, 'Vacant_Clean', NULL),
(26, '306', 8, 'Vacant_Clean', NULL),
(27, '307', 8, 'Vacant_Clean', NULL),
(28, '308', 8, 'Vacant_Clean', NULL),
(29, '309', 8, 'Vacant_Clean', NULL),
(30, '310', 8, 'Vacant_Clean', NULL),
(31, '401', 6, 'Vacant_Clean', NULL),
(32, '402', 6, 'Vacant_Clean', NULL),
(33, '403', 6, 'Vacant_Clean', NULL),
(34, '404', 6, 'Vacant_Clean', NULL),
(35, '405', 6, 'Vacant_Clean', NULL),
(36, '406', 3, 'Vacant_Clean', NULL),
(37, '407', 3, 'Vacant_Clean', NULL),
(38, '408', 3, 'Vacant_Clean', NULL),
(39, '409', 3, 'Vacant_Clean', NULL),
(40, '410', 3, 'Vacant_Clean', NULL),
(41, '501', 5, 'Vacant_Clean', NULL),
(42, '502', 5, 'Vacant_Clean', NULL),
(43, '503', 5, 'Vacant_Clean', NULL),
(44, '504', 5, 'Vacant_Clean', NULL),
(45, '505', 5, 'Vacant_Clean', NULL),
(46, '506', 9, 'Vacant_Clean', NULL),
(47, '507', 9, 'Vacant_Clean', NULL),
(48, '508', 9, 'Vacant_Clean', NULL),
(49, '509', 9, 'Vacant_Clean', NULL),
(50, '510', 9, 'Vacant_Clean', NULL);

-- ── 9. Dynamic Pricing (10 rows) ─────────────────────────────
INSERT INTO Dynamic_Pricing (price_id, category_id, start_date, end_date, price_modifier, reason) VALUES 
(1, 1, '2026-06-01', '2026-06-30', 200000, 'Mùa cao điểm hè'),
(2, 2, '2026-06-01', '2026-06-30', 300000, 'Mùa cao điểm hè'),
(3, 3, '2026-06-01', '2026-06-30', 500000, 'Mùa cao điểm hè'),
(4, 5, '2026-06-01', '2026-06-30', 1000000, 'Mùa cao điểm hè'),
(5, 9, '2026-06-01', '2026-06-30', 800000, 'Mùa cao điểm hè'),
(6, 1, '2026-09-01', '2026-09-30', -200000, 'Ưu đãi mùa thu'),
(7, 2, '2026-09-01', '2026-09-30', -300000, 'Ưu đãi mùa thu'),
(8, 3, '2026-09-01', '2026-09-30', -500000, 'Ưu đãi mùa thu'),
(9, 4, '2026-09-01', '2026-09-30', -150000, 'Ưu đãi mùa thu'),
(10, 6, '2026-09-01', '2026-09-30', -250000, 'Ưu đãi mùa thu');


-- ── 10. Daily Rates (10 rows) ────────────────────────────────
INSERT INTO Daily_Rates (daily_rate_id, category_id, rate_date, computed_price, is_weekend, is_holiday) VALUES 

(1, 1, '2026-06-13', 2700000, TRUE, FALSE),
(2, 1, '2026-06-14', 2700000, TRUE, FALSE),
(3, 2, '2026-06-13', 3800000, TRUE, FALSE),
(4, 2, '2026-06-14', 3800000, TRUE, FALSE),
(5, 3, '2026-06-13', 8500000, TRUE, FALSE),
(6, 3, '2026-06-14', 8500000, TRUE, FALSE),
(7, 4, '2026-06-13', 2200000, TRUE, FALSE),
(8, 5, '2026-06-13', 16000000, TRUE, FALSE),
(9, 6, '2026-06-13', 3300000, TRUE, FALSE),
(10, 7, '2026-06-13', 4800000, TRUE, FALSE);

-- ── 11. Promotions (10 rows) ─────────────────────────────────
INSERT INTO Promotions (promo_id, promo_code, discount_type, discount_value, valid_from, valid_to, max_uses, current_uses, is_active, description) VALUES 

(1, 'SUMMER2026', 'PERCENTAGE', 10.00, '2026-05-01 00:00:00', '2026-08-31 23:59:59', 1000, 15, TRUE, 'Giảm giá 10% cho toàn bộ dịch vụ đặt phòng hè.'),
(2, 'WELCOMETOHOANIEN', 'FIXED_AMOUNT', 200000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 5000, 120, TRUE, 'Tặng ngay 200,000 VND cho khách đặt phòng lần đầu.'),
(3, 'VIPGOLD', 'PERCENTAGE', 15.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 9999, 45, TRUE, 'Ưu đãi đặc biệt giảm 15% cho thành viên Gold.'),
(4, 'MIDWEEK20', 'PERCENTAGE', 20.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 200, 10, TRUE, 'Giảm 20% đặt phòng từ thứ 2 đến thứ 5.'),

(5, 'AUTUMNRETREAT', 'PERCENTAGE', 12.00, '2026-09-01 00:00:00', '2026-11-30 23:59:59', 500, 0, TRUE, 'Giảm giá 12% chăm sóc sức khoẻ mùa thu.'),

(6, 'HONEYMOON', 'FIXED_AMOUNT', 500000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 100, 2, TRUE, 'Gói trăng mật ngọt ngào giảm ngay 500k.'),
(7, 'FESTIVE15', 'PERCENTAGE', 15.00, '2026-12-20 00:00:00', '2027-01-05 23:59:59', 1000, 0, TRUE, 'Chào đón giáng sinh và năm mới.'),
(8, 'VOUCHER100K', 'FIXED_AMOUNT', 100000.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 10000, 300, TRUE, 'Voucher 100k cho khách hàng thân thiết.'),
(9, 'EARLYBIRD', 'PERCENTAGE', 8.00, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 2000, 50, TRUE, 'Đặt trước 30 ngày hưởng ngay ưu đãi 8%.');
-- ── 12. Bookings (20 rows) ───────────────────────────────────

INSERT INTO Bookings (booking_id, customer_id, booking_date, total_price, booking_status, booking_source, applied_promotion_id, version) VALUES
(1, 1, '2026-06-01', 5000000, 'Confirmed', 'Direct_Web', 1, 1),
(2, 2, '2026-06-02', 7000000, 'Confirmed', 'Direct_Web', NULL, 1),
(3, 3, '2026-06-03', 16000000, 'Confirmed', 'OTA', NULL, 1),
(4, 4, '2026-06-04', 5000000, 'Confirmed', 'OTA', 2, 1),
(5, 5, '2026-06-05', 5000000, 'Confirmed', 'Direct_Web', NULL, 1),
(6, 6, '2026-06-06', 7000000, 'Confirmed', 'OTA', NULL, 1),
(7, 7, '2026-06-07', 7000000, 'Confirmed', 'Direct_Web', 3, 1),
(8, 8, '2026-06-08', 16000000, 'Confirmed', 'Direct_Web', NULL, 1),
(9, 1, '2026-06-10', 1200000, 'Confirmed', 'Direct_Web', NULL, 1),
(10, 2, '2026-06-10', 1200000, 'Confirmed', 'Direct_Web', NULL, 1),
(11, 9, '2026-06-12', 1200000, 'Confirmed', 'Direct_Web', NULL, 1),
(12, 10, '2026-06-12', 1200000, 'Confirmed', 'Direct_Web', NULL, 1),
(13, 11, '2026-06-12', 1200000, 'Confirmed', 'Direct_Web', NULL, 1),
(14, 12, '2026-06-12', 2500000, 'Cancelled', 'Direct_Web', NULL, 1),
(15, 13, '2026-06-12', 3500000, 'Cancelled', 'Direct_Web', NULL, 1),
(16, 14, '2026-06-12', 8000000, 'Confirmed', 'Direct_Web', NULL, 1),
(17, 15, '2026-06-12', 2000000, 'Confirmed', 'Direct_Web', NULL, 1),
(18, 3, '2026-06-12', 15000000, 'Confirmed', 'Direct_Web', NULL, 1),
(19, 4, '2026-06-12', 3000000, 'Confirmed', 'Direct_Web', NULL, 1),
(20, 5, '2026-06-12', 4500000, 'Confirmed', 'Direct_Web', NULL, 1),
(24, 1, '2026-06-01', 3000000, 'Confirmed', 'Direct_Web', NULL, 1),
(25, 2, '2026-06-02', 1200000, 'Confirmed', 'Direct_Web', NULL, 1),
(26, 3, '2026-06-03', 5400000, 'Confirmed', 'Direct_Web', NULL, 1),
(27, 4, '2026-06-04', 2500000, 'Confirmed', 'OTA', NULL, 1),
(28, 5, '2026-06-05', 2400000, 'Confirmed', 'Direct_Web', NULL, 1);

-- ── 13. Room Bookings (10 rows) ──────────────────────────────
INSERT INTO Room_Bookings (room_booking_id, check_in_date, check_out_date, deposit_amount, cancellation_deadline, credit_limit, personal_pin_hash) VALUES
(1, '2026-06-09', '2026-06-12', 1000000, '2026-06-05', 5000000, 'hash'),
(2, '2026-06-09', '2026-06-12', 1000000, '2026-06-05', 5000000, 'hash'),
(3, '2026-06-09', '2026-06-12', 2000000, '2026-06-05', 10000000, 'hash'),
(4, '2026-06-09', '2026-06-11', 1000000, '2026-06-05', 5000000, 'hash'),
(5, '2026-06-10', '2026-06-13', 1000000, '2026-06-06', 5000000, 'hash'),
(6, '2026-06-10', '2026-06-14', 1500000, '2026-06-06', 5000000, 'hash'),
(7, '2026-06-10', '2026-06-15', 1500000, '2026-06-06', 5000000, 'hash'),
(8, '2026-06-10', '2026-06-16', 3000000, '2026-06-06', 15000000, 'hash'),
(14, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 4 DAY), 1000000, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 5000000, 'hash'),
(15, '2026-07-01', '2026-07-05', 1000000, '2026-06-25', 5000000, 'hash');

-- ── 14. Room Booking Details (10 rows) ───────────────────────
INSERT INTO Room_Booking_Details (detail_id, room_booking_id, category_id, room_id, room_charge, detail_status, bed_preference, special_requests, is_charge_to_room_allowed, sub_credit_limit, billing_routing_strategy) VALUES
(1, 1, 1, 1, 2500000, 'Checked_In', 'KING_SIZE', NULL, TRUE, 500000, 'BILL_TO_LEADER'),
(2, 2, 2, 3, 3500000, 'Checked_In', 'KING_SIZE', NULL, TRUE, 1500000, 'BILL_TO_LEADER'),
(3, 3, 3, 5, 8000000, 'Checked_In', 'KING_SIZE', NULL, TRUE, 2000000, 'BILL_TO_LEADER'),
(4, 4, 1, 7, 2500000, 'Pending', 'KING_SIZE', NULL, TRUE, 500000, 'BILL_TO_LEADER'),
(5, 5, 1, 9, 2500000, 'Pending', 'KING_SIZE', NULL, TRUE, 500000, 'BILL_TO_LEADER'),
(6, 6, 2, 12, 3500000, 'Pending', 'KING_SIZE', NULL, TRUE, 1500000, 'BILL_TO_LEADER'),
(7, 7, 2, 15, 3500000, 'Checked_In', 'KING_SIZE', NULL, TRUE, 1500000, 'BILL_TO_LEADER'),
(8, 8, 3, 18, 8000000, 'Checked_In', 'KING_SIZE', NULL, TRUE, 3000000, 'BILL_TO_LEADER'),
(9, 14, 1, 2, 2500000, 'Pending', 'KING_SIZE', NULL, TRUE, 500000, 'BILL_TO_LEADER'),
(10, 15, 2, 4, 3500000, 'Pending', 'TWIN_BED', NULL, TRUE, 1500000, 'BILL_TO_LEADER');

-- ── 15. Room Guests (10 rows) ────────────────────────────────
INSERT INTO Room_Guests (guest_id, detail_id, customer_id, dependent_id, guest_type, is_primary_contact) VALUES
(1, 1, 1, NULL, 'ADULT', TRUE),
(2, 2, 2, NULL, 'ADULT', TRUE),
(3, 3, 3, NULL, 'ADULT', TRUE),
(4, 4, 4, NULL, 'ADULT', TRUE),
(5, 5, 5, NULL, 'ADULT', TRUE),
(6, 6, 6, NULL, 'ADULT', TRUE),
(7, 7, 7, NULL, 'ADULT', TRUE),
(8, 8, 8, NULL, 'ADULT', TRUE),
(9, 9, 12, NULL, 'ADULT', TRUE),
(10, 10, 13, NULL, 'ADULT', TRUE);

-- Force update tphuong to POS role in case DB already exists
UPDATE Accounts SET role_id = 4 WHERE username = 'tphuong';

-- Restore hoangnam password back to admin123 (was accidentally changed to staff123)
UPDATE Accounts SET password_hash = '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q' WHERE username = 'hoangnam';

-- Delete tmduc account and customer record
DELETE FROM Customers WHERE account_id = 98;
DELETE FROM Accounts WHERE username = 'tmduc';

-- Add ducbeo account if not exists (safe insert)
INSERT IGNORE INTO Accounts (account_id, username, password_hash, is_active, role_id, created_at)
VALUES (99, 'ducbeo', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 3, CURRENT_TIMESTAMP);


-- Force update tphuong to POS role in case DB already exists
UPDATE Accounts SET role_id = 4 WHERE username = 'tphuong';

-- Restore hoangnam password back to admin123 (was accidentally changed to staff123)
UPDATE Accounts SET password_hash = '$2a$10$4bnThA4xQVw1rF2POQv78uAQll2KsUsgF32JaYiVG5d2d3Fhgk83q' WHERE username = 'hoangnam';

-- Delete tmduc account and customer record
DELETE FROM Customers WHERE account_id = 98;
DELETE FROM Accounts WHERE username = 'tmduc';

-- Add ducbeo account if not exists (safe insert)
INSERT IGNORE INTO Accounts (account_id, username, password_hash, is_active, role_id, created_at)
VALUES (99, 'ducbeo', '$2a$10$ikP3XeXnMx/oLodhs4wqBO61AhyuE4dWtSJDJcOH7D2ii5Vrgq0bG', TRUE, 3, CURRENT_TIMESTAMP);


-- Update Room's current booking detail links
UPDATE Rooms SET current_booking_detail_id = 1 WHERE room_id = 1;
UPDATE Rooms SET current_booking_detail_id = 2 WHERE room_id = 3;
UPDATE Rooms SET current_booking_detail_id = 3 WHERE room_id = 5;
UPDATE Rooms SET current_booking_detail_id = 4 WHERE room_id = 7;
UPDATE Rooms SET current_booking_detail_id = 5 WHERE room_id = 9;
UPDATE Rooms SET current_booking_detail_id = 6 WHERE room_id = 12;
UPDATE Rooms SET current_booking_detail_id = 7 WHERE room_id = 15;
UPDATE Rooms SET current_booking_detail_id = 8 WHERE room_id = 18;
UPDATE Rooms SET current_booking_detail_id = 9 WHERE room_id = 2;
UPDATE Rooms SET current_booking_detail_id = 10 WHERE room_id = 4;

-- ── 16. Restaurant Tables (20 rows) ──────────────────────────
INSERT INTO Restaurant_Tables (table_id, table_number, capacity, table_status, is_active) VALUES 
(1, 'T01', 4, 'Occupied', TRUE),
(2, 'T02', 2, 'Available', TRUE),
(3, 'T03', 6, 'Occupied', TRUE),
(4, 'T04', 4, 'Available', TRUE),
(5, 'T05', 8, 'Available', TRUE),
(6, 'T06', 4, 'Available', TRUE),
(7, 'T07', 2, 'Occupied', TRUE),
(8, 'T08', 4, 'Available', TRUE),
(9, 'T09', 10, 'Available', TRUE),
(10, 'T10', 2, 'Available', TRUE),
(11, 'T11', 4, 'Occupied', TRUE),
(12, 'T12', 6, 'Available', TRUE),
(13, 'T13', 4, 'Available', TRUE),
(14, 'T14', 8, 'Available', TRUE),
(15, 'T15', 2, 'Occupied', TRUE),
(16, 'T16', 4, 'Available', TRUE),
(17, 'T17', 4, 'Available', TRUE),
(18, 'T18', 6, 'Occupied', TRUE),
(19, 'T19', 2, 'Available', TRUE),
(20, 'T20', 12, 'Available', TRUE);


-- ── 17. Table Reservations (10 rows) ─────────────────────────
INSERT INTO Table_Reservations (reservation_id, customer_id, table_id, reserve_date, reserve_time, end_time, deposit_amount, status) VALUES 
(1, 1, 1, '2026-06-13', '18:30:00', '20:30:00', 100000, 'Confirmed'),
(2, 2, 3, '2026-06-13', '19:00:00', '21:00:00', 100000, 'Confirmed'),
(3, 3, 4, '2026-06-13', '19:30:00', '21:30:00', 200000, 'Confirmed'),
(4, 4, 8, '2026-06-14', '18:00:00', '20:00:00', 100000, 'Pending'),
(5, 5, 14, '2026-06-14', '19:00:00', '21:00:00', 200000, 'Pending'),
(6, 6, 20, '2026-06-14', '20:00:00', '22:00:00', 300000, 'Confirmed'),
(7, 7, 7, '2026-06-13', '21:00:00', '23:00:00', 100000, 'Confirmed'),
(8, 8, 15, '2026-06-13', '20:30:00', '22:30:00', 100000, 'Confirmed'),
(9, 12, 11, '2026-06-13', '19:00:00', '21:00:00', 100000, 'Confirmed'),
(10, 13, 18, '2026-06-13', '18:00:00', '20:00:00', 150000, 'Confirmed'),
(11, 14, 1, '2026-06-20', '18:00:00', '20:00:00', 100000, 'Confirmed'),
(12, 15, 2, '2026-06-20', '19:00:00', '21:00:00', 150000, 'Confirmed'),
(13, 1, 3, '2026-06-20', '12:00:00', '14:00:00', 100000, 'Confirmed'),
(14, 2, 4, '2026-06-20', '20:00:00', '22:30:00', 200000, 'Pending'),
(15, 3, 5, '2026-06-21', '08:00:00', '10:00:00', 150000, 'Confirmed'),
(16, 4, 6, '2026-06-21', '11:30:00', '13:30:00', 100000, 'Confirmed'),
(17, 5, 7, '2026-06-21', '19:00:00', '21:00:00', 200000, 'Confirmed'),
(18, 6, 8, '2026-06-22', '18:30:00', '20:30:00', 100000, 'Pending'),
(19, 7, 9, '2026-06-22', '20:00:00', '22:00:00', 300000, 'Confirmed'),
(20, 8, 10, '2026-06-23', '09:00:00', '11:00:00', 100000, 'Confirmed'),
(21, 9, 11, '2026-06-23', '12:00:00', '14:00:00', 150000, 'Confirmed'),
(22, 10, 12, '2026-06-24', '18:00:00', '21:00:00', 200000, 'Pending'),
(23, 11, 13, '2026-06-24', '19:30:00', '21:30:00', 100000, 'Confirmed'),
(24, 12, 14, '2026-06-25', '11:00:00', '13:00:00', 300000, 'Confirmed'),
(25, 13, 15, '2026-06-25', '20:00:00', '22:00:00', 100000, 'Confirmed'),
(26, 14, 16, '2026-06-26', '18:00:00', '20:00:00', 100000, 'Confirmed'),
(27, 15, 17, '2026-06-26', '19:00:00', '21:00:00', 100000, 'Pending'),
(28, 1, 18, '2026-06-27', '08:30:00', '10:30:00', 150000, 'Confirmed'),
(29, 2, 19, '2026-06-27', '12:30:00', '14:30:00', 100000, 'Confirmed'),
(30, 3, 20, '2026-06-28', '19:00:00', '21:00:00', 400000, 'Confirmed'),
(31, 4, 1, '2026-06-28', '20:00:00', '22:00:00', 100000, 'Pending'),
(32, 5, 2, '2026-06-29', '18:00:00', '20:30:00', 100000, 'Confirmed'),
(33, 6, 3, '2026-06-29', '19:00:00', '21:00:00', 100000, 'Confirmed'),
(34, 7, 4, '2026-06-30', '11:00:00', '13:00:00', 200000, 'Confirmed'),
(35, 8, 5, '2026-06-30', '18:30:00', '20:30:00', 150000, 'Confirmed'),
(36, 9, 6, '2026-07-01', '12:00:00', '14:00:00', 100000, 'Pending'),
(37, 10, 7, '2026-07-01', '18:00:00', '20:00:00', 100000, 'Confirmed'),
(38, 11, 8, '2026-07-01', '19:00:00', '21:00:00', 100000, 'Confirmed');

-- ── 18. Menu Items (34 rows) ─────────────────────────────────
INSERT INTO Menu_Items (item_id, item_name, price, category, is_available, description, image_url, allergy_tags, is_always_available) VALUES 


(1, 'Gỏi cá mai', 185000, 'Khai vị', TRUE, 'Đặc sản Mũi Né với cá mai tươi, rau thơm và nước chấm chua ngọt hài hòa.', 'https://cdn.zsoft.solutions/poseidon-web/app/media/Kham-pha-am-thuc/03.2024/18324-goi-ca-mai-1.jpg', 'Fish, Peanut', FALSE),
(2, 'Bánh căn hải sản', 95000, 'Khai vị', TRUE, 'Bánh căn nóng giòn kết hợp tôm, mực tươi và nước chấm đậm đà.', 'https://media.thuonghieucongluan.vn/uploads/2026/03/01/banh-can-thumb-1772322285.jpg', 'Shellfish, Egg', FALSE),
(3, 'Bánh xèo Mũi Né', 110000, 'Khai vị', TRUE, 'Bánh xèo vàng giòn với nhân tôm thịt và rau sống tươi ngon.', 'https://i-giadinh.vnecdn.net/2023/09/19/Bc10Thnhphm11-1695107510-2493-1695107555.jpg', 'Shellfish, Gluten', FALSE),
(4, 'Súp cua', 120000, 'Khai vị', TRUE, 'Súp cua thơm ngọt với thịt cua, trứng và bắp non.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8zh8s2hEoOtqeCKJD9AUdyuYcpMXLIbUMlzdFDIw1FQ&s=10', 'Shellfish, Egg', FALSE),
(5, 'Súp hải sản', 145000, 'Khai vị', TRUE, 'Súp hải sản thanh ngọt từ tôm, mực và rau củ tươi.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRMVM6DWLLebjQTPWe7VkBltCuqH4bgqGxF1tL5UolVlw&s=10', 'Shellfish', FALSE),
(6, 'Súp gà nấm hương', 95000, 'Khai vị', TRUE, 'Súp gà mềm ngọt hòa quyện cùng nấm hương thơm tự nhiên.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSWn21CZqwUZCo8466Eqh4pbrRcTb-Vk4oFNL9fZqYXMg&s=10', NULL, FALSE),
(7, 'Súp bí đỏ kem tươi', 85000, 'Khai vị', TRUE, 'Bí đỏ xay mịn kết hợp kem tươi, béo nhẹ và thanh vị.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQUKVE6Brt_re0D5tnTCoqbBsJttHZpqZzOV-vXeu4vlA&s=10', 'Milk', FALSE),
(8, 'Gỏi ngó sen tôm thịt', 165000, 'Khai vị', TRUE, 'Ngó sen giòn tươi hòa quyện cùng tôm thịt và nước sốt chua ngọt.', 'https://cooponline.vn/tin-tuc/wp-content/uploads/2025/10/Avatar-2.png', 'Shellfish', FALSE),
(9, 'Gỏi gà xé phay', 135000, 'Khai vị', TRUE, 'Gà xé mềm trộn rau thơm và hành tây, thanh mát và hấp dẫn.', 'https://www.huongnghiepaau.com/wp-content/uploads/2025/05/cach-lam-goi-ga-xe-phay.jpg', NULL, FALSE),
(10, 'Gỏi bò bóp thấu', 175000, 'Khai vị', TRUE, 'Thịt bò mềm kết hợp rau củ tươi và nước sốt đậm vị.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTzllA_ZP9TSD2nQoyOFZcO_tSm6pjC330U4ZrDEEtC5g&s=10', NULL, FALSE),
(11, 'Gỏi xoài tôm khô', 145000, 'Khai vị', TRUE, 'Xoài xanh giòn chua nhẹ kết hợp tôm khô đậm đà.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcStIHf9pX74-zFf2s1VD39X4noHVXdIMVy1lwIJafLINQ&s=10', 'Shellfish', FALSE),
(12, 'Gỏi hải sản', 195000, 'Khai vị', TRUE, 'Hải sản tươi trộn rau củ cùng nước sốt chua cay đặc trưng.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSJqlK-m2m3xYxNQIkzS8YY-py6aaS0zjoUkf2MmTum8w&s=10', 'Shellfish', FALSE),
(13, 'Salad rau củ dầu giấm', 95000, 'Khai vị', TRUE, 'Rau củ tươi theo mùa trộn cùng sốt dầu giấm thanh nhẹ.', 'https://winefood.com.vn/wp-content/uploads/2024/06/z5512961476611_be635db082cdac788fca5636858378e3.jpg', NULL, FALSE),
(14, 'Salad cá ngừ', 185000, 'Khai vị', TRUE, 'Cá ngừ áp chảo kết hợp rau xanh tươi, cà chua bi và sốt mè rang thanh nhẹ.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTl8cQq2uQtbwh_kgPSN8NVX9E21FAjvQMbNIIrsap37Q&s=10', 'Fish, Sesame', FALSE),
(15, 'Mực một nắng nướng muối ớt', 395000, 'Món chính', TRUE, 'Mực một nắng nướng thơm, giữ trọn vị ngọt tự nhiên của biển.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTMosFGfsoMVWXSgYpus4wExPxCmHG6AHRsxT86UnQsRg&s=10', 'Shellfish', FALSE),
(16, 'Lẩu cá bớp', 520000, 'Món chính', TRUE, 'Cá bớp tươi trong nước lẩu chua thanh, dùng kèm rau và bún.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRK6eKKuhAudZfD5u7gclDml2jb4iJQU3ZjOyRzzYA0Lw&s=10', 'Fish', FALSE),
(17, 'Bánh canh chả cá', 125000, 'Món chính', TRUE, 'Bánh canh dai mềm cùng chả cá và nước dùng đậm đà.', 'https://lalago.vn/wp-content/uploads/2025/05/banh-canh-cha-ca-phan-thiet-6.jpg', 'Fish, Gluten', FALSE),
(18, 'Cá bóp kho tộ', 245000, 'Món chính', TRUE, 'Cá bóp kho theo phong cách truyền thống với hương vị đậm đà.', 'https://i-giadinh.vnecdn.net/2023/09/16/Bc4Thnhphm11-1694856717-6271-1694856861.jpg', 'Fish, Soy', FALSE),
(19, 'Tôm nướng mọi', 385000, 'Món chính', TRUE, 'Tôm tươi nướng nguyên con, giữ vị ngọt tự nhiên.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS1NeG6yA1EQWEt_55ym0xMlWGcxItK_LWpiJyangoyFA&s=10', 'Shellfish', FALSE),
(20, 'Ghẹ hấp sả', 595000, 'Món chính', TRUE, 'Ghẹ tươi hấp cùng sả, thơm dịu và ngọt thịt.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdqcrP2iDZvCfLVdvinGmUR-rayFu9lJtr0ELRyfSu3g&s=10', 'Shellfish', FALSE),
(21, 'Sò điệp nướng mỡ hành', 285000, 'Món chính', TRUE, 'Sò điệp nướng béo thơm cùng mỡ hành và đậu phộng.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS668yHo5tseb6H2oxdx3x3NIvMn7UKgwSN-ayuIsEoRQ&s=10', 'Shellfish, Peanut', FALSE),
(22, 'Mực lá nướng sa tế', 355000, 'Món chính', TRUE, 'Mực lá nướng cùng sốt sa tế cay nhẹ, đậm hương vị.', 'https://cdn.tgdd.vn/Files/2019/03/23/1156454/3-cach-lam-muc-nuong-sa-te-han-quoc-va-chao-thom-ngon-kho-cuong-202110301600389672.jpg', 'Shellfish', FALSE),
(23, 'Ốc hương rang muối', 325000, 'Món chính', TRUE, 'Ốc hương rang muối giòn thơm, hấp dẫn trong từng miếng.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSUG-QP5tovwnkSjC4RPxvvW64vT5usWe5zRKiKd9qzcQ&s=10', 'Shellfish', FALSE),
(24, 'Cá thu sốt cà chua', 225000, 'Món chính', TRUE, 'Cá thu áp chảo kết hợp sốt cà chua chua ngọt ăn cùng với cơm trắng.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIOU1DQo7Y23Epa8Mz24iU5FLrcYb1Y4Ui7ZUQTax2_Q&s=10', 'Fish', FALSE),
(25, 'Cá hồi áp chảo sốt chanh dây', 395000, 'Món chính', TRUE, 'Cá hồi áp chảo vàng đều, kết hợp sốt chanh dây chua ngọt, mang đến hương vị tinh tế và hấp dẫn.', 'https://chefstudio.vn/uploads/r/cach-lam-ca-hoi-sot-chanh-leo-thom-ngon.jpg', 'Fish, Milk', FALSE),
(26, 'Cháo hải sản', 195000, 'Món chính', TRUE, 'Cháo nóng hổi với hải sản tươi và hương vị thanh ngọt.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtnzVLq_O4-f6Q7M5h5IkuoVxIDPI4UezW6HwYFQULPw&s=10', 'Shellfish', FALSE),
(27, 'Cơm chiên hải sản', 165000, 'Món chính', TRUE, 'Cơm chiên vàng đều cùng tôm, mực và trứng thơm ngon.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ4D2lMJP1Y0Aleu0jslS_vP0GfFdw1mmkYg3Spjk-1eA&s=10', 'Shellfish, Egg', FALSE),
(28, 'Tôm sú hấp nước dừa', 425000, 'Món chính', TRUE, 'Tôm sú hấp nước dừa giữ trọn vị ngọt và hương thơm tự nhiên.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRRMYT-f7lrpfv2Zj5uYcHIGwKOzr81XkNcid9NB_8YlA&s=10', 'Shellfish', FALSE),
(29, 'Tôm chiên bơ tỏi', 315000, 'Món chính', TRUE, 'Tôm chiên vàng giòn hòa quyện cùng bơ tỏi thơm béo.', 'https://cdn-i.vtcnews.vn/resize/th/upload/2024/10/03/tomchienbotois-11580919.png', 'Shellfish, Milk', FALSE),
(30, 'Mực xào chua ngọt', 265000, 'Món chính', TRUE, 'Mực tươi xào rau củ với sốt chua ngọt hài hòa.', 'https://i-giadinh.vnecdn.net/2021/03/31/muc1-1617182767-1435-1617182779.jpg', 'Shellfish', FALSE),
(31, 'Cá mú hấp Hồng Kông', 565000, 'Món chính', TRUE, 'Cá mú hấp xì dầu và gừng theo phong cách Hồng Kông.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR76WAMp8bA3-YSzofjutAfHTiTTvwuES0NEnLlfiz23A&s=10', 'Fish, Soy', FALSE),
(32, 'Ngao sốt thái', 215000, 'Món chính', TRUE, 'Ngao tươi sốt thái, thơm dịu và giữ nguyên vị ngọt, nước sốt thái chua ngọt đậm đà.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQvs0dElDV9HpnEs0tYYitzSpaVXMA15kPW2g4N4cLnGg&s=10', 'Shellfish', FALSE),
(33, 'Sò huyết nướng mỡ hành', 265000, 'Món chính', TRUE, 'Sò huyết nướng thơm béo cùng mỡ hành hấp dẫn.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT1f0N22gTd5krNaOWL9boDwUnK1Sqj4UHYRd86sfOhEg&s=10', 'Shellfish, Peanut', FALSE),
(34, 'Hàu nướng phô mai', 245000, 'Món chính', TRUE, 'Hàu tươi phủ phô mai béo ngậy, nướng vàng hấp dẫn.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMnWtYtmDJ_752ljmrFLSDcySv0FONNUYZEGiohb_XYw&s=10', 'Shellfish, Milk', FALSE),
(35, 'Cua rang me', 625000, 'Món chính', TRUE, 'Cua rang cùng sốt me chua ngọt đậm đà.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqVibdAbutiMSSGvl-MOuqw5jLGCPimM3rZsP2xEBBmQ&s=10', 'Shellfish', FALSE),
(36, 'Lẩu hải sản chua cay', 595000, 'Món chính', TRUE, 'Lẩu hải sản với nước dùng chua cay và nguyên liệu tươi ngon.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_3wUpu2uCMtzIqaFfIYPR_vnzxRhE3GHscuAAepFTfA&s=10', 'Shellfish', FALSE),
(37, 'Bạch tuộc nướng', 335000, 'Món chính', TRUE, 'Bạch tuộc nướng than hoa với độ giòn ngọt tự nhiên.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSlA-ERuGgo7gmv4KheNoQ3niCPD50eUYEzemipIhI5Yg&s=10', 'Shellfish', FALSE),
(38, 'Phở bò', 135000, 'Món chính', TRUE, 'Phở bò truyền thống với nước dùng hầm xương đậm vị.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRp63hqqANZeXyRQKFP_zSNKqvaT4iSdS0Lya21HtzS0A&s=10', 'Gluten', FALSE),
(39, 'Phở gà', 125000, 'Món chính', TRUE, 'Phở gà thanh ngọt cùng thịt gà mềm và rau thơm.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRyHPQAc0yF7zypjDfPfcEahLm5FjSoCcDoXhZh_OaCJA&s=10', 'Gluten', FALSE),
(40, 'Bún bò Huế', 125000, 'Món chính', TRUE, 'Bún bò Huế cay nhẹ với hương vị đặc trưng miền Trung.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0ZAe9xT_Kc5K0OMyRMqY5BswDwQYI9Wc0lNu9-H2rpw&s=10', NULL, FALSE),
(41, 'Bún chả Hà Nội', 145000, 'Món chính', TRUE, 'Thịt nướng thơm lừng ăn cùng bún và nước chấm truyền thống.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSMo3e19Uddx_I9WVoMfn6sRNQRb139oI2wrIxmQ_SlAg&s=10', 'Soy', FALSE),
(42, 'Bún thịt nướng', 135000, 'Món chính', TRUE, 'Thịt nướng đậm vị kết hợp bún tươi và rau sống.', 'https://cooponline.vn/tin-tuc/wp-content/uploads/2025/10/cach-lam-bun-thit-nuong-chuan-vi-sai-gon-thom-ngon-dam-da-kho-cuong.png', 'Peanut', FALSE),
(43, 'Cơm tấm sườn bì chả', 155000, 'Món chính', TRUE, 'Cơm tấm truyền thống với sườn nướng, bì và chả.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRbEqgR3k6WTSMqqYT0oBvseNzbV_dlNjnleBNoAGzJKQ&s=10', 'Egg', FALSE),
(44, 'Cơm gà xối mỡ', 145000, 'Món chính', TRUE, 'Gà chiên giòn rụm dùng cùng cơm trắng nóng.', 'https://static.vinwonders.com/production/2025/02/com-ga-xoi-mo-sai-gon-ut-minh.jpg', NULL, FALSE),
(45, 'Cơm chiên Dương Châu', 155000, 'Món chính', TRUE, 'Cơm chiên cùng hải sản, trứng và rau củ đầy màu sắc.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaEx810OnvI1ucAZhMW5JlgMa8LRSc0RvQ4ViskrCAvQ&s=10', 'Shellfish, Egg', FALSE),
(46, 'Bò lúc lắc', 285000, 'Món chính', TRUE, 'Thịt bò mềm áp chảo cùng rau củ và tiêu đen.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFtHwjMKEffc64iIpjsrUMsR1ITNs9qk4dVApiDQV4bw&s=10', 'Soy', FALSE),
(47, 'Bò né', 245000, 'Món chính', TRUE, 'Bò áp chảo nóng hổi dùng cùng trứng và bánh mì.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTcFFUwfBdCahr8pewDJpRaNkQTpm-S_tlKWX9KDX2CqQ&s=10', 'Gluten, Egg', FALSE),
(48, 'Gà nướng mật ong', 255000, 'Món chính', TRUE, 'Gà nướng vàng óng với lớp sốt mật ong thơm ngọt.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxGMNUJ1xcNkE7iN_eHpbEflgW8lTbZ0Lrilfea6fPCw&s=10', NULL, FALSE),
(49, 'Gà chiên nước mắm', 235000, 'Món chính', TRUE, 'Gà chiên giòn phủ nước mắm tỏi đậm đà.', 'https://tiki.vn/blog/wp-content/uploads/2023/07/thumb-1.jpeg', NULL, FALSE),
(50, 'Canh chua cá', 215000, 'Món chính', TRUE, 'Canh chua miền Nam với cá tươi và rau đặc trưng ăn cùng với cơm trắng.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQTxbS393egPaZuOA-lQ0GVTYWNCpsSi5s8LYHGDkF9eQ&s=10', 'Fish', FALSE),
(51, 'Pizza Hải sản', 325000, 'Món chính', TRUE, 'Pizza giòn thơm phủ hải sản tươi và phô mai mozzarella.', 'https://img.dominos.vn/Pizzaminsea-Hai-San-Nhiet-Doi-Xot-Tieu.jpg', 'Gluten, Milk, Shellfish', FALSE),
(52, 'Pizza Margherita', 255000, 'Món chính', TRUE, 'Pizza cổ điển với sốt cà chua và phô mai mozzarella.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRdKA-zTX8iiBW-l_rjHpWArsTBf22VRLM_DqA4wPzKmw&s=10', 'Gluten, Milk', FALSE),
(53, 'Spaghetti Seafood', 295000, 'Món chính', TRUE, 'Mì Ý sốt hải sản với hương vị đậm đà và tươi ngon.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSu2y44Agc3YwXdClTx6HMLU9-azbDJJOdNDXteLv-x5g&s=10', 'Gluten, Shellfish', FALSE),
(54, 'Chè ba màu', 55000, 'Tráng miệng', TRUE, 'Món chè truyền thống với nhiều loại đậu và nước cốt dừa.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTu6LrFBziAPnqKrnP4cbenKZc2K75Ep-GBYgo-ls4_bg&s=10', NULL, FALSE),
(55, 'Chè khúc bạch', 65000, 'Tráng miệng', TRUE, 'Chè khúc bạch thanh mát dùng cùng nhãn và hạnh nhân.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTSPecn767GitLnV9QGuMVOcbSR0PGcQIw-d5PYeaIPyw&s=10', 'Milk', FALSE),
(56, 'Kem dừa', 75000, 'Tráng miệng', TRUE, 'Kem dừa mịn màng với vị béo nhẹ tự nhiên.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYbXMHINuADHtuYZQxJ20Iw-Lxt0jyBwvnTN11fD7fXw&s=10', 'Milk', FALSE),
(57, 'Bánh flan', 55000, 'Tráng miệng', TRUE, 'Bánh flan mềm mịn với vị caramel ngọt dịu.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRSnF8AD_3JTqWnqbUx4VZYpKY3SRKremr3FM1t2dB5gg&s=10', 'Egg, Milk', FALSE),
(58, 'Trái cây theo mùa', 95000, 'Tráng miệng', TRUE, 'Tuyển chọn trái cây tươi ngon theo mùa.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSRjevrsK_VXNK_uASyCcCJxwmQUiTsh7JUjfS800aZ4A&s=10', NULL, FALSE),
(59, 'Kem vani', 75000, 'Tráng miệng', TRUE, 'Kem vani mịn béo với hương thơm dịu nhẹ.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQd3fhIwXSVsshPxkGb9gkC441QnIMjfILTSi-8lZl8PA&s=10', 'Milk', FALSE),
(60, 'Sữa chua', 45000, 'Tráng miệng', TRUE, 'Sữa chua việt quất chua ngọt tự nhiên, thanh mát và dễ thưởng thức.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSkXL1FGqhj4YVCK6uaMQe_7oIdDZwLEnQSG1dt8qWcAQ&s=10', 'Milk', FALSE),
(61, 'Bánh Mousse Chocolate', 90000, 'Tráng miệng', TRUE, 'Bánh mousse chocolate Bỉ mịn màng, đắng nhẹ.', 'https://i.ytimg.com/vi/pESVrDm6yIM/maxresdefault.jpg', 'Milk, egg', FALSE),
(62, 'Bánh Tiramisu', 95000, 'Tráng miệng', TRUE, 'Bánh Tiramisu mềm mịn với hương cà phê nhẹ, lớp kem mascarpone béo ngậy và bột cacao thơm, mang đến dư vị ngọt ngào sau bữa ăn.', 'https://thermomixvietnam.vn/wp-content/uploads/2021/08/tiramisu-truyen-thong.jpg', 'Milk, Egg, Gluten', FALSE),
(63, 'Nước dừa tươi', 55000, 'Đồ uống', TRUE, 'Nước dừa tươi mát được phục vụ nguyên trái.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRh0FZGaA6xavPbCzXxS7QyFohDVnGsLZk2zosFje_3eA&s=10', NULL, TRUE),
(64, 'Cam ép', 65000, 'Đồ uống', TRUE, 'Nước cam ép nguyên chất, giàu vitamin C.', 'https://www.sieuthidonglanh.com/wp-content/uploads/2023/03/Nuoc-ep-cam-giup-chong-lao-hoa-da-hieu-qua.png', NULL, TRUE),
(65, 'Nước chanh dây', 60000, 'Đồ uống', TRUE, 'Nước chanh dây chua ngọt, tươi mát.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT_VJrLPs5FB0AHsHZmJk8Bwy6Kb7dyTztazL_8SPSHkQ&s=10', NULL, TRUE),
(66, 'Nước ép dưa hấu', 65000, 'Đồ uống', TRUE, 'Nước ép dưa hấu nguyên chất, giải nhiệt hiệu quả.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTvHhFBXMrB3U9D5o-XWF9B0pY-bm444AO3WBbSwD6pXQ&s=10', NULL, TRUE),
(67, 'Sinh tố xoài', 75000, 'Đồ uống', TRUE, 'Sinh tố xoài sánh mịn với vị ngọt tự nhiên.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTJc0QXQ4IqYKElXcUr1GTWVxnWPhB2TK_OpOpQMMDhRw&s=10', 'Milk', TRUE),
(68, 'Sinh tố bơ', 80000, 'Đồ uống', TRUE, 'Sinh tố bơ ngọt nhẹ với vị béo ngậy cuốn hút.', 'https://img.freepik.com/premium-photo/avocado-smoothie-with-avocado-wooden-board-dark-background_490636-2675.jpg', 'Milk', TRUE),
(69, 'Sinh tố mãng cầu', 80000, 'Đồ uống', TRUE, 'Sinh tố mãng cầu chua ngọt hài hòa, mát lạnh', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQliOZ99dtOiJEG_cP78-xLuhfShPa1PSpmQXAsZaamKA&s=10', 'Milk', TRUE),
(70, 'Cà phê đen', 45000, 'Đồ uống', TRUE, 'Cà phê pha phin đậm đà theo phong cách Việt Nam.', 'https://vinbarista.com/uploads/news/10-loi-ich-bat-ngo-khi-uong-ca-phe-den-nguyen-chat-202504021427.jpg', NULL, TRUE),
(71, 'Cà phê sữa', 50000, 'Đồ uống', TRUE, 'Cà phê pha cùng sữa đặc, cân bằng vị đắng và ngọt.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnSg_F7K27YeJ5JcEOsW2QIdzpYQur2npcsOUgIxOQFw&s=10', 'Milk', TRUE),
(72, 'Cappuccino', 75000, 'Đồ uống', TRUE, 'Espresso kết hợp bọt sữa mịn theo phong cách Ý.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQB2t7LYP_hjvl-vBCTTS4NebRJGnX-OBlKcDnONH7X8Q&s=10', 'Milk', TRUE),
(73, 'Matcha Latte', 80000, 'Đồ uống', TRUE, 'Matcha Nhật Bản hòa quyện cùng sữa tươi và lớp foam mịn, mang đến hương vị thanh nhẹ, béo ngậy và cân bằng.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS2_gzK--VFUUdnjc-htZ4Kv2JUuX3MkY0X3rC6C4u9eA&s=10', 'Milk', TRUE),
(74, 'Trà đào', 55000, 'Đồ uống', TRUE, 'Trà đào thơm nhẹ với vị ngọt thanh dễ uống.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR4CCqp_lnl-X0wM_hqWJm2ZVdgXD8agHWfxzRnsWuHpQ&s=10', NULL, TRUE),
(75, 'Trà chanh', 45000, 'Đồ uống', TRUE, 'Trà chanh tươi mát với vị chua nhẹ sảng khoái.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqW5vUkX8PBxW9d5FL8_IQhmmOT8Km_VG4-Pi9RgNz4w&s=10', NULL, TRUE),
(76, 'Coca-Cola', 35000, 'Đồ uống', TRUE, 'Nước ngọt có ga phục vụ lạnh.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSecAEC3Qp39IHdyjLvoy2NTFBAfUjPQs4Ky2WY76V7Mg&s=10', NULL, TRUE),
(77, 'Pepsi', 35000, 'Đồ uống', TRUE, 'Nước ngọt có ga với hương vị quen thuộc.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSsbc5nPVtoJLUe7lTKlv5cpym3yvsQDrfe906ys17Psg&s=10', NULL, TRUE),
(78, 'Sprite', 35000, 'Đồ uống', TRUE, 'Nước ngọt vị chanh tươi mát.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSf33j4JK4YbOEIf0XD6cH9PfgyfqvFictJ-JyqfoAywA&s=10', NULL, TRUE),
(79, 'Soda chanh', 50000, 'Đồ uống', TRUE, 'Soda kết hợp nước cốt chanh tươi, sảng khoái.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0Qcpx_WfKUzQZVhpr26ocdX_3ol7Ibl9TmgpbSOFCHg&s=10', NULL, TRUE),
(80, 'Tiger Beer', 55000, 'Đồ uống', TRUE, 'Bia Tiger phục vụ lạnh, thích hợp cùng hải sản.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9YsRq1ocwyGXOUIFAUym39raCZxQ7GDixULXAo4vD7A&s=10', 'Gluten', TRUE),
(81, 'Heineken', 65000, 'Đồ uống', TRUE, 'Bia Heineken nhập khẩu với hương vị cân bằng.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRSIa9ayaH4xjGBGJk5vJVAqQF8o-sN3sOYOO1MaX5YNw&s=10', 'Gluten', TRUE),
(82, 'Bia thủ công', 95000, 'Đồ uống', TRUE, 'Bia thủ công với hương vị đặc trưng theo mùa.', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIlCLAN6vfvnzSB-PjslKfnpGOvOfbIv8mtG45I3y2gA&s=10', 'Gluten', TRUE),
(83, 'Nước khoáng Evian', 25000, 'Đồ uống', TRUE, 'Nước khoáng Evian Pháp tinh khiết và mát lạnh.', 'https://gangnamkong.co.kr/web/upload/NNEditor/20230424/33378a1a3af8ff1869c4b1faf8da8863.jpg', NULL, TRUE);


-- ── 18.5 Menu Item Days (Phân bổ thực đơn theo ngày) ───────────
INSERT INTO Menu_Item_Days (item_id, day_of_week) VALUES
-- MONDAY
(1, 'MONDAY'), (2, 'MONDAY'), (3, 'MONDAY'), (4, 'MONDAY'), (15, 'MONDAY'), (16, 'MONDAY'), (17, 'MONDAY'), (18, 'MONDAY'), (19, 'MONDAY'), (20, 'MONDAY'), (21, 'MONDAY'), (22, 'MONDAY'), (23, 'MONDAY'), (24, 'MONDAY'), (54, 'MONDAY'), (55, 'MONDAY'), (56, 'MONDAY'),

-- TUESDAY
(5, 'TUESDAY'), (6, 'TUESDAY'), (7, 'TUESDAY'), (8, 'TUESDAY'), (25, 'TUESDAY'), (26, 'TUESDAY'), (27, 'TUESDAY'), (28, 'TUESDAY'), (29, 'TUESDAY'), (30, 'TUESDAY'), (31, 'TUESDAY'), (32, 'TUESDAY'), (33, 'TUESDAY'), (34, 'TUESDAY'), (57, 'TUESDAY'), (58, 'TUESDAY'), (59, 'TUESDAY'),

-- WEDNESDAY
(9, 'WEDNESDAY'), (10, 'WEDNESDAY'), (11, 'WEDNESDAY'), (12, 'WEDNESDAY'), (35, 'WEDNESDAY'), (36, 'WEDNESDAY'), (37, 'WEDNESDAY'), (38, 'WEDNESDAY'), (39, 'WEDNESDAY'), (40, 'WEDNESDAY'), (41, 'WEDNESDAY'), (42, 'WEDNESDAY'), (43, 'WEDNESDAY'), (44, 'WEDNESDAY'), (60, 'WEDNESDAY'), (61, 'WEDNESDAY'), (62, 'WEDNESDAY'),

-- THURSDAY
(13, 'THURSDAY'), (14, 'THURSDAY'), (1, 'THURSDAY'), (2, 'THURSDAY'), (45, 'THURSDAY'), (46, 'THURSDAY'), (47, 'THURSDAY'), (48, 'THURSDAY'), (49, 'THURSDAY'), (50, 'THURSDAY'), (51, 'THURSDAY'), (52, 'THURSDAY'), (53, 'THURSDAY'), (15, 'THURSDAY'), (54, 'THURSDAY'), (55, 'THURSDAY'), (56, 'THURSDAY'),

-- FRIDAY
(3, 'FRIDAY'), (4, 'FRIDAY'), (5, 'FRIDAY'), (6, 'FRIDAY'), (16, 'FRIDAY'), (17, 'FRIDAY'), (18, 'FRIDAY'), (19, 'FRIDAY'), (20, 'FRIDAY'), (21, 'FRIDAY'), (22, 'FRIDAY'), (23, 'FRIDAY'), (24, 'FRIDAY'), (25, 'FRIDAY'), (57, 'FRIDAY'), (58, 'FRIDAY'), (59, 'FRIDAY'),

-- SATURDAY
(7, 'SATURDAY'), (8, 'SATURDAY'), (9, 'SATURDAY'), (10, 'SATURDAY'), (26, 'SATURDAY'), (27, 'SATURDAY'), (28, 'SATURDAY'), (29, 'SATURDAY'), (30, 'SATURDAY'), (31, 'SATURDAY'), (32, 'SATURDAY'), (33, 'SATURDAY'), (34, 'SATURDAY'), (35, 'SATURDAY'), (60, 'SATURDAY'), (61, 'SATURDAY'), (62, 'SATURDAY'),

-- SUNDAY
(11, 'SUNDAY'), (12, 'SUNDAY'), (13, 'SUNDAY'), (14, 'SUNDAY'), (36, 'SUNDAY'), (37, 'SUNDAY'), (38, 'SUNDAY'), (39, 'SUNDAY'), (40, 'SUNDAY'), (41, 'SUNDAY'), (42, 'SUNDAY'), (43, 'SUNDAY'), (44, 'SUNDAY'), (45, 'SUNDAY'), (54, 'SUNDAY'), (55, 'SUNDAY'), (56, 'SUNDAY');
