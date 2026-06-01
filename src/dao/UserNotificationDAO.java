package dao;

import model.Account;
import model.Notification;
import model.UserNotification;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserNotificationDAO extends DAO {

    public boolean broadcastToAll(int notificationId, List<Account> students) throws SQLException {
        String sql = "INSERT INTO tblUserNotification (notificationId, accountId, isRead) VALUES (?, ?, FALSE)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (Account s : students) {
                ps.setInt(1, notificationId);
                ps.setInt(2, s.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return true;
    }

    public List<UserNotification> getByAccount(int accountId) throws SQLException {
        String sql = """
                SELECT un.*, n.title, n.content, n.createdAt AS notiCreated
                FROM tblUserNotification un
                JOIN tblNotification n ON un.notificationId = n.id
                WHERE un.accountId=?
                ORDER BY n.createdAt DESC
                """;
        java.util.List<UserNotification> list = new java.util.ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserNotification un = new UserNotification();
                    un.setId(rs.getInt("id"));
                    un.setRead(rs.getBoolean("isRead"));
                    un.setReadAt(toLocalDateTime(rs.getTimestamp("readAt")));
                    Notification n = new Notification();
                    n.setId(rs.getInt("notificationId"));
                    n.setTitle(rs.getString("title"));
                    n.setContent(rs.getString("content"));
                    n.setCreatedAt(toLocalDateTime(rs.getTimestamp("notiCreated")));
                    un.setNotification(n);
                    list.add(un);
                }
            }
        }
        return list;
    }

    public void markAsRead(int userNotificationId) throws SQLException {
        String sql = "UPDATE tblUserNotification SET isRead=TRUE, readAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userNotificationId);
            ps.executeUpdate();
        }
    }
}
