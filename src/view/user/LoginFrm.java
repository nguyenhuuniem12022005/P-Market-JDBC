package view.user;

import dao.AccountDAO;
import model.Account;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module a - Đăng nhập. */
public class LoginFrm extends JFrame implements ActionListener {

    private final JTextField inEmail = new JTextField(25);
    private final JPasswordField inPassword = new JPasswordField(25);
    private final JButton btnLogin = new JButton("Đăng nhập");
    private final AccountDAO accountDAO = new AccountDAO();

    public LoginFrm() {
        super("P-Market - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("P-Market", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(inEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        panel.add(inPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnLogin.addActionListener(this);
        panel.add(btnLogin, gbc);

        JLabel hint = new JLabel("<html>Admin: admin@ptit.edu.vn / admin123<br>"
                + "SV: anhnv.b21ce009@stu.ptit.edu.vn / student123</html>");
        hint.setFont(hint.getFont().deriveFont(11f));
        gbc.gridy = 4;
        panel.add(hint, gbc);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != btnLogin) {
            return;
        }
        String email = inEmail.getText().trim();
        String password = new String(inPassword.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            UiHelper.showError(this, "Vui lòng nhập email và mật khẩu.");
            return;
        }
        try {
            Account account = accountDAO.login(email, password);
            if (account == null) {
                UiHelper.showError(this, "Sai email hoặc mật khẩu.");
                return;
            }
            if (Account.STATUS_BANNED.equalsIgnoreCase(account.getStatus())) {
                UiHelper.showError(this, "Tài khoản bị khóa. Lý do: "
                        + (account.getBanReason() != null ? account.getBanReason() : ""));
                return;
            }
            SessionManager.setCurrentAccount(account);
            dispose();
            if ("admin".equalsIgnoreCase(account.getRole())) {
                new AdminHomeFrm().setVisible(true);
            } else {
                new StudentHomeFrm().setVisible(true);
            }
        } catch (Exception ex) {
            UiHelper.showError(this, "Lỗi kết nối CSDL: " + ex.getMessage());
        }
    }
}
