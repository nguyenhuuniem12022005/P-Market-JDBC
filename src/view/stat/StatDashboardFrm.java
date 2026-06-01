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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StatDashboardFrm extends JFrame implements ActionListener {

    private final JTextField inStartDate = new JTextField(10);
    private final JTextField inEndDate = new JTextField(10);
    private final JTextArea outAccount = new JTextArea(6, 40);
    private final JTextArea outPost = new JTextArea(6, 40);
    private final JButton btnExport = new JButton("Xuất báo cáo");
    private final AccountStatDAO accountStatDAO = new AccountStatDAO();
    private final PostStatDAO postStatDAO = new PostStatDAO();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public StatDashboardFrm() {
        super("Thống kê hệ thống");
        setSize(560, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outAccount.setEditable(false);
        outPost.setEditable(false);

        inStartDate.setToolTipText("dd/MM/yyyy");
        inEndDate.setToolTipText("dd/MM/yyyy");

        FocusAdapter autoLoadOnBlur = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                maybeLoadStats();
            }
        };
        inStartDate.addFocusListener(autoLoadOnBlur);
        inEndDate.addFocusListener(autoLoadOnBlur);
        inStartDate.addActionListener(this);
        inEndDate.addActionListener(this);
        btnExport.addActionListener(this);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Ngày bắt đầu:"));
        top.add(inStartDate);
        top.add(new JLabel("Ngày kết thúc:"));
        top.add(inEndDate);
        top.add(btnExport);

        JPanel center = new JPanel(new GridLayout(2, 1, 8, 8));
        center.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        center.add(titled("Thống kê tài khoản", outAccount));
        center.add(titled("Thống kê bài đăng", outPost));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        maybeLoadStats();
    }

    private JPanel titled(String title, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(new JScrollPane(c), BorderLayout.CENTER);
        return p;
    }

    private LocalDate[] getDateRange() {
        String startText = inStartDate.getText().trim();
        String endText = inEndDate.getText().trim();
        if (startText.isEmpty() || endText.isEmpty()) {
            return null;
        }
        if (startText.length() < 10 || endText.length() < 10) {
            return null;
        }
        try {
            LocalDate start = LocalDate.parse(startText, dateFormatter);
            LocalDate end = LocalDate.parse(endText, dateFormatter);
            if (start.isAfter(end)) {
                UiHelper.showError(this, "Ngày bắt đầu không được sau ngày kết thúc.");
                return null;
            }
            return new LocalDate[] { start, end };
        } catch (DateTimeParseException ex) {
            UiHelper.showError(this, "Ngày không đúng định dạng (dd/MM/yyyy).");
            return null;
        }
    }

    private void loadStats(LocalDate start, LocalDate end) {
        try {
            AccountStat as = accountStatDAO.getAccountStat(start, end);
            PostStat ps = postStatDAO.getPostStat(start, end);
            outAccount.setText(formatAccountStat(as));
            outPost.setText(formatPostStat(ps));
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private void maybeLoadStats() {
        LocalDate[] range = getDateRange();
        if (range == null) {
            outAccount.setText("");
            outPost.setText("");
            return;
        }
        loadStats(range[0], range[1]);
    }

    private String formatAccountStat(AccountStat s) {
        return String.format("""
                Từ %s đến %s
                Tài khoản mới: %d
                Tài khoản bị khóa (tổng): %d
                Tổng sinh viên: %d
                """,
                s.getStartDate().format(dateFormatter), s.getEndDate().format(dateFormatter),
                s.getNewAccounts(), s.getBannedAccounts(), s.getTotalAccounts());
    }

    private String formatPostStat(PostStat s) {
        return String.format("""
                Từ %s đến %s
                Bài đăng mới: %d
                Bài đã xoá (tổng): %d
                Tổng bài đăng: %d
                """,
                s.getStartDate().format(dateFormatter), s.getEndDate().format(dateFormatter),
                s.getNewPosts(), s.getDeletedPosts(), s.getTotalPosts());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == inStartDate || e.getSource() == inEndDate) {
            maybeLoadStats();
        } else if (e.getSource() == btnExport) {
            new ExportReportFrm().setVisible(true);
        }
    }
}
