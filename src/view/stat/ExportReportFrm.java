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
    private final JButton btnDownload = new JButton("Tai ve (CSV)");
    private final JButton btnCancel = new JButton("Huy");
    private final AccountStatDAO accountStatDAO = new AccountStatDAO();
    private final PostStatDAO postStatDAO = new PostStatDAO();

    public ExportReportFrm() {
        super("Xuat bao cao");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        form.add(new JLabel("Tu ngay (yyyy-MM-dd):"));
        form.add(inStart);
        form.add(new JLabel("Den ngay:"));
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
            UiHelper.showInfo(this, "Da xuat file: " + file.toAbsolutePath());
            dispose();
        } catch (Exception ex) {
            UiHelper.showError(this, "Loi xuat bao cao: " + ex.getMessage());
        }
    }

    private String buildCsv(AccountStat as, PostStat ps) {
        return "Loai,Chi so,Gia tri\n"
                + "Account,Tu ngay," + as.getStartDate() + "\n"
                + "Account,Den ngay," + as.getEndDate() + "\n"
                + "Account,Tai khoan moi," + as.getNewAccounts() + "\n"
                + "Account,Bi khoa," + as.getBannedAccounts() + "\n"
                + "Account,Tong," + as.getTotalAccounts() + "\n"
                + "Post,Bai moi," + ps.getNewPosts() + "\n"
                + "Post,Da ban," + ps.getSoldPosts() + "\n"
                + "Post,Tong," + ps.getTotalPosts() + "\n";
    }
}
