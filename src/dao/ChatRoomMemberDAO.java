package dao;

import model.Account;
import model.ChatRoom;
import model.ChatRoomMember;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomMemberDAO extends DAO {

    public void addMember(int chatRoomId, int accountId) throws SQLException {
        String sql = "INSERT INTO tblChatRoomMember (chatRoomId, accountId) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, chatRoomId);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    public List<ChatRoomMember> getMembersByRoomId(int chatRoomId) throws SQLException {
        String sql = """
                SELECT m.*, a.fullName, a.email
                FROM tblChatRoomMember m
                JOIN tblAccount a ON m.accountId = a.id
                WHERE m.chatRoomId=?
                """;
        List<ChatRoomMember> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, chatRoomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChatRoomMember m = new ChatRoomMember();
                    m.setId(rs.getInt("id"));
                    m.setJoinedAt(toLocalDateTime(rs.getTimestamp("joinedAt")));
                    Account a = new Account();
                    a.setId(rs.getInt("accountId"));
                    a.setFullName(rs.getString("fullName"));
                    a.setEmail(rs.getString("email"));
                    m.setAccount(a);
                    ChatRoom cr = new ChatRoom();
                    cr.setId(chatRoomId);
                    m.setChatRoom(cr);
                    list.add(m);
                }
            }
        }
        return list;
    }
}
