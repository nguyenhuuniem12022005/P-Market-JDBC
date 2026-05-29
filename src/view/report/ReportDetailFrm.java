package view.report;

import view.user.UiHelper;

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
    private final JTextArea outInfo = new JTextArea();
    private final JButton btnDeletePost = new JButton("Xoa bai vi pham");
    private final JButton btnLockAccount = new JButton("Khoa tai khoan vi pham");
    private final JButton btnReject = new JButton("Bac bo bao cao");

    public ReportDetailFrm(int reportId, Runnable onDone) {
        super((Frame) null, "Chi tiet bao cao #" + reportId, true);
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
            btnDeletePost.setEnabled("post".equalsIgnoreCase(report.getTargetType()));
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
