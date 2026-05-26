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
            "7 ngay gan nhat", "30 ngay gan nhat", "Tat ca"
    });
    private final JTextArea outAccount = new JTextArea(6, 40);
    private final JTextArea outPost = new JTextArea(6, 40);
    private final JButton btnRefresh = new JButton("Cap nhat");
    private final JButton btnExport = new JButton("Xuat bao cao");
    private final AccountStatDAO accountStatDAO = new AccountStatDAO();
    private final PostStatDAO postStatDAO = new PostStatDAO();

    public StatDashboardFrm() {
        super("Thong ke he thong");
        setSize(560, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outAccount.setEditable(false);
        outPost.setEditable(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Ky thong ke:"));
        top.add(inPeriod);
        btnRefresh.addActionListener(this);
        btnExport.addActionListener(this);
        top.add(btnRefresh);
        top.add(btnExport);
        inPeriod.addActionListener(this);

        JPanel center = new JPanel(new GridLayout(2, 1, 8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        center.add(titled("Thong ke tai khoan", outAccount));
        center.add(titled("Thong ke bai dang", outPost));

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
                Tu %s den %s
                Tai khoan moi: %d
                Tai khoan bi khoa (tong): %d
                Tong sinh vien: %d
                """,
                s.getStartDate(), s.getEndDate(),
                s.getNewAccounts(), s.getBannedAccounts(), s.getTotalAccounts());
    }

    private String formatPostStat(PostStat s) {
        return String.format("""
                Tu %s den %s
                Bai dang moi: %d
                Bai da ban (tong): %d
                Tong bai dang: %d
                """,
                s.getStartDate(), s.getEndDate(),
                s.getNewPosts(), s.getSoldPosts(), s.getTotalPosts());
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
