package test.unit;

import dao.AccountDAO;
import model.Account;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AccountDaoTest {

    private final AccountDAO accountDAO = new AccountDAO();

    @Test
    public void testLoginNotFound() throws Exception {
        Account account = accountDAO.login("not-found@stu.ptit.edu.vn", "wrong");
        Assert.assertNull(account);
    }

    @Test
    public void testLoginFound() throws Exception {
        Account account = accountDAO.login("admin@ptit.edu.vn", "admin123");
        Assert.assertNotNull(account);
        Assert.assertEquals("admin", account.getRole());
        Assert.assertEquals("ACTIVE", account.getStatus());
    }

    @Test
    public void testSearchAccountFound() throws Exception {
        List<Account> accounts = accountDAO.searchAccounts("Nguyen");
        Assert.assertNotNull(accounts);
        Assert.assertTrue(accounts.size() > 0);
        for (Account a : accounts) {
            String text = (a.getFullName() + " " + a.getEmail()).toLowerCase();
            Assert.assertTrue(text.contains("nguyen"));
        }
    }

    @Test
    public void testSearchAccountNotFound() throws Exception {
        List<Account> accounts = accountDAO.searchAccounts("xxxxxxxxxx_junit");
        Assert.assertNotNull(accounts);
        Assert.assertEquals(0, accounts.size());
    }

    @Test
    public void testLockAndUnlockAccount() throws Exception {
        String token = DbTestUtil.unique("lock_account");
        int accountId = DbTestUtil.insertStudent(token);

        Assert.assertTrue(accountDAO.lockAccount(accountId, "JUnit lock reason"));
        Account locked = accountDAO.findById(accountId);
        Assert.assertEquals("LOCKED", locked.getStatus());
        Assert.assertEquals("JUnit lock reason", locked.getBanReason());

        Assert.assertTrue(accountDAO.unlockAccount(accountId));
        Account active = accountDAO.findById(accountId);
        Assert.assertEquals("ACTIVE", active.getStatus());
        Assert.assertNull(active.getBanReason());
    }

    @Test
    public void testUpdatePasswordStoresHashAndLoginStillWorks() throws Exception {
        String token = DbTestUtil.unique("password_account");
        int accountId = DbTestUtil.insertStudent(token);
        String email = token + "@stu.ptit.edu.vn";
        String newPassword = "newPassword123";

        Assert.assertTrue(accountDAO.updatePassword(accountId, newPassword));
        Account updated = accountDAO.findById(accountId);
        Assert.assertNotEquals(newPassword, updated.getPassword());
        Assert.assertTrue(updated.getPassword().startsWith("pbkdf2$"));

        Assert.assertNotNull(accountDAO.login(email, newPassword));
        Assert.assertNull(accountDAO.login(email, "student123"));
    }
}
