package view.user;

import view.user.UiHelper;

import dao.AccountDAO;
import model.Account;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Module a — Dang nhap (View).
 */
public class LoginFrm extends JFrame implements ActionListener {

    private final JTextField inEmail = new JTextField(25);
    private final JPasswordField inPassword = new JPasswordField(25);
    private final JButton btnLogin = new JButton("Dang nhap");
    private final AccountDAO accountDAO = new AccountDAO();

    public LoginFrm() {
        super("P-Market — Dang nhap");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(inEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mat khau:"), gbc);
        gbc.gridx = 1;
        panel.add(inPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        btnLogin.addActionListener(this);
        panel.add(btnLogin, gbc);

        JLabel hint = new JLabel("<html>Admin: admin@ptit.edu.vn / admin123<br>"
                + "SV: anhnv.b21ce009@stu.ptit.edu.vn / student123</html>");
        hint.setFont(hint.getFont().deriveFont(11f));
        gbc.gridy = 3;
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
            UiHelper.showError(this, "Vui long nhap email va mat khau.");
            return;
        }
        try {
            Account account = accountDAO.login(email, password);
            if (account == null) {
                UiHelper.showError(this, "Sai email hoac mat khau.");
                return;
            }
            if ("banned".equalsIgnoreCase(account.getStatus())) {
                UiHelper.showError(this, "Tai khoan bi khoa. Ly do: "
                        + (account.getBanReason() != null ? account.getBanReason() : ""));
                return;
            }
            SessionManager.setCurrentAccount(account);
            dispose();
            if ("admin".equalsIgnoreCase(account.getRole())) {
                new HomeAdminFrm().setVisible(true);
            } else {
                new HomeStudentFrm().setVisible(true);
            }
        } catch (Exception ex) {
            UiHelper.showError(this, "Loi ket noi CSDL: " + ex.getMessage());
        }
    }
}
