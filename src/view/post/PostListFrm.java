package view.post;

import dao.CategoryDAO;
import dao.PostDAO;
import model.Category;
import model.Post;
import view.user.UiHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/** Module d - Danh sach bai dang. */
public class PostListFrm extends JFrame implements ActionListener {

    private final JTextField inKeyword = new JTextField(20);
    private final JComboBox<Category> inCategory = new JComboBox<>();
    private final JButton btnSearch = new JButton("Tìm kiếm");
    private final JButton btnViewDetail = new JButton("Xem chi tiết");
    private final JTable tblPosts = new JTable();
    private final DefaultTableModel tableModel;
    private final PostDAO postDAO = new PostDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final boolean adminMode;
    private List<Post> results = List.of();

    public PostListFrm(boolean adminMode) {
        super("Danh sách bài đăng");
        this.adminMode = adminMode;
        setSize(750, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Tiêu đề", "Giá", "Người bán", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblPosts.setModel(tableModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Từ khóa:"));
        top.add(inKeyword);
        top.add(new JLabel("Danh mục:"));
        inCategory.addItem(null);
        loadCategories();
        top.add(inCategory);
        btnSearch.addActionListener(this);
        btnViewDetail.addActionListener(this);
        top.add(btnSearch);
        top.add(btnViewDetail);
        top.add(new JLabel("(Chọn một dòng rồi bấm Xem chi tiết)"));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblPosts), BorderLayout.CENTER);
        doSearch();
    }

    private void loadCategories() {
        try {
            for (Category c : categoryDAO.getAllCategories()) {
                inCategory.addItem(c);
            }
        } catch (Exception ignored) {
        }
    }

    private void doSearch() {
        try {
            Category cat = (Category) inCategory.getSelectedItem();
            Integer catId = cat != null ? cat.getId() : null;
            results = postDAO.searchPosts(inKeyword.getText(), catId);
            tableModel.setRowCount(0);
            for (Post p : results) {
                String seller = p.getAccount() != null ? p.getAccount().getFullName() : "";
                tableModel.addRow(new Object[]{
                        p.getId(), p.getTitle(), String.format("%,.0f", p.getPrice()),
                        seller, UiHelper.statusLabel(p.getStatus())
                });
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private void openSelectedPost() {
        int row = tblPosts.getSelectedRow();
        if (row < 0 || row >= results.size()) {
            UiHelper.showError(this, "Vui lòng chọn một bài đăng.");
            return;
        }
        new PostDetailFrm(results.get(row).getId(), adminMode).setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            doSearch();
        } else if (e.getSource() == btnViewDetail) {
            openSelectedPost();
        }
    }
}
