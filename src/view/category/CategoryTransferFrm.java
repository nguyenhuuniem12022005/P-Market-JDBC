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
    private final JButton btnConfirm = new JButton("Xác nhận");
    private final JButton btnCancel = new JButton("Hủy");
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PostDAO postDAO = new PostDAO();

    public CategoryTransferFrm(JFrame parent, Category deleting, int postCount, Runnable onDone) {
        super(parent, "Chuyển bài đăng trước khi xóa", true);
        this.deleting = deleting;
        this.onDone = onDone;
        setSize(440, 200);
        setLocationRelativeTo(parent);

        JPanel info = new JPanel(new GridLayout(0, 1, 6, 6));
        info.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        info.add(new JLabel("Danh mục \"" + deleting.getName() + "\" còn " + postCount + " bài đăng."));
        info.add(new JLabel("Chọn danh mục thay thế:"));
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
            UiHelper.showError(this, "Vui lòng chọn danh mục thay thế.");
            return;
        }
        try {
            postDAO.transferPostsToCategory(deleting.getId(), target.getId());
            categoryDAO.deleteCategory(deleting.getId());
            UiHelper.showInfo(this, "Xóa danh mục thành công.");
            dispose();
            if (onDone != null) onDone.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
