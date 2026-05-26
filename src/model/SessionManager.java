package model;

/** Luu tai khoan dang nhap hien tai (sau module a). */
public final class SessionManager {

    private static Account currentAccount;

    private SessionManager() {}

    public static void setCurrentAccount(Account account) {
        currentAccount = account;
    }

    public static Account getCurrentAccount() {
        return currentAccount;
    }

    public static void clear() {
        currentAccount = null;
    }

    public static boolean isAdmin() {
        return currentAccount != null && "admin".equalsIgnoreCase(currentAccount.getRole());
    }

    public static boolean isStudent() {
        return currentAccount != null && "student".equalsIgnoreCase(currentAccount.getRole());
    }
}
