package view.user;

import model.SessionManager;
import view.category.CategoryListFrm;
import view.notification.NotificationFrm;
import view.post.SearchPostFrm;
import view.report.ReportManageFrm;
import view.stat.StatDashboardFrm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Trang chủ Admin. */
public class AdminHomeFrm extends JFrame implements ActionListener {

    private final JButton btnManageAccount = UiHelper.createMenuButton("Quản lý tài khoản");
    private final JButton btnManageCategory = UiHelper.createMenuButton("Quản lý danh mục");
    private final JButton btnManageReport = UiHelper.createMenuButton("Duyệt báo cáo");
    private final JButton btnStats = UiHelper.createMenuButton("Thống kê");
    private final JButton btnNotification = UiHelper.createMenuButton("Thông báo hệ thống");
    private final JButton btnSearch = UiHelper.createMenuButton("Tìm kiếm bài đăng");
    private final JButton btnLogout = UiHelper.createMenuButton("Đăng xuất");

    public AdminHomeFrm() {
        super("P-Market - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 500);
        setLocationRelativeTo(null);

        var user = SessionManager.getCurrentAccount();
        JLabel lblWelcome = new JLabel("Xin chào, " + (user != null ? user.getFullName() : "Admin"));
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.BOLD, 18f));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(18, 28, 4, 28));

        JPanel menu = new JPanel(new GridLayout(0, 1, 10, 10));
        menu.setBorder(BorderFactory.createEmptyBorder(18, 48, 24, 48));
        for (JButton b : new JButton[] { btnManageAccount, btnManageCategory, btnManageReport,
                btnStats, btnNotification, btnSearch, btnLogout }) {
            b.addActionListener(this);
            menu.add(b);
        }

        setLayout(new BorderLayout(10, 10));
        add(lblWelcome, BorderLayout.NORTH);
        add(menu, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnManageAccount) {
            new AccountManageFrm().setVisible(true);
        } else if (src == btnManageCategory) {
            new CategoryListFrm().setVisible(true);
        } else if (src == btnManageReport) {
            new ReportManageFrm().setVisible(true);
        } else if (src == btnStats) {
            new StatDashboardFrm().setVisible(true);
        } else if (src == btnNotification) {
            new NotificationFrm().setVisible(true);
        } else if (src == btnSearch) {
            new SearchPostFrm(true).setVisible(true);
        } else if (src == btnLogout) {
            SessionManager.clear();
            dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
