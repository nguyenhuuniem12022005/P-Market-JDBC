package view.report;

import view.user.UiHelper;

import dao.AccountDAO;
import dao.ImageDAO;
import dao.PostDAO;
import dao.ReportDAO;
import dao.ReportEvidenceDAO;
import model.Account;
import model.Post;
import model.Report;
import model.SessionManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** Module g — Bao cao vi pham (ReportFormFrm: bao cao bai dang hoac tai khoan) */
public class ReportFormFrm extends JDialog implements ActionListener {

    private final Post targetPost;
    private final Account targetAccount;

    private final JComboBox<String> inReason = new JComboBox<>(new String[]{
            "Hàng cấm / Nội dung nhạy cảm",
            "Tin rác / Spam",
            "Lừa đảo / Sai mô tả",
            "Giả danh / Nick ảo"
    });
    private final JTextArea inDetail = new JTextArea(4, 28);
    private final JTextField inEvidence = new JTextField(20);
    private final ImageDAO imageDAO = new ImageDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final ReportEvidenceDAO reportEvidenceDAO = new ReportEvidenceDAO();
    private final PostDAO postDAO = new PostDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    /** Bao cao mot bai dang */
    public ReportFormFrm(Post targetPost) {
        this(targetPost, null, "Báo cáo bài: " + targetPost.getTitle());
    }

    /** Bao cao mot tai khoan */
    public ReportFormFrm(Account targetAccount) {
        this(null, targetAccount, "Báo cáo tài khoản: " + targetAccount.getFullName());
    }

    private ReportFormFrm(Post targetPost, Account targetAccount, String header) {
        super((Frame) null, "Báo cáo vi phạm", true);
        this.targetPost = targetPost;
        this.targetAccount = targetAccount;
        setSize(440, 360);
        setLocationRelativeTo(null);
        inEvidence.setEditable(false);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(new JLabel(header), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        center.add(new JLabel("Lý do vi phạm (*):"), gbc);
        gbc.gridx = 1;
        center.add(inReason, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(new JLabel("Mô tả chi tiết:"), gbc);
        gbc.gridx = 1;
        inDetail.setLineWrap(true);
        center.add(new JScrollPane(inDetail), gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        center.add(new JLabel("Ảnh bằng chứng:"), gbc);
        gbc.gridx = 1;
        JPanel evPanel = new JPanel(new BorderLayout());
        evPanel.add(inEvidence, BorderLayout.CENTER);
        JButton btnBrowse = new JButton("Chọn");
        btnBrowse.addActionListener(e -> {
            JFileChooser ch = new JFileChooser();
            ch.setFileFilter(new FileNameExtensionFilter(
                    "Ảnh (*.png, *.jpg, *.jpeg, *.gif)", "png", "jpg", "jpeg", "gif"));
            if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inEvidence.setText(ch.getSelectedFile().getAbsolutePath());
            }
        });
        evPanel.add(btnBrowse, BorderLayout.EAST);
        center.add(evPanel, gbc);
        p.add(center, BorderLayout.CENTER);

        JButton btnSend = new JButton("Gửi báo cáo");
        JButton btnCancel = new JButton("Hủy");
        btnSend.addActionListener(this);
        btnCancel.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(btnSend);
        bottom.add(btnCancel);
        p.add(bottom, BorderLayout.SOUTH);
        add(p);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inReason.getSelectedItem() == null) {
            UiHelper.showError(this, "Vui lòng chọn lý do vi phạm.");
            return;
        }
        try {
            Account me = SessionManager.getCurrentAccount();
            Report r = new Report();
            r.setReporter(me);
            r.setReporterId(me.getId());
            r.setReason((String) inReason.getSelectedItem());
            r.setDetail(inDetail.getText().trim());
            r.setStatus(Report.STATUS_PENDING);

            if (targetPost != null) {
                if (postDAO.findActivePostById(targetPost.getId()) == null) {
                    UiHelper.showError(this, "Bài đăng không còn tồn tại.");
                    return;
                }
                r.setPostId(targetPost.getId());
                r.setAccountId(null);
                if (reportDAO.hasPendingDuplicate(me.getId(), r.getPostId(), null)) {
                    UiHelper.showError(this, "Bạn đã báo cáo bài đăng này và đang chờ xử lý.");
                    return;
                }
            } else {
                if (targetAccount.getId() == me.getId()) {
                    UiHelper.showError(this, "Không thể tự báo cáo tài khoản của mình.");
                    return;
                }
                if (accountDAO.findActiveAccountById(targetAccount.getId()) == null) {
                    UiHelper.showError(this, "Tài khoản không còn tồn tại.");
                    return;
                }
                r.setPostId(null);
                r.setAccountId(targetAccount.getId());
                r.setAccount(targetAccount);
                if (reportDAO.hasPendingDuplicate(me.getId(), null, r.getAccountId())) {
                    UiHelper.showError(this, "Bạn đã báo cáo tài khoản này và đang chờ xử lý.");
                    return;
                }
            }

            int reportId = reportDAO.addReport(r);

            List<String> evidence = new ArrayList<>();
            String ev = inEvidence.getText().trim();
            if (!ev.isEmpty()) {
                evidence.addAll(imageDAO.uploadImages(List.of(ev)));
            }
            reportEvidenceDAO.addEvidenceList(reportId, evidence);

            UiHelper.showInfo(this, "Đã ghi nhận báo cáo thành công.");
            dispose();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
