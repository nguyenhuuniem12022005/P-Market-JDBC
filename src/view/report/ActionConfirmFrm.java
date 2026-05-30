package view.report;

import view.user.UiHelper;

import dao.AccountDAO;
import dao.PostDAO;
import dao.ReportDAO;
import model.Report;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module h — Xac nhan quyet dinh xu ly bao cao (ActionConfirmFrm) */
public class ActionConfirmFrm extends JDialog implements ActionListener {

    public static final String DELETE_POST = "DELETE_POST";
    public static final String LOCK_ACCOUNT = "LOCK_ACCOUNT";
    public static final String REJECT = "REJECT";

    private final Report report;
    private final String actionType;
    private final Runnable onDone;
    private final ReportDAO reportDAO = new ReportDAO();
    private final PostDAO postDAO = new PostDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public ActionConfirmFrm(JDialog parent, Report report, String actionType, Runnable onDone) {
        super(parent, "Xác nhận xử lý", true);
        this.report = report;
        this.actionType = actionType;
        this.onDone = onDone;
        setSize(420, 200);
        setLocationRelativeTo(parent);

        JLabel msg = new JLabel("<html>" + describe() + "<br>Bạn có chắc chắn?</html>");
        msg.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton btnConfirm = new JButton("Xác nhận");
        JButton btnCancel = new JButton("Hủy");
        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(btnConfirm);
        bottom.add(btnCancel);

        setLayout(new BorderLayout());
        add(msg, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private String describe() {
        return switch (actionType) {
            case DELETE_POST -> "Xóa bài vi phạm #" + report.getPostId() + " và đánh dấu báo cáo đã xử lý.";
            case LOCK_ACCOUNT -> "Khóa tài khoản vi phạm và đánh dấu báo cáo đã xử lý.";
            default -> "Bác bỏ báo cáo #" + report.getId() + ".";
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            switch (actionType) {
                case DELETE_POST -> {
                    if (report.getPostId() != null) {
                        postDAO.deletePost(report.getPostId());
                    }
                    reportDAO.updateStatus(report.getId(), Report.STATUS_PROCESSED);
                    UiHelper.showInfo(this, "Xử lý báo cáo thành công.");
                }
                case LOCK_ACCOUNT -> {
                    int accId = resolveAccountId();
                    if (accId > 0) {
                        accountDAO.lockAccount(accId, "Vi phạm từ báo cáo #" + report.getId());
                    }
                    reportDAO.updateStatus(report.getId(), Report.STATUS_PROCESSED);
                    UiHelper.showInfo(this, "Xử lý báo cáo thành công.");
                }
                default -> {
                    reportDAO.updateStatus(report.getId(), Report.STATUS_PROCESSED);
                    UiHelper.showInfo(this, "Đã bác bỏ và đánh dấu báo cáo đã xử lý.");
                }
            }
            dispose();
            if (onDone != null) onDone.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private int resolveAccountId() {
        if (report.getAccountId() != null) {
            return report.getAccountId();
        }
        if (report.getPost() != null && report.getPost().getAccount() != null) {
            return report.getPost().getAccount().getId();
        }
        return -1;
    }
}
