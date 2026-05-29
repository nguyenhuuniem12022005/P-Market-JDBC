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
        super(parent, "Xác nhận gửi thông báo", true);
        this.notification = notification;
        this.onSuccess = onSuccess;
        setSize(450, 280);
        setLocationRelativeTo(parent);

        JLabel warning = new JLabel("<html><font color=red>Hành động này sẽ gửi thông báo đến "
                + "TẤT CẢ tài khoản sinh viên đang hoạt động.<br>Bạn có chắc chắn muốn gửi?</font></html>");
        JTextArea summary = new JTextArea();
        summary.setEditable(false);
        summary.setText("Tiêu đề: " + notification.getTitle() + "\n\n" + notification.getContent());

        JButton btnConfirm = new JButton("Xác nhận gửi");
        JButton btnCancel = new JButton("Hủy");
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
                UiHelper.showInfo(this, "Đã lưu thông báo nhưng chưa có sinh viên nhận.");
            } else {
                userNotificationDAO.broadcastToAll(saved.getId(), students);
                UiHelper.showInfo(this, "Đã gửi thông báo thành công đến " + students.size() + " sinh viên.");
            }
            dispose();
            if (onSuccess != null) onSuccess.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
