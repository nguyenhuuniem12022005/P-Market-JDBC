package view.user;

import view.user.UiHelper;

import model.SessionManager;
import view.notification.NotificationFrm;
import view.post.SearchPostFrm;
import view.report.ManageReportFrm;
import view.stat.StatDashboardFrm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Trang chu Admin — dieu huong cac module b, h, i, k */
public class HomeAdminFrm extends JFrame implements ActionListener {

    private final JButton btnManageAccount = UiHelper.createMenuButton("Quan ly tai khoan (b)");
    private final JButton btnManageReport = UiHelper.createMenuButton("Duyet bao cao (h)");
    private final JButton btnStats = UiHelper.createMenuButton("Xem thong ke (i)");
    private final JButton btnNotification = UiHelper.createMenuButton("Gui thong bao (k)");
    private final JButton btnSearch = UiHelper.createMenuButton("Tim kiem bai dang (d)");
    private final JButton btnLogout = UiHelper.createMenuButton("Dang xuat");

    public HomeAdminFrm() {
        super("P-Market — Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 420);
        setLocationRelativeTo(null);

        var user = SessionManager.getCurrentAccount();
        JLabel lblWelcome = new JLabel("Xin chao, " + (user != null ? user.getFullName() : "Admin"));
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.BOLD, 16f));

        JPanel menu = new JPanel(new GridLayout(0, 1, 8, 8));
        menu.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40));
        for (JButton b : new JButton[]{btnManageAccount, btnManageReport, btnStats,
                btnNotification, btnSearch, btnLogout}) {
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
            new ManageAccountFrm().setVisible(true);
        } else if (src == btnManageReport) {
            new ManageReportFrm().setVisible(true);
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
