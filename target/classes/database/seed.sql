-- Du lieu mau de test cac module a -> k

INSERT INTO tblAccount (fullName, email, password, phone, address, role, status, avatarUrl) VALUES
('Quan tri vien', 'admin@ptit.edu.vn', 'admin123', '0900000001', 'PTIT', 'admin', 'active', ''),
('Nguyen Van Anh', 'anhnv.b21ce009@stu.ptit.edu.vn', 'student123', '0912345678', 'Ha Noi', 'student', 'active', ''),
('Tran Thi Binh', 'binhtt.b22ce001@stu.ptit.edu.vn', 'student123', '0923456789', 'Ha Noi', 'student', 'active', ''),
('Le Van Cuong', 'cuonglv.b21ce010@stu.ptit.edu.vn', 'student123', '0934567890', 'Hai Phong', 'student', 'banned', '');

UPDATE tblAccount SET banReason = 'Dang ban giao trinh gia mao' WHERE email = 'cuonglv.b21ce010@stu.ptit.edu.vn';

INSERT INTO tblCategory (parentId, name) VALUES
(NULL, 'Hoc tap'),
(1, 'Giao trinh'),
(1, 'Do dung hoc tap'),
(NULL, 'Dien tu');

INSERT INTO tblPost (accountId, categoryId, title, description, price, quantity, status) VALUES
(2, 2, 'Giao trinh Giai tich 1', 'Sach moi 90%, co highlight nhe', 85000, 1, 'available'),
(2, 3, 'Ban phim co Logitech', 'Dung tot, co day USB', 250000, 1, 'available'),
(3, 2, 'De thi lap - Noi dung cam', 'Ban de thi cu (bai test bao cao)', 50000, 1, 'available');

INSERT INTO tblImage (postId, imageUrl) VALUES
(1, 'uploads/sample_book.png'),
(2, 'uploads/sample_keyboard.png'),
(3, 'uploads/sample_exam.png');

INSERT INTO tblReport (accountId, targetType, targetId, reason, status) VALUES
(2, 'post', 3, 'Noi dung cam', 'pending');

INSERT INTO tblReportEvidence (reportId, imageUrl) VALUES
(1, 'uploads/evidence1.png');

INSERT INTO tblNotification (title, content) VALUES
('Chao mung P-Market', 'He thong cho sinh vien PTIT da san sang!');

INSERT INTO tblUserNotification (notificationId, accountId, isRead) VALUES
(1, 2, FALSE),
(1, 3, TRUE);
