package view.user;

import dao.AccountDAO;
import model.Account;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module a — Chinh sua thong tin ca nhan (so dien thoai, dia chi) */
public class EditProfileFrm extends JFrame implements ActionListener {

    private final Account account;
    private final Runnable onSaved;
    private final JTextField inPhone = new JTextField(20);
    private final JTextField inAddress = new JTextField(20);
    private final JButton btnSave = new JButton("Lưu thay đổi");
    private final JButton btnCancel = new JButton("Hủy");
    private final AccountDAO accountDAO = new AccountDAO();

    public EditProfileFrm(Account account, Runnable onSaved) {
        super("Chỉnh sửa hồ sơ");
        this.account = account;
        this.onSaved = onSaved;
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        inPhone.setText(account.getPhone());
        inAddress.setText(account.getAddress());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        form.add(inPhone, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        form.add(inAddress, gbc);

        btnSave.addActionListener(this);
        btnCancel.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(btnSave);
        bottom.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String phone = inPhone.getText().trim();
        if (phone.isEmpty()) {
            UiHelper.showError(this, "Vui lòng nhập số điện thoại.");
            return;
        }
        try {
            account.setPhone(phone);
            account.setAddress(inAddress.getText().trim());
            accountDAO.updateProfile(account);
            SessionManager.setCurrentAccount(account);
            UiHelper.showInfo(this, "Cập nhật hồ sơ thành công.");
            dispose();
            if (onSaved != null) onSaved.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
