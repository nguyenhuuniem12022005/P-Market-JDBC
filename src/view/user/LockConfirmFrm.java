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
        super(parent, "Xác nhận khóa tài khoản", true);
        this.account = account;
        this.onSuccess = onSuccess;
        setSize(450, 320);
        setLocationRelativeTo(parent);

        JPanel info = new JPanel(new GridLayout(4, 2, 8, 8));
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        info.add(new JLabel("Tài khoản:"));
        info.add(new JLabel(account.getFullName() + " (ID: " + account.getId() + ")"));
        info.add(new JLabel("Email:"));
        info.add(new JLabel(account.getEmail()));
        info.add(new JLabel("Trạng thái:"));
        info.add(new JLabel(UiHelper.statusLabel(account.getStatus())));
        info.add(new JLabel("Lý do khóa (*):"));
        inReason.setLineWrap(true);
        info.add(new JScrollPane(inReason));

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
        String reason = inReason.getText().trim();
        if (reason.isEmpty()) {
            UiHelper.showError(this, "Vui lòng nhập lý do khóa tài khoản.");
            return;
        }
        try {
            accountDAO.lockAccount(account.getId(), reason);
            UiHelper.showInfo(this, "Khóa tài khoản thành công.");
            dispose();
            if (onSuccess != null) onSuccess.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
