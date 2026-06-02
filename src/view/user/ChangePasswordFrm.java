
package view.user;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChangePasswordFrm extends JFrame implements ActionListener {

    private final JPasswordField txtOldPassword = new JPasswordField(20);
    private final JPasswordField txtNewPassword = new JPasswordField(20);

    private final JButton btnOk = new JButton("OK");
    private final JButton btnCancel = new JButton("Cancel");

    private final AccountDAO accountDAO = new AccountDAO();
    private final Account account;
    private final Runnable onSuccess;

    public ChangePasswordFrm(Account account, Runnable onSuccess) {
        super("Doi mat khau");

        this.account = account;
        this.onSuccess = onSuccess;

        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Mat khau cu:"));
        formPanel.add(txtOldPassword);

        formPanel.add(new JLabel("Mat khau moi:"));
        formPanel.add(txtNewPassword);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);

        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

 @Override
public void actionPerformed(ActionEvent e) {

    if (e.getSource() == btnCancel) {
        dispose();
        return;
    }

    if (e.getSource() == btnOk) {

        String oldPassword =
                new String(txtOldPassword.getPassword());

        String newPassword =
                new String(txtNewPassword.getPassword());

        try {

            if (!accountDAO.verifyPassword(
                    account.getId(),
                    oldPassword)) {

                UiHelper.showError(this,
                        "Mat khau cu khong dung.");
                return;
            }

            if (newPassword.isBlank()) {
                UiHelper.showError(this,
                        "Mat khau moi khong duoc rong.");
                return;
            }

            boolean success =
                    accountDAO.updatePassword(
                            account.getId(),
                            newPassword);

            if (!success) {
                UiHelper.showError(this,
                        "Doi mat khau that bai.");
                return;
            }

            account.setPassword(newPassword);

            UiHelper.showInfo(this,
                    "Doi mat khau thanh cong.");

            if (onSuccess != null) {
                onSuccess.run();
            }

            dispose();

        } catch (Exception ex) {
            UiHelper.showError(this,
                    ex.getMessage());
        }
    }
}
}