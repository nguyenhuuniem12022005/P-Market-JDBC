package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Tao CSDL 11 bang theo CONTEXT_FOR_AI.md (tuong thich H2). */
public final class SchemaInitializer {

    private SchemaInitializer() {}

    public static void init(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblAccount (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  fullName VARCHAR(200) NOT NULL,
                  email VARCHAR(200) NOT NULL UNIQUE,
                  password VARCHAR(200) NOT NULL,
                  phone VARCHAR(50),
                  address VARCHAR(500),
                  role VARCHAR(20) NOT NULL,
                  status VARCHAR(20) NOT NULL,
                  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  avatarUrl VARCHAR(500),
                  banReason VARCHAR(500)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblCategory (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  parentId INT,
                  name VARCHAR(200) NOT NULL,
                  FOREIGN KEY (parentId) REFERENCES tblCategory(id)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblPost (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  accountId INT NOT NULL,
                  categoryId INT NOT NULL,
                  title VARCHAR(300) NOT NULL,
                  description CLOB,
                  price DOUBLE NOT NULL,
                  quantity INT NOT NULL DEFAULT 1,
                  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  status VARCHAR(20) NOT NULL,
                  FOREIGN KEY (accountId) REFERENCES tblAccount(id),
                  FOREIGN KEY (categoryId) REFERENCES tblCategory(id)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblImage (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  postId INT NOT NULL,
                  imageUrl VARCHAR(500) NOT NULL,
                  FOREIGN KEY (postId) REFERENCES tblPost(id) ON DELETE CASCADE
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblReport (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  accountId INT NOT NULL,
                  targetType VARCHAR(20) NOT NULL,
                  targetId INT NOT NULL,
                  reason VARCHAR(500) NOT NULL,
                  status VARCHAR(20) NOT NULL,
                  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (accountId) REFERENCES tblAccount(id)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblReportEvidence (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  reportId INT NOT NULL,
                  imageUrl VARCHAR(500) NOT NULL,
                  FOREIGN KEY (reportId) REFERENCES tblReport(id) ON DELETE CASCADE
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblNotification (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  title VARCHAR(300) NOT NULL,
                  content CLOB NOT NULL,
                  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblUserNotification (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  notificationId INT NOT NULL,
                  accountId INT NOT NULL,
                  isRead BOOLEAN DEFAULT FALSE,
                  readAt TIMESTAMP,
                  FOREIGN KEY (notificationId) REFERENCES tblNotification(id) ON DELETE CASCADE,
                  FOREIGN KEY (accountId) REFERENCES tblAccount(id)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblChatRoom (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblChatRoomMember (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  chatRoomId INT NOT NULL,
                  accountId INT NOT NULL,
                  joinedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (chatRoomId) REFERENCES tblChatRoom(id) ON DELETE CASCADE,
                  FOREIGN KEY (accountId) REFERENCES tblAccount(id)
                )
                """);
            st.execute("""
                CREATE TABLE IF NOT EXISTS tblMessage (
                  id INT AUTO_INCREMENT PRIMARY KEY,
                  chatRoomId INT NOT NULL,
                  accountId INT NOT NULL,
                  content VARCHAR(2000),
                  imageUrl VARCHAR(500),
                  sentAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY (chatRoomId) REFERENCES tblChatRoom(id) ON DELETE CASCADE,
                  FOREIGN KEY (accountId) REFERENCES tblAccount(id)
                )
                """);
        }
    }

    public static void seedIfEmpty(Connection con) throws SQLException {
        try (var ps = con.prepareStatement("SELECT COUNT(*) FROM tblAccount");
             var rs = ps.executeQuery()) {
            rs.next();
            if (rs.getInt(1) > 0) {
                return;
            }
        }
        try (Statement st = con.createStatement()) {
            st.execute("""
                INSERT INTO tblAccount (fullName, email, password, phone, address, role, status, avatarUrl) VALUES
                ('Quan tri vien', 'admin@ptit.edu.vn', 'admin123', '0900000001', 'PTIT', 'admin', 'active', ''),
                ('Nguyen Van Anh', 'anhnv.b21ce009@stu.ptit.edu.vn', 'student123', '0912345678', 'Ha Noi', 'student', 'active', ''),
                ('Tran Thi Binh', 'binhtt.b22ce001@stu.ptit.edu.vn', 'student123', '0923456789', 'Ha Noi', 'student', 'active', ''),
                ('Le Van Cuong', 'cuonglv.b21ce010@stu.ptit.edu.vn', 'student123', '0934567890', 'Hai Phong', 'student', 'banned', '')
                """);
            st.execute("UPDATE tblAccount SET banReason = 'Dang ban giao trinh gia mao' WHERE email = 'cuonglv.b21ce010@stu.ptit.edu.vn'");
            st.execute("""
                INSERT INTO tblCategory (parentId, name) VALUES
                (NULL, 'Hoc tap'), (1, 'Giao trinh'), (1, 'Do dung hoc tap'), (NULL, 'Dien tu')
                """);
            st.execute("""
                INSERT INTO tblPost (accountId, categoryId, title, description, price, quantity, status) VALUES
                (2, 2, 'Giao trinh Giai tich 1', 'Sach moi 90%', 85000, 1, 'available'),
                (2, 3, 'Ban phim co Logitech', 'Dung tot', 250000, 1, 'available'),
                (3, 2, 'De thi lap - Noi dung cam', 'Ban de thi cu', 50000, 1, 'available')
                """);
            st.execute("""
                INSERT INTO tblImage (postId, imageUrl) VALUES
                (1, 'uploads/sample_book.png'), (2, 'uploads/sample_keyboard.png'), (3, 'uploads/sample_exam.png')
                """);
            st.execute("""
                INSERT INTO tblReport (accountId, targetType, targetId, reason, status) VALUES
                (2, 'post', 3, 'Noi dung cam', 'pending')
                """);
            st.execute("INSERT INTO tblReportEvidence (reportId, imageUrl) VALUES (1, 'uploads/evidence1.png')");
            st.execute("INSERT INTO tblNotification (title, content) VALUES ('Chao mung P-Market', 'He thong da san sang!')");
            st.execute("INSERT INTO tblUserNotification (notificationId, accountId, isRead) VALUES (1, 2, FALSE), (1, 3, TRUE)");
        }
    }
}
