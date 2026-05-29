package dao;

import model.Account;
import model.ChatRoom;
import model.ChatRoomMember;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomDAO extends DAO {

    private final ChatRoomMemberDAO memberDAO = new ChatRoomMemberDAO();

    /** Module f: tim hoac tao phong chat 1-1 */
    public ChatRoom getOrCreateRoom(int accountId1, int accountId2) throws SQLException {
        String sql = """
                SELECT cr.id FROM tblChatRoom cr
                JOIN tblChatRoomMember m1 ON cr.id = m1.chatRoomId AND m1.accountId = ?
                JOIN tblChatRoomMember m2 ON cr.id = m2.chatRoomId AND m2.accountId = ?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId1);
            ps.setInt(2, accountId2);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChatRoom room = new ChatRoom();
                    room.setId(rs.getInt("id"));
                    room.setListMember(memberDAO.getMembersByRoomId(room.getId()));
                    return room;
                }
            }
        }
        ChatRoom room = new ChatRoom();
        String insert = "INSERT INTO tblChatRoom DEFAULT VALUES";
        try (PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    room.setId(keys.getInt(1));
                }
            }
        }
        memberDAO.addMember(room.getId(), accountId1);
        memberDAO.addMember(room.getId(), accountId2);
        room.setListMember(memberDAO.getMembersByRoomId(room.getId()));
        return room;
    }

    /** Module f: lay chi tiet mot phong chat theo id */
    public ChatRoom getRoomById(int roomId) throws SQLException {
        String sql = "SELECT id, createdAt FROM tblChatRoom WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChatRoom room = new ChatRoom();
                    room.setId(rs.getInt("id"));
                    room.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));
                    room.setListMember(memberDAO.getMembersByRoomId(room.getId()));
                    return room;
                }
            }
        }
        return null;
    }

    public List<ChatRoom> getRoomsByAccount(int accountId) throws SQLException {
        String sql = """
                SELECT DISTINCT cr.id, cr.createdAt
                FROM tblChatRoom cr
                JOIN tblChatRoomMember m ON cr.id = m.chatRoomId
                WHERE m.accountId=?
                ORDER BY cr.createdAt DESC
                """;
        List<ChatRoom> rooms = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChatRoom room = new ChatRoom();
                    room.setId(rs.getInt("id"));
                    room.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));
                    List<ChatRoomMember> members = memberDAO.getMembersByRoomId(room.getId());
                    room.setListMember(members);
                    rooms.add(room);
                }
            }
        }
        return rooms;
    }

    public Account getOtherMember(ChatRoom room, int currentAccountId) throws SQLException {
        for (ChatRoomMember m : room.getListMember()) {
            if (m.getAccount().getId() != currentAccountId) {
                return m.getAccount();
            }
        }
        return null;
    }
}
