package view.user;

import view.user.UiHelper;

import model.SessionManager;
import view.chat.ChatListFrm;
import view.notification.StudentNotificationFrm;
import view.post.CreatePostFrm;
import view.post.SearchPostFrm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Trang chu Sinh vien — dieu huong module c, d, f, g + thong bao */
public class HomeStudentFrm extends JFrame implements ActionListener {

    private final JButton btnCreatePost = UiHelper.createMenuButton("Dang bai (c)");
    private final JButton btnSearch = UiHelper.createMenuButton("Tim kiem bai dang (d)");
    private final JButton btnChat = UiHelper.createMenuButton("Nhan tin (f)");
    private final JButton btnNotifications = UiHelper.createMenuButton("Thong bao cua toi");
    private final JButton btnLogout = UiHelper.createMenuButton("Dang xuat");

    public HomeStudentFrm() {
        super("P-Market — Sinh vien");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 380);
        setLocationRelativeTo(null);

        var user = SessionManager.getCurrentAccount();
        JLabel lbl = new JLabel("Xin chao, " + (user != null ? user.getFullName() : ""));
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));

        JPanel menu = new JPanel(new GridLayout(0, 1, 8, 8));
        menu.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        for (JButton b : new JButton[]{btnCreatePost, btnSearch, btnChat, btnNotifications, btnLogout}) {
            b.addActionListener(this);
            menu.add(b);
        }
        setLayout(new BorderLayout());
        add(lbl, BorderLayout.NORTH);
        add(menu, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCreatePost) {
            new CreatePostFrm().setVisible(true);
        } else if (e.getSource() == btnSearch) {
            new SearchPostFrm(false).setVisible(true);
        } else if (e.getSource() == btnChat) {
            new ChatListFrm().setVisible(true);
        } else if (e.getSource() == btnNotifications) {
            new StudentNotificationFrm().setVisible(true);
        } else if (e.getSource() == btnLogout) {
            SessionManager.clear();
            dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
