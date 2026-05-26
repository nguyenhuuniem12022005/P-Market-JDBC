package view.notification;

import view.user.UiHelper;

import dao.UserNotificationDAO;
import model.UserNotification;
import model.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Xem thong bao da nhan (sinh vien) */
public class StudentNotificationFrm extends JFrame {

    public StudentNotificationFrm() {
        super("Thong bao cua toi");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Tieu de", "Da doc", "Thoi gian"}, 0);
        JTable table = new JTable(model);
        UserNotificationDAO dao = new UserNotificationDAO();

        try {
            List<UserNotification> list = dao.getByAccount(SessionManager.getCurrentAccount().getId());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (UserNotification un : list) {
                String title = un.getNotification() != null ? un.getNotification().getTitle() : "";
                String time = un.getNotification() != null && un.getNotification().getCreatedAt() != null
                        ? un.getNotification().getCreatedAt().format(fmt) : "";
                model.addRow(new Object[]{un.getId(), title, un.isRead() ? "Da doc" : "Chua doc", time});
            }
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = table.getSelectedRow();
                    if (row >= 0 && row < list.size()) {
                        UserNotification un = list.get(row);
                        String content = un.getNotification() != null ? un.getNotification().getContent() : "";
                        JOptionPane.showMessageDialog(StudentNotificationFrm.this, content,
                                un.getNotification().getTitle(), JOptionPane.INFORMATION_MESSAGE);
                        try {
                            dao.markAsRead(un.getId());
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
