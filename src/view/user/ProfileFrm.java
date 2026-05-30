package view.user;

import dao.AccountDAO;
import model.Account;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module a - Hiển thị hồ sơ cá nhân. */
public class ProfileFrm extends JFrame implements ActionListener {

    private final JTextArea outInfo = new JTextArea();
    private final JButton btnEdit = new JButton("Chỉnh sửa");
    private final JButton btnChangePassword = new JButton("Đổi mật khẩu");
    private final JButton btnChangeAvatar = new JButton("Đổi ảnh đại diện");
    private final AccountDAO accountDAO = new AccountDAO();

    public ProfileFrm() {
        super("Hồ sơ cá nhân");
        setSize(500, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outInfo.setEditable(false);
        outInfo.setFont(new Font("Consolas", Font.PLAIN, 13));
        outInfo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        btnEdit.addActionListener(this);
        btnChangePassword.addActionListener(this);
        btnChangeAvatar.addActionListener(this);
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(btnEdit);
        bottom.add(btnChangePassword);
        bottom.add(btnChangeAvatar);

        add(new JScrollPane(outInfo), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        loadProfile();
    }

    private void loadProfile() {
        try {
            Account me = accountDAO.getProfile(SessionManager.getCurrentAccount().getId());
            SessionManager.setCurrentAccount(me);
            outInfo.setText(String.format("""
                    Họ tên        : %s
                    Email         : %s
                    Số điện thoại : %s
                    Địa chỉ       : %s
                    Vai trò       : %s
                    Trạng thái    : %s
                    """,
                    me.getFullName(), me.getEmail(),
                    nv(me.getPhone()), nv(me.getAddress()),
                    me.getRole(), UiHelper.statusLabel(me.getStatus())));
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private String nv(String s) {
        return s == null ? "" : s;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Account me = SessionManager.getCurrentAccount();
        if (e.getSource() == btnEdit) {
            new EditProfileFrm(me, this::loadProfile).setVisible(true);
        } else if (e.getSource() == btnChangePassword) {
            changePassword(me);
        } else if (e.getSource() == btnChangeAvatar) {
            changeAvatar(me);
        }
    }

    private void changePassword(Account me) {
        JPasswordField oldPass = new JPasswordField();
        JPasswordField newPass = new JPasswordField();
        Object[] msg = {"Mật khẩu cũ:", oldPass, "Mật khẩu mới:", newPass};
        int ok = JOptionPane.showConfirmDialog(this, msg, "Đổi mật khẩu", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        String oldP = new String(oldPass.getPassword());
        String newP = new String(newPass.getPassword());
        if (newP.isBlank()) {
            UiHelper.showError(this, "Mật khẩu mới không được rỗng.");
            return;
        }
        try {
            if (!accountDAO.verifyPassword(me.getId(), oldP)) {
                UiHelper.showError(this, "Mật khẩu cũ không đúng.");
                return;
            }
            accountDAO.updatePassword(me.getId(), newP);
            SessionManager.setCurrentAccount(accountDAO.getProfile(me.getId()));
            UiHelper.showInfo(this, "Đổi mật khẩu thành công.");
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private void changeAvatar(Account me) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            String url = "uploads/" + chooser.getSelectedFile().getName();
            me.setAvatarUrl(url);
            accountDAO.updateProfile(me);
            UiHelper.showInfo(this, "Cập nhật ảnh đại diện thành công.");
            loadProfile();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
