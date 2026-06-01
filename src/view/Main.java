import dao.AccountDAO;
import view.user.LoginFrm;
import view.user.UiHelper;

import javax.swing.*;

/** Diem khoi chay ung dung P-Market Desktop. */
public class Main {

    public static void main(String[] args) {
        UiHelper.installLookAndFeel();
        try {
            new AccountDAO().upgradeLegacyPlainTextPasswords();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Khong the nang cap mat khau cu: " + ex.getMessage(),
                    "Loi", JOptionPane.ERROR_MESSAGE);
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrm login = new LoginFrm();
            login.setVisible(true);
        });
    }
}