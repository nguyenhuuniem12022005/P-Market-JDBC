package test.unit;

import dao.AccountDAO;
import dao.NotificationDAO;
import model.Notification;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class NotificationDaoTest {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Test
    public void testCreateNotificationAndHistory() throws Exception {
        String title = DbTestUtil.unique("JUnit notification");
        Notification n = notificationDAO.createNotification(title, "Noi dung test");

        Assert.assertTrue(n.getId() > 0);
        List<Notification> history = notificationDAO.getNotificationHistory();
        Assert.assertTrue(history.stream().anyMatch(item -> title.equals(item.getTitle())));
    }
}
