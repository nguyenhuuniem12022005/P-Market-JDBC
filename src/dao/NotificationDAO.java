package dao;

import model.Notification;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends DAO {

    /** Module k */
    public Notification createNotification(String title, String content) throws SQLException {
        String sql = "INSERT INTO tblNotification (title, content) VALUES (?, ?)";
        Notification n = new Notification(title, content);
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    n.setId(keys.getInt(1));
                }
            }
        }
        return n;
    }

    public List<Notification> getNotificationHistory() throws SQLException {
        String sql = "SELECT * FROM tblNotification ORDER BY createdAt DESC";
        List<Notification> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                n.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));
                list.add(n);
            }
        }
        return list;
    }
}
