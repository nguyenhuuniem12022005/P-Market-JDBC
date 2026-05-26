package test.unit;

import dao.AccountDAO;
import model.Account;

/** Chay: java -cp build/classes;lib/h2.jar test.unit.DbSmokeTest */
public class DbSmokeTest {
    public static void main(String[] args) throws Exception {
        Account a = new AccountDAO().login("admin@ptit.edu.vn", "admin123");
        System.out.println(a != null ? "OK: " + a.getFullName() : "LOGIN_FAIL");
    }
}
