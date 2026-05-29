package view.post;

import dao.PostDAO;
import model.Image;
import model.Post;
import model.SessionManager;
import view.chat.ChatRoomFrm;
import view.report.ReportFormFrm;
import view.user.UiHelper;
import view.user.UserProfileFrm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module e — Xem chi tiet bai dang (+ mo chat/bao cao cho SV) */
public class PostDetailFrm extends JFrame implements ActionListener {

    private final int postId;
    private final boolean adminMode;
    private Post post;
    private final PostDAO postDAO = new PostDAO();
    private final JTextArea outDetail = new JTextArea();
    private final JButton btnChat = new JButton("Nhắn tin người bán");
    private final JButton btnSellerProfile = new JButton("Hồ sơ người bán");
    private final JButton btnReport = new JButton("Báo cáo vi phạm");
    private final JButton btnClose = new JButton("Đóng");

    public PostDetailFrm(int postId, boolean adminMode) {
        super("Chi tiết bài đăng #" + postId);
        this.postId = postId;
        this.adminMode = adminMode;
        setSize(520, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outDetail.setEditable(false);
        outDetail.setLineWrap(true);
        outDetail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPanel actions = new JPanel(new FlowLayout());
        if (!adminMode && SessionManager.isStudent()) {
            btnChat.addActionListener(this);
            btnSellerProfile.addActionListener(this);
            btnReport.addActionListener(this);
            actions.add(btnChat);
            actions.add(btnSellerProfile);
            actions.add(btnReport);
        }
        btnClose.addActionListener(this);
        actions.add(btnClose);

        add(new JScrollPane(outDetail), BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);
        loadPost();
    }

    private void loadPost() {
        try {
            post = postDAO.getPostById(postId);
            if (post == null) {
                UiHelper.showError(this, "Không tìm thấy bài đăng.");
                dispose();
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("TIÊU ĐỀ: ").append(post.getTitle()).append("\n\n");
            sb.append("MÔ TẢ:\n").append(post.getDescription()).append("\n\n");
            sb.append("GIÁ: ").append(String.format("%,.0f VND", post.getPrice())).append("\n");
            sb.append("SỐ LƯỢNG: ").append(post.getQuantity()).append("\n");
            sb.append("TRẠNG THÁI: ").append(UiHelper.statusLabel(post.getStatus())).append("\n");
            if (post.getCategory() != null) {
                sb.append("DANH MỤC: ").append(post.getCategory().getName()).append("\n");
            }
            if (post.getAccount() != null) {
                sb.append("NGƯỜI BÁN: ").append(post.getAccount().getFullName())
                        .append(" (").append(post.getAccount().getEmail()).append(")\n");
            }
            sb.append("\nẢNH:\n");
            for (Image img : post.getListImage()) {
                sb.append(" - ").append(img.getImageUrl()).append("\n");
            }
            outDetail.setText(sb.toString());
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose) {
            dispose();
        } else if (e.getSource() == btnReport && post != null) {
            new ReportFormFrm(post).setVisible(true);
        } else if (e.getSource() == btnSellerProfile && post != null && post.getAccount() != null) {
            new UserProfileFrm(post.getAccount().getId()).setVisible(true);
        } else if (e.getSource() == btnChat && post != null && post.getAccount() != null) {
            int sellerId = post.getAccount().getId();
            int myId = SessionManager.getCurrentAccount().getId();
            if (sellerId == myId) {
                UiHelper.showError(this, "Không thể nhắn tin với chính mình.");
                return;
            }
            new ChatRoomFrm(sellerId, post.getTitle()).setVisible(true);
        }
    }
}
