package test.unit;

import dao.AccountDAO;
import model.Account;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
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

    String token = DbTestUtil.unique("login_found");
    DbTestUtil.insertStudent(token);

    Account account =
            accountDAO.login(
                    token + "@stu.ptit.edu.vn",
                    "student123");

    Assert.assertNotNull(account);
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

        Assert.assertTrue(accountDAO.lockAccount(accountId, "Kiểm thử lock reason"));
        Account locked = accountDAO.findById(accountId);
        Assert.assertEquals(Account.STATUS_BANNED, locked.getStatus());
        Assert.assertEquals("Kiểm thử lock reason", locked.getBanReason());

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

    // getProfile 
    @Test
    public void testGetProfileFound() throws SQLException {

        AccountDAO dao = new AccountDAO();

        Account result = dao.getProfile(1);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getId());

        return;
    }

    @Test
    public void testGetProfileNotFound() throws SQLException {

        AccountDAO dao = new AccountDAO();

        Account result = dao.getProfile(-1);

        Assert.assertNull(result);

        return;
    }

    // updateProfile 
   @Test
    public void testUpdateProfileSuccess() throws SQLException {

        AccountDAO dao = new AccountDAO();

        Account acc = dao.getProfile(1);

        acc.setPhone("0988888888");
        acc.setAddress("Hà Nội");

        boolean result = dao.updateProfile(acc);

        Assert.assertTrue(result);

        return;
    }

    @Test
    public void testUpdateProfileFail() throws SQLException {

        AccountDAO dao = new AccountDAO();

        Account acc = new Account();

        acc.setId(99999);
        acc.setPhone("0123456789");
        acc.setAddress("ABC");

        boolean result = dao.updateProfile(acc);

        Assert.assertFalse(result);

        return;
    }

    // verifyPassword
@Test
public void testVerifyPasswordCorrect() throws Exception {

    String token = DbTestUtil.unique("verify_password");
    int accountId = DbTestUtil.insertStudent(token);

    Account account =
            accountDAO.login(
                    token + "@stu.ptit.edu.vn",
                    "student123");

    Assert.assertNotNull(account);

    boolean result =
            accountDAO.verifyPassword(
                    accountId,
                    "student123");

    Assert.assertTrue(result);
}
@Test
public void testVerifyPasswordWrong() throws Exception {

    String token = DbTestUtil.unique("verify_wrong");
    int accountId = DbTestUtil.insertStudent(token);

    boolean result =
            accountDAO.verifyPassword(
                    accountId,
                    "wrongpassword");

    Assert.assertFalse(result);
}

    // updatePassword
  @Test
public void testUpdatePasswordSuccess() throws Exception {

    String token = DbTestUtil.unique("update_password");
    int accountId = DbTestUtil.insertStudent(token);

    boolean result =
            accountDAO.updatePassword(
                    accountId,
                    "newPassword123");

    Assert.assertTrue(result);

    Assert.assertNotNull(
            accountDAO.login(
                    token + "@stu.ptit.edu.vn",
                    "newPassword123"));
}
    @Test
    public void testUpdatePasswordFail()
            throws SQLException {

        AccountDAO dao = new AccountDAO();

        boolean result =
                dao.updatePassword(
                        99999,
                        "newPassword123");

        Assert.assertFalse(result);

        return;
    }


}
