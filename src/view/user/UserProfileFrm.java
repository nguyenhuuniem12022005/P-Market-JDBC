package view.user;

import dao.AccountDAO;
import model.Account;
import model.SessionManager;
import view.report.ReportFormFrm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module g — Ho so nguoi dung khac (co the bao cao tai khoan) */
public class UserProfileFrm extends JFrame implements ActionListener {

    private final int accountId;
    private Account account;
    private final AccountDAO accountDAO = new AccountDAO();
    private final JTextArea outInfo = new JTextArea();
    private final JButton btnReport = new JButton("Bao cao tai khoan");
    private final JButton btnClose = new JButton("Dong");

    public UserProfileFrm(int accountId) {
        super("Ho so nguoi dung #" + accountId);
        this.accountId = accountId;
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outInfo.setEditable(false);
        outInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel actions = new JPanel(new FlowLayout());
        if (SessionManager.isStudent()) {
            btnReport.addActionListener(this);
            actions.add(btnReport);
        }
        btnClose.addActionListener(this);
        actions.add(btnClose);

        add(new JScrollPane(outInfo), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
        loadProfile();
    }

    private void loadProfile() {
        try {
            account = accountDAO.getAccountDetails(accountId);
            if (account == null) {
                UiHelper.showError(this, "Khong tim thay tai khoan.");
                dispose();
                return;
            }
            outInfo.setText(String.format("""
                    Ho ten : %s
                    Email  : %s
                    Trang thai : %s
                    """,
                    account.getFullName(), account.getEmail(),
                    UiHelper.statusLabel(account.getStatus())));
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose) {
            dispose();
        } else if (e.getSource() == btnReport && account != null) {
            int myId = SessionManager.getCurrentAccount().getId();
            if (account.getId() == myId) {
                UiHelper.showError(this, "Khong the bao cao chinh minh.");
                return;
            }
            new ReportFormFrm(account).setVisible(true);
        }
    }
}
