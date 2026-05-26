package view.report;

import view.user.UiHelper;

import dao.AccountDAO;
import dao.PostDAO;
import dao.ReportDAO;
import model.Report;
import model.ReportEvidence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module h — Chi tiet va xu ly bao cao */
public class ReportDetailFrm extends JDialog implements ActionListener {

    private final int reportId;
    private final Runnable onDone;
    private Report report;
    private final ReportDAO reportDAO = new ReportDAO();
    private final PostDAO postDAO = new PostDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final JTextArea outInfo = new JTextArea();
    private final JComboBox<String> inDeleteReason = new JComboBox<>(new String[]{
            "Hang cam / Noi dung nhay cam",
            "Tin rac / Spam",
            "Lua dao / Sai mo ta"
    });

    public ReportDetailFrm(int reportId, Runnable onDone) {
        super((Frame) null, "Chi tiet bao cao #" + reportId, true);
        this.reportId = reportId;
        this.onDone = onDone;
        setSize(520, 480);
        setLocationRelativeTo(null);

        outInfo.setEditable(false);
        outInfo.setLineWrap(true);

        JButton btnDeletePost = new JButton("Xoa bai vi pham");
        JButton btnBanUser = new JButton("Khoa tai khoan vi pham");
        JButton btnReject = new JButton("Bac bo bao cao");
        btnDeletePost.addActionListener(this);
        btnBanUser.addActionListener(this);
        btnReject.addActionListener(this);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        actions.add(new JLabel("Ly do xoa (neu xoa bai):"));
        actions.add(inDeleteReason);
        actions.add(btnDeletePost);
        actions.add(btnBanUser);
        actions.add(btnReject);

        add(new JScrollPane(outInfo), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
        loadReport();
    }

    private void loadReport() {
        try {
            report = reportDAO.getReportById(reportId);
            if (report == null) {
                UiHelper.showError(this, "Khong tim thay bao cao.");
                dispose();
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Ma bao cao: #").append(report.getId()).append("\n");
            sb.append("Nguoi gui: ").append(report.getAccount().getFullName()).append("\n");
            sb.append("Ly do: ").append(report.getReason()).append("\n");
            sb.append("Loai: ").append(report.getTargetType()).append(" #").append(report.getTargetId()).append("\n");
            if (report.getPost() != null) {
                sb.append("\nNoi dung bai dang:\n").append(report.getPost().getTitle()).append("\n");
                sb.append(report.getPost().getDescription()).append("\n");
            }
            sb.append("\nBang chung:\n");
            for (ReportEvidence ev : report.getListEvidence()) {
                sb.append(" - ").append(ev.getImageUrl()).append("\n");
            }
            outInfo.setText(sb.toString());
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private void markResolved() throws Exception {
        reportDAO.updateStatus(reportId, "resolved");
        UiHelper.showInfo(this, "Xu ly bao cao thanh cong.");
        dispose();
        if (onDone != null) onDone.run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JButton src = (JButton) e.getSource();
            if (src.getText().contains("Xoa bai")) {
                if (inDeleteReason.getSelectedItem() == null) {
                    UiHelper.showError(this, "Vui long chon ly do vi pham truoc khi xoa.");
                    return;
                }
                if (report != null && "post".equalsIgnoreCase(report.getTargetType())) {
                    postDAO.hidePost(report.getTargetId());
                }
                markResolved();
            } else if (src.getText().contains("Khoa")) {
                if (report != null && report.getPost() != null && report.getPost().getAccount() != null) {
                    int accId = report.getPost().getAccount().getId();
                    accountDAO.banAccount(accId, "Vi pham tu bao cao #" + reportId);
                }
                markResolved();
            } else if (src.getText().contains("Bac bo")) {
                reportDAO.updateStatus(reportId, "rejected");
                UiHelper.showInfo(this, "Da bac bo bao cao.");
                dispose();
                if (onDone != null) onDone.run();
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
