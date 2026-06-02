package view.post;

import view.user.UiHelper;
import dao.PostDAO;
import model.Post;
import model.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Module b — Quản lý bài đăng cá nhân */
public class ManagePostFrm extends JFrame implements ActionListener {

    private final PostDAO postDAO = new PostDAO();
    private final JTable table = new JTable();
    private final DefaultTableModel model;
    private final JButton btnCreate = new JButton("Tạo bài đăng");
    private final JButton btnEdit = new JButton("Sửa");
    private final JButton btnDelete = new JButton("Xóa");
    private List<Post> posts = List.of();

    public ManagePostFrm() {
        super("Quản lý bài đăng của tôi");
        setSize(720, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new String[]{"ID", "Tiêu đề", "Giá", "Số lượng", "Danh mục","Ảnh", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    table.setRowSelectionInterval(row, row);
                    openSelectedPostDetail();
                }
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnCreate.addActionListener(this);
        top.add(btnCreate);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEdit.addActionListener(this);
        btnDelete.addActionListener(this);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        reload();
    }

    private void reload() {
        try {
            int myId = SessionManager.getCurrentAccount().getId();
            posts = postDAO.getPostsByAccount(myId);
            model.setRowCount(0);
            for (Post p : posts) {
                String imageUrl= ""; 
                if (p.getListImage() != null
                    && !p.getListImage().isEmpty()) {

                 imageUrl = p.getListImage()
                    .get(0)
                    .getImageUrl();
}
                model.addRow(new Object[]{
                        p.getId(), p.getTitle(), String.format("%,.0f", p.getPrice()),
                        p.getQuantity(),
                        p.getCategory() != null ? p.getCategory().getName() : "",
                        imageUrl,
                        UiHelper.statusLabel(p.getStatus())
                });
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private Post selectedPost() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= posts.size()) {
            UiHelper.showError(this, "Vui lòng chọn một bài đăng.");
            return null;
        }
        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= posts.size()) {
            UiHelper.showError(this, "Không mở được bài đăng đang chọn.");
            return null;
        }
        return posts.get(modelRow);
    }

    private void openSelectedPostDetail() {
        Post p = selectedPost();
        if (p != null) {
            new PostDetailFrm(p.getId(), false).setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCreate) {
            new CreatePostFrm(this::reload).setVisible(true);
        } else if (e.getSource() == btnEdit) {
            Post p = selectedPost();
            if (p != null) {
                new EditPostFrm(p.getId(), this::reload).setVisible(true);
            }
        } else if (e.getSource() == btnDelete) {
            Post p = selectedPost();
            if (p == null) return;
            int ok = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa bài đăng này?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    postDAO.deletePost(p.getId());
                    UiHelper.showInfo(this, "Xóa thanh cong");
                    reload();
                } catch (Exception ex) {
                    UiHelper.showError(this, ex.getMessage());
                }
            }
        }
    }
}