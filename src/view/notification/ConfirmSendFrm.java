package view.notification;

import view.user.UiHelper;

import dao.AccountDAO;
import dao.NotificationDAO;
import dao.UserNotificationDAO;
import model.Account;
import model.Notification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/** Module k — Xac nhan gui thong bao */
public class ConfirmSendFrm extends JDialog implements ActionListener {

    private final Notification notification;
    private final Runnable onSuccess;
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final UserNotificationDAO userNotificationDAO = new UserNotificationDAO();

    public ConfirmSendFrm(JFrame parent, Notification notification, Runnable onSuccess) {
        super(parent, "Xac nhan gui thong bao", true);
        this.notification = notification;
        this.onSuccess = onSuccess;
        setSize(450, 280);
        setLocationRelativeTo(parent);

        JLabel warning = new JLabel("<html><font color=red>Hanh dong nay se gui thong bao den "
                + "TAT CA tai khoan sinh vien dang hoat dong.<br>Ban co chac chan muon gui?</font></html>");
        JTextArea summary = new JTextArea();
        summary.setEditable(false);
        summary.setText("Tieu de: " + notification.getTitle() + "\n\n" + notification.getContent());

        JButton btnConfirm = new JButton("Xac nhan gui");
        JButton btnCancel = new JButton("Huy");
        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(e -> dispose());

        setLayout(new BorderLayout(10, 10));
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(warning, BorderLayout.NORTH);
        p.add(new JScrollPane(summary), BorderLayout.CENTER);
        add(p, BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        bottom.add(btnConfirm);
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Notification saved = notificationDAO.createNotification(
                    notification.getTitle(), notification.getContent());
            List<Account> students = accountDAO.getAllStudentIds();
            if (students.isEmpty()) {
                UiHelper.showInfo(this, "Da luu thong bao nhung chua co sinh vien nhan.");
            } else {
                userNotificationDAO.broadcastToAll(saved.getId(), students);
                UiHelper.showInfo(this, "Da gui thong bao thanh cong den " + students.size() + " sinh vien.");
            }
            dispose();
            if (onSuccess != null) onSuccess.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
