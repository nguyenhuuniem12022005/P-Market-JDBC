package view.report;

import view.user.UiHelper;

import dao.ReportEvidenceDAO;
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
    private final ReportEvidenceDAO evidenceDAO = new ReportEvidenceDAO();
    private final JTextArea outInfo = new JTextArea();
    private final JButton btnDeletePost = new JButton("Xóa bài vi phạm");
    private final JButton btnLockAccount = new JButton("Khóa tài khoản vi phạm");
    private final JButton btnReject = new JButton("Bác bỏ báo cáo");

    public ReportDetailFrm(int reportId, Runnable onDone) {
        super((Frame) null, "Chi tiết báo cáo #" + reportId, true);
        this.reportId = reportId;
        this.onDone = onDone;
        setSize(520, 480);
        setLocationRelativeTo(null);

        outInfo.setEditable(false);
        outInfo.setLineWrap(true);

        btnDeletePost.addActionListener(this);
        btnLockAccount.addActionListener(this);
        btnReject.addActionListener(this);

        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 8));
        actions.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        actions.add(btnDeletePost);
        actions.add(btnLockAccount);
        actions.add(btnReject);

        add(new JScrollPane(outInfo), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
        loadReport();
    }

    public ReportDetailFrm(Report report, Runnable onDone) {
        this(report.getId(), onDone);
    }

    private void loadReport() {
        try {
            report = reportDAO.getReportById(reportId);
            if (report == null) {
                UiHelper.showError(this, "Không tìm thấy báo cáo.");
                dispose();
                return;
            }
            report.setListEvidence(evidenceDAO.getEvidenceByReportId(reportId));
            StringBuilder sb = new StringBuilder();
            sb.append("Mã báo cáo: #").append(report.getId()).append("\n");
            sb.append("Người gửi: ").append(report.getReporter().getFullName()).append("\n");
            sb.append("Lý do: ").append(report.getReason()).append("\n");
            if (report.getPostId() != null) {
                sb.append("Đối tượng: Bài đăng #").append(report.getPostId()).append("\n");
            } else {
                sb.append("Đối tượng: Tài khoản #").append(report.getAccountId()).append("\n");
            }
            if (report.getPost() != null) {
                sb.append("\nNội dung bài đăng:\n").append(report.getPost().getTitle()).append("\n");
                sb.append(report.getPost().getDescription()).append("\n");
            }
            if (report.getAccount() != null) {
                sb.append("\nTài khoản bị báo cáo:\n").append(report.getAccount().getFullName())
                        .append(" (").append(report.getAccount().getEmail()).append(")\n");
            }
            sb.append("\nBằng chứng:\n");
            for (ReportEvidence ev : report.getListEvidence()) {
                sb.append(" - ").append(ev.getImageUrl()).append("\n");
            }
            outInfo.setText(sb.toString());
            btnDeletePost.setEnabled(report.getPostId() != null);
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (report == null) return;
        Object src = e.getSource();
        String actionType;
        if (src == btnDeletePost) {
            actionType = ActionConfirmFrm.DELETE_POST;
        } else if (src == btnLockAccount) {
            actionType = ActionConfirmFrm.LOCK_ACCOUNT;
        } else {
            actionType = ActionConfirmFrm.REJECT;
        }
        new ActionConfirmFrm(this, report, actionType, () -> {
            dispose();
            if (onDone != null) onDone.run();
        }).setVisible(true);
    }
}
