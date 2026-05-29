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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Module d — Quan ly danh muc (danh sach) */
public class CategoryListFrm extends JFrame implements ActionListener {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PostDAO postDAO = new PostDAO();
    private final JTable table = new JTable();
    private final DefaultTableModel model;
    private final JButton btnAdd = new JButton("Them danh muc moi");
    private final JButton btnEdit = new JButton("Sua");
    private final JButton btnDelete = new JButton("Xoa");
    private List<Category> categories = List.of();

    public CategoryListFrm() {
        super("Quan ly danh muc");
        setSize(620, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "Ten danh muc", "Danh muc cha"}, 0) {
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
            categories = categoryDAO.getCategories();
            Map<Integer, String> names = new HashMap<>();
            for (Category c : categories) {
                names.put(c.getId(), c.getName());
            }
            model.setRowCount(0);
            for (Category c : categories) {
                String parentName = c.getParent() != null
                        ? names.getOrDefault(c.getParent().getId(), "") : "";
                model.addRow(new Object[]{c.getId(), c.getName(), parentName});
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private Category selected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= categories.size()) {
            UiHelper.showError(this, "Vui long chon mot danh muc.");
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
                            "Ban co chac chan muon xoa danh muc nay?",
                            "Xac nhan", JOptionPane.YES_NO_OPTION);
                    if (ok == JOptionPane.YES_OPTION) {
                        categoryDAO.deleteCategory(c.getId());
                        UiHelper.showInfo(this, "Xoa danh muc thanh cong");
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
