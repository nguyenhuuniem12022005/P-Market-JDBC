package test.unit;

import dao.ChatRoomDAO;
import dao.MessageDAO;
import model.ChatRoom;
import model.Message;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class MessageDaoTest {

    private final ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    @Test
    public void testSendAndGetMessagesByRoom() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("message_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("message_b"));
        ChatRoom room = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);
        String content = DbTestUtil.unique("Noi dung tin nhan");

        Message sent = messageDAO.sendMessage(room.getId(), accountId1, content, null);
        Assert.assertEquals(content, sent.getContent());

        List<Message> messages = messageDAO.getMessagesByRoom(room.getId());
        Assert.assertTrue(messages.stream().anyMatch(m -> content.equals(m.getContent())));
    }
}
