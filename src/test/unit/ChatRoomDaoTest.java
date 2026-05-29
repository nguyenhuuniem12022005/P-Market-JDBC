package test.unit;

import dao.ChatRoomDAO;
import model.Account;
import model.ChatRoom;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ChatRoomDaoTest {

    private final ChatRoomDAO chatRoomDAO = new ChatRoomDAO();

    @Test
    public void testGetOrCreateRoomCreatesRoom() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("chat_room_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("chat_room_b"));

        ChatRoom room = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);
        Assert.assertNotNull(room);
        Assert.assertTrue(room.getId() > 0);
        Assert.assertEquals(2, room.getListMember().size());
    }

    @Test
    public void testGetOrCreateRoomReturnsExistingRoom() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("chat_existing_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("chat_existing_b"));

        ChatRoom room1 = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);
        ChatRoom room2 = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);
        Assert.assertEquals(room1.getId(), room2.getId());
    }

    @Test
    public void testGetRoomsByAccountAndOtherMember() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("chat_list_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("chat_list_b"));
        ChatRoom room = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);

        List<ChatRoom> rooms = chatRoomDAO.getRoomsByAccount(accountId1);
        Assert.assertTrue(rooms.stream().anyMatch(r -> r.getId() == room.getId()));

        Account other = chatRoomDAO.getOtherMember(room, accountId1);
        Assert.assertNotNull(other);
        Assert.assertEquals(accountId2, other.getId());
    }
}
