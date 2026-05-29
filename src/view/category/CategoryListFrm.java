package view.category;

import view.user.UiHelper;
import dao.CategoryDAO;
import dao.PostDAO;
import model.Category;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Module d — Quan ly danh muc (danh sach) */
public class CategoryListFrm extends JFrame implements ActionListener {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PostDAO postDAO = new PostDAO();
    private final JTable table = new JTable();
    private final DefaultTableModel model;
    private final JButton btnAdd = new JButton("Thêm danh mục mới");
    private final JButton btnEdit = new JButton("Sửa");
    private final JButton btnDelete = new JButton("Xóa");
    private List<Category> categories = List.of();

    public CategoryListFrm() {
        super("Quản lý danh mục");
        setSize(620, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Tên danh mục", "Danh mục cha", "Trạng thái"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setModel(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd.addActionListener(this);
        top.add(btnAdd);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnEdit.addActionListener(this);
        btnDelete.addActionListener(this);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        reloadCategories();
    }

    private void reloadCategories() {
        try {
            List<Category> all = categoryDAO.getCategories();
            Map<Integer, String> names = new HashMap<>();
            Map<Integer, List<Category>> children = new HashMap<>();
            List<Category> roots = new ArrayList<>();
            for (Category c : all) {
                names.put(c.getId(), c.getName());
                if (c.getParent() == null) {
                    roots.add(c);
                } else {
                    children.computeIfAbsent(c.getParent().getId(), key -> new ArrayList<>()).add(c);
                }
            }
            Comparator<Category> byName = Comparator.comparing(Category::getName, String.CASE_INSENSITIVE_ORDER);
            roots.sort(byName);
            for (List<Category> list : children.values()) {
                list.sort(byName);
            }

            categories = new ArrayList<>();
            model.setRowCount(0);
            for (Category root : roots) {
                addCategoryRow(root, 0, names, children);
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private void addCategoryRow(Category c, int level, Map<Integer, String> names,
                                Map<Integer, List<Category>> children) {
        categories.add(c);
        String parentName = c.getParent() != null
                ? names.getOrDefault(c.getParent().getId(), "") : "";
        String displayName = indent(level) + (level > 0 ? "|- " : "") + c.getName();
        model.addRow(new Object[]{c.getId(), displayName, parentName, c.getStatus()});
        for (Category child : children.getOrDefault(c.getId(), List.of())) {
            addCategoryRow(child, level + 1, names, children);
        }
    }

    private String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private Category selected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= categories.size()) {
            UiHelper.showError(this, "Vui lòng chọn một danh mục.");
            return null;
        }
        return categories.get(row);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            new CategoryEditFrm(null, this::reloadCategories).setVisible(true);
        } else if (e.getSource() == btnEdit) {
            Category c = selected();
            if (c == null) return;
            try {
                Category detail = categoryDAO.getCategoryDetail(c.getId());
                new CategoryEditFrm(detail, this::reloadCategories).setVisible(true);
            } catch (Exception ex) {
                UiHelper.showError(this, ex.getMessage());
            }
        } else if (e.getSource() == btnDelete) {
            Category c = selected();
            if (c == null) return;
            try {
                int count = postDAO.countPostByCategory(c.getId());
                if (count == 0) {
                    int ok = JOptionPane.showConfirmDialog(this,
                            "Bạn có chắc chắn muốn xóa danh mục này?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (ok == JOptionPane.YES_OPTION) {
                        categoryDAO.deleteCategory(c.getId());
                        UiHelper.showInfo(this, "Xóa danh mục thành công.");
                        reloadCategories();
                    }
                } else {
                    new CategoryTransferFrm(this, c, count, this::reloadCategories).setVisible(true);
                }
            } catch (Exception ex) {
                UiHelper.showError(this, ex.getMessage());
            }
        }
    }
}
