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
import java.io.File;

/** Module h — Chi tiết và xử lý báo cáo */
public class ReportDetailFrm extends JDialog implements ActionListener {

    private final int reportId;
    private final Runnable onDone;
    private Report report;
    private final ReportDAO reportDAO = new ReportDAO();
    private final ReportEvidenceDAO evidenceDAO = new ReportEvidenceDAO();
    private final JTextArea outInfo = new JTextArea();
    private final JPanel evidencePanel = new JPanel();
    private final JButton btnDeletePost = new JButton("Xóa bài vi phạm");
    private final JButton btnLockAccount = new JButton("Khóa tài khoản vi phạm");
    private final JButton btnReject = new JButton("Bác bỏ báo cáo");

    public ReportDetailFrm(int reportId, Runnable onDone) {
        super((Frame) null, "Chi tiết báo cáo #" + reportId, true);
        this.reportId = reportId;
        this.onDone = onDone;
        setSize(760, 520);
        setLocationRelativeTo(null);

        outInfo.setEditable(false);
        outInfo.setLineWrap(true);
        evidencePanel.setLayout(new BoxLayout(evidencePanel, BoxLayout.Y_AXIS));
        evidencePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        btnDeletePost.addActionListener(this);
        btnLockAccount.addActionListener(this);
        btnReject.addActionListener(this);

        JPanel actions = new JPanel(new GridLayout(1, 3, 8, 8));
        actions.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        actions.add(btnDeletePost);
        actions.add(btnLockAccount);
        actions.add(btnReject);

        JScrollPane evidenceScroll = new JScrollPane(evidencePanel);
        evidenceScroll.setPreferredSize(new Dimension(220, 0));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(outInfo), evidenceScroll);
        split.setResizeWeight(0.68);
        add(split, BorderLayout.CENTER);
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
            sb.append("Trạng thái: ").append(UiHelper.statusLabel(report.getStatus())).append("\n");
            sb.append("Người gửi: ").append(report.getReporter().getFullName()).append("\n");
            sb.append("Lý do: ").append(report.getReason()).append("\n");
            if (report.getDetail() != null && !report.getDetail().isBlank()) {
                sb.append("Mô tả chi tiết: ").append(report.getDetail()).append("\n");
            }
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
            renderEvidenceImages();
            boolean pending = Report.STATUS_PENDING.equalsIgnoreCase(report.getStatus());
            btnDeletePost.setEnabled(pending && report.getPostId() != null);
            btnLockAccount.setEnabled(pending);
            btnReject.setEnabled(pending);
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }


    private void renderEvidenceImages() {
        evidencePanel.removeAll();
        JLabel title = new JLabel("Ảnh bằng chứng");
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        evidencePanel.add(title);
        evidencePanel.add(Box.createVerticalStrut(8));

        if (report.getListEvidence().isEmpty()) {
            JLabel empty = new JLabel("Không có ảnh bằng chứng.");
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            evidencePanel.add(empty);
            evidencePanel.revalidate();
            evidencePanel.repaint();
            return;
        }

        for (ReportEvidence ev : report.getListEvidence()) {
            JPanel item = new JPanel(new BorderLayout(4, 4));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            item.setMaximumSize(new Dimension(200, 170));
            item.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            ImageIcon thumbnail = loadThumbnail(ev.getImageUrl(), 180, 120);
            JLabel imageLabel = thumbnail != null
                    ? new JLabel(thumbnail)
                    : new JLabel("Không mở được ảnh");
            imageLabel.setHorizontalAlignment(SwingConstants.LEFT);
            item.add(imageLabel, BorderLayout.CENTER);

            JLabel pathLabel = new JLabel(fileName(ev.getImageUrl()));
            pathLabel.setToolTipText(ev.getImageUrl());
            item.add(pathLabel, BorderLayout.SOUTH);
            evidencePanel.add(item);
        }
        evidencePanel.revalidate();
        evidencePanel.repaint();
    }

    private ImageIcon loadThumbnail(String imageUrl, int maxWidth, int maxHeight) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        File file = resolveImageFile(imageUrl);
        if (!file.exists()) {
            return null;
        }
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }
        double scale = Math.min(
                (double) maxWidth / icon.getIconWidth(),
                (double) maxHeight / icon.getIconHeight());
        int width = Math.max(1, (int) Math.round(icon.getIconWidth() * Math.min(scale, 1.0)));
        int height = Math.max(1, (int) Math.round(icon.getIconHeight() * Math.min(scale, 1.0)));
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private File resolveImageFile(String imageUrl) {
        File file = new File(imageUrl);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(System.getProperty("user.dir"), imageUrl);
    }

    private String fileName(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return "";
        }
        return new File(imageUrl).getName();
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
