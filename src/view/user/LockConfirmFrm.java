package view.user;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module b — Xác nhận khóa tài khoản (LockConfirmFrm) */
public class LockConfirmFrm extends JDialog implements ActionListener {

    private final Account account;
    private final Runnable onSuccess;
    private final AccountDAO accountDAO = new AccountDAO();

    public LockConfirmFrm(JFrame parent, Account account, Runnable onSuccess) {
        super(parent, "Xác nhận khóa tài khoản", true);
        this.account = account;
        this.onSuccess = onSuccess;
        setSize(450, 320);
        setLocationRelativeTo(parent);

        JPanel info = new JPanel(new GridLayout(3, 2, 8, 8));
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        info.add(new JLabel("Tài khoản:"));
        info.add(new JLabel(account.getFullName() + " (ID: " + account.getId() + ")"));
        info.add(new JLabel("Email:"));
        info.add(new JLabel(account.getEmail()));
        info.add(new JLabel("Trạng thái:"));
        info.add(new JLabel(UiHelper.statusLabel(account.getStatus())));

        JButton btnConfirm = new JButton("Xác nhận khóa");
        JButton btnCancel = new JButton("Quay lại");
        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(this);

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(btnConfirm);
        bottom.add(btnCancel);

        setLayout(new BorderLayout());
        add(info, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton src = (JButton) e.getSource();
        if ("Quay lại".equals(src.getText())) {
            dispose();
            return;
        }
        try {
            accountDAO.lockAccount(account.getId(), null);
            UiHelper.showInfo(this, "Khóa tài khoản thành công.");
            dispose();
            if (onSuccess != null)
                onSuccess.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
