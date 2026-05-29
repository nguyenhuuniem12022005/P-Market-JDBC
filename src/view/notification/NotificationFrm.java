package view.notification;

import view.user.UiHelper;

import dao.NotificationDAO;
import model.Notification;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Module k — Gui thong bao */
public class NotificationFrm extends JFrame implements ActionListener {

    private final JTextField inTitle = new JTextField(30);
    private final JTextArea inContent = new JTextArea(6, 30);
    private final JButton btnSend = new JButton("Gửi thông báo");
    private final JButton btnHistory = new JButton("Lịch sử đã gửi");
    private final JTable tblHistory = new JTable();
    private final DefaultTableModel historyModel;
    private final NotificationDAO notificationDAO = new NotificationDAO();

    public NotificationFrm() {
        super("Gửi thông báo hệ thống");
        setSize(620, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        historyModel = new DefaultTableModel(new String[]{"ID", "Tiêu đề", "Thời gian"}, 0);
        tblHistory.setModel(historyModel);

        JPanel compose = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        compose.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        compose.add(inTitle, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        compose.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1;
        compose.add(new JScrollPane(inContent), gbc);
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSend.addActionListener(this);
        btnHistory.addActionListener(this);
        btns.add(btnSend);
        btns.add(btnHistory);
        compose.add(btns, gbc);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, compose, new JScrollPane(tblHistory));
        split.setResizeWeight(0.45);
        add(split);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSend) {
            String title = inTitle.getText().trim();
            String content = inContent.getText().trim();
            if (title.isEmpty() || content.isEmpty()) {
                UiHelper.showError(this, "Vui lòng nhập đầy đủ tiêu đề và nội dung.");
                return;
            }
            Notification n = new Notification(title, content);
            new ConfirmSendFrm(this, n, () -> {
                inTitle.setText("");
                inContent.setText("");
                loadHistory();
            }).setVisible(true);
        } else if (e.getSource() == btnHistory) {
            loadHistory();
        }
    }

    private void loadHistory() {
        try {
            List<Notification> list = notificationDAO.getNotificationHistory();
            historyModel.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Notification n : list) {
                String time = n.getCreatedAt() != null ? n.getCreatedAt().format(fmt) : "";
                historyModel.addRow(new Object[]{n.getId(), n.getTitle(), time});
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
