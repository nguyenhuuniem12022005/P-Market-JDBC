package test.unit;

import dao.ChatRoomDAO;
import dao.ChatRoomMemberDAO;
import model.ChatRoom;
import model.ChatRoomMember;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ChatRoomMemberDaoTest {

    private final ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
    private final ChatRoomMemberDAO memberDAO = new ChatRoomMemberDAO();

    @Test
    public void testGetMembersByRoomId() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("member_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("member_b"));
        ChatRoom room = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);

        List<ChatRoomMember> members = memberDAO.getMembersByRoomId(room.getId());
        Assert.assertEquals(2, members.size());
        Assert.assertTrue(members.stream().anyMatch(m -> m.getAccount().getId() == accountId1));
        Assert.assertTrue(members.stream().anyMatch(m -> m.getAccount().getId() == accountId2));
    }
}
