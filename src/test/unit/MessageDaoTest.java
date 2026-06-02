package test.unit;

import dao.ChatRoomDAO;
import dao.MessageDAO;
import model.ChatRoom;
import model.Message;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class MessageDaoTest {

    private final ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    @Test
    public void testSendAndGetMessagesByRoom() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("message_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("message_b"));
        ChatRoom room = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);
        String content = DbTestUtil.unique("Nội dung tin nhắn");

        Message sent = messageDAO.sendMessage(room.getId(), accountId1, content, null);
        Assert.assertEquals(content, sent.getContent());

        List<Message> messages = messageDAO.getMessagesByRoom(room.getId());
        Assert.assertTrue(messages.stream().anyMatch(m -> content.equals(m.getContent())));
    }

    @Test
    public void testSendImageOnlyMessage() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("message_img_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("message_img_b"));
        ChatRoom room = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);
        String imageUrl = "uploads/junit_chat.png";

        Message sent = messageDAO.sendMessage(room.getId(), accountId1, null, imageUrl);
        Assert.assertNull(sent.getContent());
        Assert.assertEquals(imageUrl, sent.getImageUrl());

        List<Message> messages = messageDAO.getMessagesByRoom(room.getId());
        Assert.assertTrue(messages.stream().anyMatch(m -> imageUrl.equals(m.getImageUrl())));
    }

    @Test
    public void testSendEmptyMessage() throws Exception {
        int accountId1 = DbTestUtil.insertStudent(DbTestUtil.unique("msg_empty_a"));
        int accountId2 = DbTestUtil.insertStudent(DbTestUtil.unique("msg_empty_b"));
        ChatRoom room = chatRoomDAO.getOrCreateRoom(accountId1, accountId2);

        try {
            messageDAO.sendMessage(room.getId(), accountId1, "", null);
            Assert.fail("Kỳ vọng hàm sendMessage phải chặn lại và ném ra lỗi");

        } catch (SQLException ex) {
            Assert.assertNotNull("Ngoại lệ ném ra bị thiếu message", ex.getMessage());
            Assert.assertTrue("Thông báo lỗi chưa chính xác. Thực tế nhận được: " + ex.getMessage(),
                    ex.getMessage().contains("Tin nhắn phải có nội dung hoặc ảnh"));
        }
    }
}


