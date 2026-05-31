package view.post;

import view.user.UiHelper;
import dao.PostDAO;
import model.Post;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Module d — Hien thi tat ca bai dang (Khong tim kiem) */
public class PostListFrm extends JFrame { // Nên đổi tên class này thành AllPostFrm

    private final JTable tblPosts = new JTable();
    private final DefaultTableModel tableModel;
    private final PostDAO postDAO = new PostDAO();
    private final boolean adminMode;
    private List<Post> results = List.of();

    public PostListFrm(boolean adminMode) {
        super("Danh sách bài đăng"); // Đổi tiêu đề cửa sổ
        this.adminMode = adminMode;
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Tiêu đề", "Giá", "Người bán", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPosts.setModel(tableModel);

        // Sự kiện click đúp để mở chi tiết bài đăng
        tblPosts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblPosts.getSelectedRow();
                    if (row >= 0 && row < results.size()) {
                        new PostDetailFrm(results.get(row).getId(), adminMode).setVisible(true);
                    }
                }
            }
        });

        // Chỉ thêm hướng dẫn click đúp ở phía trên (Top)
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("(Double-click vào một dòng để xem chi tiết)"));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblPosts), BorderLayout.CENTER);

        // Tự động tải dữ liệu ngay khi bật form lên
        loadAllPosts();
    }

    // Hàm này thay thế cho doSearch() cũ
    private void loadAllPosts() {
        try {
            // Lưu ý: Đảm bảo bạn đã thêm hàm getAllPosts() vào file PostDAO.java như hướng dẫn ở tin nhắn trước
            results = postDAO.getAllPosts();
            tableModel.setRowCount(0);
            for (Post p : results) {
                String seller = p.getAccount() != null ? p.getAccount().getFullName() : "";
                tableModel.addRow(new Object[]{
                        p.getId(), p.getTitle(), String.format("%,.0f", p.getPrice()),
                        seller, UiHelper.statusLabel(p.getStatus())
                });
            }
        } catch (Exception ex) {
            UiHelper.showError(this, "Lỗi khi tải danh sách: " + ex.getMessage());
        }
    }
}