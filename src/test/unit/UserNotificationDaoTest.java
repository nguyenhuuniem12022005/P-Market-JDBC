package test.unit;

import dao.AccountDAO;
import dao.NotificationDAO;
import dao.UserNotificationDAO;
import model.Account;
import model.Notification;
import model.UserNotification;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class UserNotificationDaoTest {

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final UserNotificationDAO userNotificationDAO = new UserNotificationDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    @Test
    public void testBroadcastToAllAndGetByAccount() throws Exception {
        Notification n = notificationDAO.createNotification(DbTestUtil.unique("Kiểm thử broadcast"), "Nội dung broadcast");
        List<Account> students = accountDAO.getAllStudentIds();
        Assert.assertTrue(students.size() > 0);

        Assert.assertTrue(userNotificationDAO.broadcastToAll(n.getId(), students));
        List<UserNotification> notifications = userNotificationDAO.getByAccount(students.get(0).getId());

        Assert.assertTrue(notifications.stream()
                .anyMatch(un -> un.getNotification() != null && un.getNotification().getId() == n.getId()));
    }

    @Test
    public void testMarkAsRead() throws Exception {
        Notification n = notificationDAO.createNotification(DbTestUtil.unique("Kiểm thử read"), "Nội dung read");
        List<Account> students = accountDAO.getAllStudentIds();
        userNotificationDAO.broadcastToAll(n.getId(), List.of(students.get(0)));

        UserNotification created = userNotificationDAO.getByAccount(students.get(0).getId()).stream()
                .filter(un -> un.getNotification() != null && un.getNotification().getId() == n.getId())
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(created);
        Assert.assertFalse(created.isRead());

        userNotificationDAO.markAsRead(created.getId());
        UserNotification read = userNotificationDAO.getByAccount(students.get(0).getId()).stream()
                .filter(un -> un.getId() == created.getId())
                .findFirst()
                .orElse(null);

        Assert.assertNotNull(read);
        Assert.assertTrue(read.isRead());
    }
}
