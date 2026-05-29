package view.category;

import view.user.UiHelper;
import dao.CategoryDAO;
import dao.PostDAO;
import model.Category;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module d — Chon danh muc thay the khi xoa danh muc con bai dang */
public class CategoryTransferFrm extends JDialog implements ActionListener {

    private final Category deleting;
    private final Runnable onDone;
    private final JComboBox<Category> inTarget = new JComboBox<>();
    private final JButton btnConfirm = new JButton("Xac nhan");
    private final JButton btnCancel = new JButton("Huy");
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PostDAO postDAO = new PostDAO();

    public CategoryTransferFrm(JFrame parent, Category deleting, int postCount, Runnable onDone) {
        super(parent, "Chuyen bai dang truoc khi xoa", true);
        this.deleting = deleting;
        this.onDone = onDone;
        setSize(440, 200);
        setLocationRelativeTo(parent);

        JPanel info = new JPanel(new GridLayout(0, 1, 6, 6));
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        info.add(new JLabel("Danh muc \"" + deleting.getName() + "\" con " + postCount + " bai dang."));
        info.add(new JLabel("Chon danh muc thay the:"));
        info.add(inTarget);

        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(e -> dispose());
        JPanel bottom = new JPanel();
        bottom.add(btnConfirm);
        bottom.add(btnCancel);

        setLayout(new BorderLayout());
        add(info, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        loadTargets();
    }

    private void loadTargets() {
        try {
            for (Category c : categoryDAO.getCategories()) {
                if (c.getId() != deleting.getId()) {
                    inTarget.addItem(c);
                }
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Category target = (Category) inTarget.getSelectedItem();
        if (target == null) {
            UiHelper.showError(this, "Vui long chon danh muc thay the.");
            return;
        }
        try {
            postDAO.transferPostsToCategory(deleting.getId(), target.getId());
            categoryDAO.deleteCategory(deleting.getId());
            UiHelper.showInfo(this, "Xoa danh muc thanh cong");
            dispose();
            if (onDone != null) onDone.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
