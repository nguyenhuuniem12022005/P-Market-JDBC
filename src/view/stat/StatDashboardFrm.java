package view.stat;

import view.user.UiHelper;

import dao.AccountStatDAO;
import dao.PostStatDAO;
import model.AccountStat;
import model.PostStat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

/** Module i — Xem thong ke */
public class StatDashboardFrm extends JFrame implements ActionListener {

    private final JComboBox<String> inPeriod = new JComboBox<>(new String[]{
            "7 ngày gần nhất", "30 ngày gần nhất", "Tất cả"
    });
    private final JTextArea outAccount = new JTextArea(6, 40);
    private final JTextArea outPost = new JTextArea(6, 40);
    private final JButton btnRefresh = new JButton("Cập nhật");
    private final JButton btnExport = new JButton("Xuất báo cáo");
    private final AccountStatDAO accountStatDAO = new AccountStatDAO();
    private final PostStatDAO postStatDAO = new PostStatDAO();

    public StatDashboardFrm() {
        super("Thống kê hệ thống");
        setSize(560, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outAccount.setEditable(false);
        outPost.setEditable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Kỳ thống kê:"));
        top.add(inPeriod);
        btnRefresh.addActionListener(this);
        btnExport.addActionListener(this);
        top.add(btnRefresh);
        top.add(btnExport);
        inPeriod.addActionListener(this);

        JPanel center = new JPanel(new GridLayout(2, 1, 8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        center.add(titled("Thống kê tài khoản", outAccount));
        center.add(titled("Thống kê bài đăng", outPost));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        loadStats();
    }

    private JPanel titled(String title, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(new JScrollPane(c), BorderLayout.CENTER);
        return p;
    }

    private LocalDate[] getDateRange() {
        LocalDate end = LocalDate.now();
        LocalDate start = switch (inPeriod.getSelectedIndex()) {
            case 0 -> end.minusDays(7);
            case 1 -> end.minusDays(30);
            default -> end.minusYears(10);
        };
        return new LocalDate[]{start, end};
    }

    private void loadStats() {
        try {
            LocalDate[] range = getDateRange();
            AccountStat as = accountStatDAO.getAccountStat(range[0], range[1]);
            PostStat ps = postStatDAO.getPostStat(range[0], range[1]);
            outAccount.setText(formatAccountStat(as));
            outPost.setText(formatPostStat(ps));
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private String formatAccountStat(AccountStat s) {
        return String.format("""
                Từ %s đến %s
                Tài khoản mới: %d
                Tài khoản bị khóa (tổng): %d
                Tổng sinh viên: %d
                """,
                s.getStartDate(), s.getEndDate(),
                s.getNewAccounts(), s.getBannedAccounts(), s.getTotalAccounts());
    }

    private String formatPostStat(PostStat s) {
        return String.format("""
                Từ %s đến %s
                Bài đăng mới: %d
                Bài đã xóa (tổng): %d
                Tổng bài đang hoạt động: %d
                """,
                s.getStartDate(), s.getEndDate(),
                s.getNewPosts(), s.getDeletedPosts(), s.getTotalPosts());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh || e.getSource() == inPeriod) {
            loadStats();
        } else if (e.getSource() == btnExport) {
            new ExportReportFrm().setVisible(true);
        }
    }
}
