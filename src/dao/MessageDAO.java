package dao;

import model.Account;
import model.ChatRoom;
import model.Message;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends DAO {

    /** Module f: gui tin nhan */
    public Message sendMessage(int chatRoomId, int accountId, String content, String imageUrl) throws SQLException {
        String sql = "INSERT INTO tblMessage (chatRoomId, accountId, content, imageUrl) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, chatRoomId);
            ps.setInt(2, accountId);
            ps.setString(3, content);
            ps.setString(4, imageUrl);
            ps.executeUpdate();
        }
        Message m = new Message();
        m.setContent(content);
        m.setImageUrl(imageUrl);
        Account a = new Account();
        a.setId(accountId);
        m.setAccount(a);
        ChatRoom cr = new ChatRoom();
        cr.setId(chatRoomId);
        m.setChatRoom(cr);
        return m;
    }

    public List<Message> getMessagesByRoom(int chatRoomId) throws SQLException {
        String sql = """
                SELECT m.*, a.fullName
                FROM tblMessage m
                JOIN tblAccount a ON m.accountId = a.id
                WHERE m.chatRoomId=?
                ORDER BY m.sentAt ASC
                """;
        List<Message> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, chatRoomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message();
                    msg.setId(rs.getInt("id"));
                    msg.setContent(rs.getString("content"));
                    msg.setImageUrl(rs.getString("imageUrl"));
                    msg.setSentAt(toLocalDateTime(rs.getTimestamp("sentAt")));
                    Account a = new Account();
                    a.setId(rs.getInt("accountId"));
                    a.setFullName(rs.getString("fullName"));
                    msg.setAccount(a);
                    list.add(msg);
                }
            }
        }
        return list;
    }
}
