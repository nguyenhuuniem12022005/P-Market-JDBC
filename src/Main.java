import view.user.LoginFrm;



import javax.swing.*;

/** Diem khoi chay ung dung P-Market Desktop. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            LoginFrm login = new LoginFrm();
            login.setVisible(true);
        });
    }
}
