package view.report;

import view.user.UiHelper;

import dao.AccountDAO;
import dao.PostDAO;
import dao.ReportDAO;
import dao.ReportEvidenceDAO;
import model.Account;
import model.Post;
import model.Report;
import model.SessionManager;

import javax.swing.*;
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
            "Hang cam / Noi dung nhay cam",
            "Tin rac / Spam",
            "Lua dao / Sai mo ta",
            "Gia danh / Nick ao"
    });
    private final JTextArea inDetail = new JTextArea(4, 28);
    private final JTextField inEvidence = new JTextField(20);
    private final ReportDAO reportDAO = new ReportDAO();
    private final ReportEvidenceDAO reportEvidenceDAO = new ReportEvidenceDAO();
    private final PostDAO postDAO = new PostDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    /** Bao cao mot bai dang */
    public ReportFormFrm(Post targetPost) {
        this(targetPost, null, "Bao cao bai: " + targetPost.getTitle());
    }

    /** Bao cao mot tai khoan */
    public ReportFormFrm(Account targetAccount) {
        this(null, targetAccount, "Bao cao tai khoan: " + targetAccount.getFullName());
    }

    private ReportFormFrm(Post targetPost, Account targetAccount, String header) {
        super((Frame) null, "Bao cao vi pham", true);
        this.targetPost = targetPost;
        this.targetAccount = targetAccount;
        setSize(440, 360);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(new JLabel(header), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        center.add(new JLabel("Ly do vi pham (*):"), gbc);
        gbc.gridx = 1;
        center.add(inReason, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        center.add(new JLabel("Mo ta chi tiet:"), gbc);
        gbc.gridx = 1;
        inDetail.setLineWrap(true);
        center.add(new JScrollPane(inDetail), gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        center.add(new JLabel("Anh bang chung:"), gbc);
        gbc.gridx = 1;
        JPanel evPanel = new JPanel(new BorderLayout());
        evPanel.add(inEvidence, BorderLayout.CENTER);
        JButton btnBrowse = new JButton("Chon");
        btnBrowse.addActionListener(e -> {
            JFileChooser ch = new JFileChooser();
            if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inEvidence.setText(ch.getSelectedFile().getAbsolutePath());
            }
        });
        evPanel.add(btnBrowse, BorderLayout.EAST);
        center.add(evPanel, gbc);
        p.add(center, BorderLayout.CENTER);

        JButton btnSend = new JButton("Gui bao cao");
        JButton btnCancel = new JButton("Huy");
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
            UiHelper.showError(this, "Vui long chon ly do vi pham.");
            return;
        }
        try {
            Account me = SessionManager.getCurrentAccount();
            Report r = new Report();
            r.setAccount(me);
            r.setReason((String) inReason.getSelectedItem());
            r.setStatus("pending");

            if (targetPost != null) {
                if (postDAO.findActivePostById(targetPost.getId()) == null) {
                    UiHelper.showError(this, "Bai dang khong con ton tai.");
                    return;
                }
                r.setTargetType("post");
                r.setTargetId(targetPost.getId());
            } else {
                if (accountDAO.findActiveAccountById(targetAccount.getId()) == null) {
                    UiHelper.showError(this, "Tai khoan khong con ton tai.");
                    return;
                }
                r.setTargetType("account");
                r.setTargetId(targetAccount.getId());
            }

            int reportId = reportDAO.addReport(r);

            List<String> evidence = new ArrayList<>();
            String ev = inEvidence.getText().trim();
            if (!ev.isEmpty()) {
                evidence.add(ev);
            } else {
                evidence.add("uploads/evidence_user.png");
            }
            reportEvidenceDAO.addEvidenceList(reportId, evidence);

            UiHelper.showInfo(this, "Da ghi nhan bao cao thanh cong.");
            dispose();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
