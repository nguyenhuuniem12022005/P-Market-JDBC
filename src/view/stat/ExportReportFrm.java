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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/** Module i — Xuat bao cao CSV */
public class ExportReportFrm extends JFrame implements ActionListener {

    private final JTextField inStart = new JTextField("2026-01-01", 12);
    private final JTextField inEnd = new JTextField(LocalDate.now().toString(), 12);
    private final JButton btnDownload = new JButton("Tải về (CSV)");
    private final JButton btnCancel = new JButton("Hủy");
    private final AccountStatDAO accountStatDAO = new AccountStatDAO();
    private final PostStatDAO postStatDAO = new PostStatDAO();

    public ExportReportFrm() {
        super("Xuất báo cáo");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        form.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        form.add(inStart);
        form.add(new JLabel("Đến ngày:"));
        form.add(inEnd);
        btnDownload.addActionListener(this);
        btnCancel.addActionListener(this);
        form.add(btnDownload);
        form.add(btnCancel);
        add(form);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            dispose();
            return;
        }
        try {
            LocalDate start = LocalDate.parse(inStart.getText().trim());
            LocalDate end = LocalDate.parse(inEnd.getText().trim());
            AccountStat as = accountStatDAO.exportReport(start, end);
            PostStat ps = postStatDAO.exportReport(start, end);
            String csv = buildCsv(as, ps);
            Path file = Path.of("reports", "pmarket_report_" + System.currentTimeMillis() + ".csv");
            Files.createDirectories(file.getParent());
            Files.writeString(file, csv);
            UiHelper.showInfo(this, "Đã xuất file: " + file.toAbsolutePath());
            dispose();
        } catch (Exception ex) {
            UiHelper.showError(this, "Lỗi xuất báo cáo: " + ex.getMessage());
        }
    }

    private String buildCsv(AccountStat as, PostStat ps) {
        return "Loại,Chỉ số,Giá trị\n"
                + "Account,Từ ngày," + as.getStartDate() + "\n"
                + "Account,Đến ngày," + as.getEndDate() + "\n"
                + "Account,Tài khoản mới," + as.getNewAccounts() + "\n"
                + "Account,Bị khóa," + as.getBannedAccounts() + "\n"
                + "Account,Tổng," + as.getTotalAccounts() + "\n"
                + "Post,Bài mới," + ps.getNewPosts() + "\n"
                + "Post,Đã xóa," + ps.getDeletedPosts() + "\n"
                + "Post,Đang hoạt động," + ps.getTotalPosts() + "\n";
    }
}
