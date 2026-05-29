package view.user;

import view.user.UiHelper;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module b — Xac nhan khoa tai khoan (LockConfirmFrm) */
public class LockConfirmFrm extends JDialog implements ActionListener {

    private final Account account;
    private final Runnable onSuccess;
    private final JTextArea inReason = new JTextArea(4, 30);
    private final AccountDAO accountDAO = new AccountDAO();

    public LockConfirmFrm(JFrame parent, Account account, Runnable onSuccess) {
        super(parent, "Xac nhan khoa tai khoan", true);
        this.account = account;
        this.onSuccess = onSuccess;
        setSize(450, 320);
        setLocationRelativeTo(parent);

        JPanel info = new JPanel(new GridLayout(4, 2, 8, 8));
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        info.add(new JLabel("Tai khoan:"));
        info.add(new JLabel(account.getFullName() + " (ID: " + account.getId() + ")"));
        info.add(new JLabel("Email:"));
        info.add(new JLabel(account.getEmail()));
        info.add(new JLabel("Trang thai:"));
        info.add(new JLabel(UiHelper.statusLabel(account.getStatus())));
        info.add(new JLabel("Ly do khoa (*):"));
        inReason.setLineWrap(true);
        info.add(new JScrollPane(inReason));

        JButton btnConfirm = new JButton("Xac nhan khoa");
        JButton btnCancel = new JButton("Quay lai");
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
        if ("Quay lai".equals(src.getText())) {
            dispose();
            return;
        }
        String reason = inReason.getText().trim();
        if (reason.isEmpty()) {
            UiHelper.showError(this, "Vui long nhap ly do khoa tai khoan.");
            return;
        }
        try {
            accountDAO.lockAccount(account.getId(), reason);
            UiHelper.showInfo(this, "Khoa tai khoan thanh cong.");
            dispose();
            if (onSuccess != null) onSuccess.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
