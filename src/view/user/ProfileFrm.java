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
 
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(btnEdit);
        bottom.add(btnChangePassword);


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
            new ChangePasswordFrm(me, this::loadProfile).setVisible(true);
        }
    }
}