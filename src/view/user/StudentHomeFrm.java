package view.user;

import model.SessionManager;
import view.chat.ListRoomFrm;
import view.notification.StudentNotificationFrm;
import view.post.ManagePostFrm;
import view.post.SearchPostFrm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Trang chủ sinh viên. */
public class StudentHomeFrm extends JFrame implements ActionListener {

    private final JButton btnProfile = UiHelper.createMenuButton("Hồ sơ cá nhân");
    private final JButton btnManagePost = UiHelper.createMenuButton("Quản lý bài đăng");
    private final JButton btnSearch = UiHelper.createMenuButton("Danh sách bài đăng");
    private final JButton btnChat = UiHelper.createMenuButton("Nhắn tin");
    private final JButton btnNotifications = UiHelper.createMenuButton("Thông báo của tôi");
    private final JButton btnLogout = UiHelper.createMenuButton("Đăng xuất");

    public StudentHomeFrm() {
        super("P-Market - Sinh viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 460);
        setLocationRelativeTo(null);

        var user = SessionManager.getCurrentAccount();
        JLabel lbl = new JLabel("Xin chào, " + (user != null ? user.getFullName() : ""));
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        lbl.setBorder(BorderFactory.createEmptyBorder(18, 28, 4, 28));

        JPanel menu = new JPanel(new GridLayout(0, 1, 10, 10));
        menu.setBorder(BorderFactory.createEmptyBorder(18, 48, 24, 48));
        for (JButton b : new JButton[]{btnProfile, btnManagePost, btnSearch, btnChat,
                btnNotifications, btnLogout}) {
            b.addActionListener(this);
            menu.add(b);
        }
        setLayout(new BorderLayout());
        add(lbl, BorderLayout.NORTH);
        add(menu, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnProfile) {
            new ProfileMenuFrm(this).setVisible(true);
        } else if (e.getSource() == btnManagePost) {
            new ManagePostFrm().setVisible(true);
        } else if (e.getSource() == btnSearch) {
            new SearchPostFrm(false).setVisible(true);
        } else if (e.getSource() == btnChat) {
            new ListRoomFrm().setVisible(true);
        } else if (e.getSource() == btnNotifications) {
            new StudentNotificationFrm().setVisible(true);
        } else if (e.getSource() == btnLogout) {
            SessionManager.clear();
            dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
