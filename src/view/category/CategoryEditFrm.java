package view.category;

import view.user.UiHelper;
import dao.CategoryDAO;
import model.Category;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module d — Them / sua danh muc */
public class CategoryEditFrm extends JFrame implements ActionListener {

    private final Category editing;
    private final Runnable onSaved;
    private final JTextField inName = new JTextField(20);
    private final JComboBox<Category> inParent = new JComboBox<>();
    private final JButton btnSave = new JButton("Luu");
    private final JButton btnCancel = new JButton("Huy");
    private final CategoryDAO categoryDAO = new CategoryDAO();

    /** Them moi: editing = null. Sua: truyen category co du lieu. */
    public CategoryEditFrm(Category editing, Runnable onSaved) {
        super(editing == null ? "Them danh muc moi" : "Sua danh muc");
        this.editing = editing;
        this.onSaved = onSaved;
        setSize(420, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Ten danh muc (*):"), gbc);
        gbc.gridx = 1;
        form.add(inName, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Danh muc cha:"), gbc);
        gbc.gridx = 1;
        form.add(inParent, gbc);

        btnSave.addActionListener(this);
        btnCancel.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(btnSave);
        bottom.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        loadParents();
    }

    private void loadParents() {
        try {
            inParent.addItem(null);
            for (Category c : categoryDAO.getCategories()) {
                if (editing != null && c.getId() == editing.getId()) {
                    continue;
                }
                inParent.addItem(c);
            }
            if (editing != null) {
                inName.setText(editing.getName());
                if (editing.getParent() != null) {
                    for (int i = 0; i < inParent.getItemCount(); i++) {
                        Category item = inParent.getItemAt(i);
                        if (item != null && item.getId() == editing.getParent().getId()) {
                            inParent.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private boolean validateCategory(String name) {
        return name != null && !name.trim().isEmpty();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = inName.getText().trim();
        if (!validateCategory(name)) {
            UiHelper.showError(this, "Ten danh muc khong duoc rong.");
            return;
        }
        try {
            int excludeId = editing != null ? editing.getId() : -1;
            if (categoryDAO.existsByName(name, excludeId)) {
                UiHelper.showError(this, "Ten danh muc da ton tai.");
                return;
            }
            Category parent = (Category) inParent.getSelectedItem();
            Category c = editing != null ? editing : new Category();
            c.setName(name);
            c.setParent(parent);
            if (editing == null) {
                categoryDAO.addCategory(c);
                UiHelper.showInfo(this, "Them danh muc moi thanh cong");
            } else {
                categoryDAO.updateCategory(c);
                UiHelper.showInfo(this, "Cap nhat danh muc thanh cong");
            }
            dispose();
            if (onSaved != null) onSaved.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
