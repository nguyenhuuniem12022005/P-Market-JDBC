package view.report;

import view.user.UiHelper;

import dao.ReportDAO;
import model.Account;
import model.Post;
import model.Report;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

/** Module g — Bao cao vi pham */
public class ReportFrm extends JDialog implements ActionListener {

    private final Post targetPost;
    private final JTextArea inReason = new JTextArea(4, 28);
    private final ReportDAO reportDAO = new ReportDAO();

    public ReportFrm(Post targetPost) {
        super((Frame) null, "Bao cao vi pham", true);
        this.targetPost = targetPost;
        setSize(420, 280);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        p.add(new JLabel("Bao cao bai: " + targetPost.getTitle()), BorderLayout.NORTH);
        p.add(new JScrollPane(inReason), BorderLayout.CENTER);
        JLabel lbl = new JLabel("Ly do bao cao (*):");
        p.add(lbl, BorderLayout.WEST);

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
        String reason = inReason.getText().trim();
        if (reason.isEmpty()) {
            UiHelper.showError(this, "Vui long nhap ly do bao cao.");
            return;
        }
        try {
            Report r = new Report();
            r.setTargetType("post");
            r.setTargetId(targetPost.getId());
            r.setReason(reason);
            Account me = SessionManager.getCurrentAccount();
            r.setAccount(me);
            reportDAO.createReport(r, Collections.singletonList("uploads/evidence_user.png"));
            UiHelper.showInfo(this, "Da ghi nhan bao cao thanh cong.");
            dispose();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
