import view.user.LoginFrm;
import view.user.UiHelper;



import javax.swing.*;

/** Diem khoi chay ung dung P-Market Desktop. */
public class Main {

    public static void main(String[] args) {
        UiHelper.installLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            LoginFrm login = new LoginFrm();
            login.setVisible(true);
        });
    }
}
