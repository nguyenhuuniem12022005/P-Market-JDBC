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
        super(parent, "Xac nhan xu ly", true);
        this.report = report;
        this.actionType = actionType;
        this.onDone = onDone;
        setSize(420, 200);
        setLocationRelativeTo(parent);

        JLabel msg = new JLabel("<html>" + describe() + "<br>Ban co chac chan?</html>");
        msg.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton btnConfirm = new JButton("Xac nhan");
        JButton btnCancel = new JButton("Huy");
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
            case DELETE_POST -> "Xoa bai vi pham #" + report.getTargetId() + " va danh dau bao cao da xu ly.";
            case LOCK_ACCOUNT -> "Khoa tai khoan vi pham va danh dau bao cao da xu ly.";
            default -> "Bac bo bao cao #" + report.getId() + ".";
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            switch (actionType) {
                case DELETE_POST -> {
                    if ("post".equalsIgnoreCase(report.getTargetType())) {
                        postDAO.deletePost(report.getTargetId());
                    }
                    reportDAO.updateStatus(report.getId(), "resolved");
                    UiHelper.showInfo(this, "Xu ly bao cao thanh cong.");
                }
                case LOCK_ACCOUNT -> {
                    int accId = resolveAccountId();
                    if (accId > 0) {
                        accountDAO.lockAccount(accId, "Vi pham tu bao cao #" + report.getId());
                    }
                    reportDAO.updateStatus(report.getId(), "resolved");
                    UiHelper.showInfo(this, "Xu ly bao cao thanh cong.");
                }
                default -> {
                    reportDAO.updateStatus(report.getId(), "rejected");
                    UiHelper.showInfo(this, "Da bac bo bao cao.");
                }
            }
            dispose();
            if (onDone != null) onDone.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private int resolveAccountId() {
        if ("account".equalsIgnoreCase(report.getTargetType())) {
            return report.getTargetId();
        }
        if (report.getPost() != null && report.getPost().getAccount() != null) {
            return report.getPost().getAccount().getId();
        }
        return -1;
    }
}
