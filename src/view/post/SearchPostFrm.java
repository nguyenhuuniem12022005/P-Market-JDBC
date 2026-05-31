package view.post;

import view.user.UiHelper;
import dao.CategoryDAO;
import dao.PostDAO;
import model.Category;
import model.Post;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Module d — Danh sach bai dang */
public class SearchPostFrm extends JFrame implements ActionListener {

    private final JTextField inKeyword = new JTextField(20);
    private final JComboBox<Category> inCategory = new JComboBox<>();
    private final JButton btnSearch = new JButton("Tìm kiếm");
    private final JTable tblPosts = new JTable();
    private final DefaultTableModel tableModel;
    private final PostDAO postDAO = new PostDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final boolean adminMode;
    private List<Post> results = List.of();

    public SearchPostFrm(boolean adminMode) {
        super("Danh sách bài đăng");
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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Từ khóa:"));
        top.add(inKeyword);
        top.add(new JLabel("Danh mục:"));
        inCategory.addItem(null);
        loadCategories();
        top.add(inCategory);
        btnSearch.addActionListener(this);
        top.add(btnSearch);
        top.add(new JLabel("(Double-click để xem chi tiết)"));

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            doSearch();
        }
    }
}
