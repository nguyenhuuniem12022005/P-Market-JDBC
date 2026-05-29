package view.user;

import dao.AccountDAO;
import model.Account;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module a — Hien thi ho so ca nhan */
public class ProfileFrm extends JFrame implements ActionListener {

    private final JTextArea outInfo = new JTextArea();
    private final JButton btnEdit = new JButton("Chinh sua");
    private final JButton btnChangePassword = new JButton("Doi mat khau");
    private final JButton btnChangeAvatar = new JButton("Doi anh dai dien");
    private final AccountDAO accountDAO = new AccountDAO();

    public ProfileFrm() {
        super("Ho so ca nhan");
        setSize(460, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outInfo.setEditable(false);
        outInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

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
                    Ho ten   : %s
                    Email    : %s
                    So dien thoai : %s
                    Dia chi  : %s
                    Vai tro  : %s
                    Trang thai : %s
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
        Object[] msg = {"Mat khau cu:", oldPass, "Mat khau moi:", newPass};
        int ok = JOptionPane.showConfirmDialog(this, msg, "Doi mat khau", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        String oldP = new String(oldPass.getPassword());
        String newP = new String(newPass.getPassword());
        if (!oldP.equals(me.getPassword())) {
            UiHelper.showError(this, "Mat khau cu khong dung.");
            return;
        }
        if (newP.isBlank()) {
            UiHelper.showError(this, "Mat khau moi khong duoc rong.");
            return;
        }
        try {
            accountDAO.updatePassword(me.getId(), newP);
            me.setPassword(newP);
            UiHelper.showInfo(this, "Doi mat khau thanh cong.");
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
            UiHelper.showInfo(this, "Cap nhat anh dai dien thanh cong.");
            loadProfile();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
