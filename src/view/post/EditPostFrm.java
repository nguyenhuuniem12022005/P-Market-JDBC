package view.post;

import view.user.UiHelper;
import dao.CategoryDAO;
import dao.ImageDAO;
import dao.PostDAO;
import model.Category;
import model.Post;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** Module b — Sua bai dang */
public class EditPostFrm extends JFrame implements ActionListener {

    private final int postId;
    private final Runnable onSaved;
    private Post post;
    private final JTextField inTitle = new JTextField(30);
    private final JTextArea inDescription = new JTextArea(5, 30);
    private final JTextField inPrice = new JTextField(10);
    private final JTextField inQuantity = new JTextField(5);
    private final JComboBox<Category> inCategory = new JComboBox<>();
    private final JTextField inImagePath = new JTextField(25);
    private final JButton btnBrowse = new JButton("Chọn ảnh");
    private final JButton btnSave = new JButton("Lưu thay đổi");
    private final PostDAO postDAO = new PostDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ImageDAO imageDAO = new ImageDAO();

    public EditPostFrm(int postId, Runnable onSaved) {
        super("Sửa bài đăng #" + postId);
        this.postId = postId;
        this.onSaved = onSaved;
        setSize(520, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inImagePath.setEditable(false);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addRow(form, gbc, row++, "Tiêu đề:", inTitle);
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        form.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(inDescription), gbc);
        row++;
        addRow(form, gbc, row++, "Giá (VND):", inPrice);
        addRow(form, gbc, row++, "Số lượng:", inQuantity);
        addRow(form, gbc, row++, "Danh mục:", inCategory);

        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(new JLabel("Ảnh mới:"), gbc);
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(inImagePath, BorderLayout.CENTER);
        btnBrowse.addActionListener(this);
        imgPanel.add(btnBrowse, BorderLayout.EAST);
        gbc.gridx = 1;
        form.add(imgPanel, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        btnSave.addActionListener(this);
        form.add(btnSave, gbc);

        add(form, BorderLayout.CENTER);
        loadData();
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(field, gbc);
    }

    private void loadData() {
        try {
            for (Category c : categoryDAO.getAllCategories()) {
                inCategory.addItem(c);
            }
            post = postDAO.getPostDetails(postId);
            if (post == null) {
                UiHelper.showError(this, "Không tìm thấy bài đăng.");
                dispose();
                return;
            }
            inTitle.setText(post.getTitle());
            inDescription.setText(post.getDescription());
            inPrice.setText(String.valueOf(post.getPrice()));
            inQuantity.setText(String.valueOf(post.getQuantity()));
            if (post.getCategory() != null) {
                for (int i = 0; i < inCategory.getItemCount(); i++) {
                    if (inCategory.getItemAt(i).getId() == post.getCategory().getId()) {
                        inCategory.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBrowse) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter(
                    "Ảnh (*.png, *.jpg, *.jpeg, *.gif)", "png", "jpg", "jpeg", "gif"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inImagePath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
            return;
        }
        if (e.getSource() != btnSave) return;

        String title = inTitle.getText().trim();
        Category cat = (Category) inCategory.getSelectedItem();
        if (title.isEmpty() || cat == null) {
            UiHelper.showError(this, "Vui lòng nhập tiêu đề và chọn danh mục.");
            return;
        }
        String path = inImagePath.getText().trim();
        boolean hasExistingImage = post.getListImage() != null && !post.getListImage().isEmpty();
        if (!hasExistingImage && path.isEmpty()) {
            UiHelper.showError(this, "Bài đăng phải có ít nhất một ảnh.");
            return;
        }
        try {
            post.setTitle(title);
            post.setDescription(inDescription.getText().trim());
            post.setPrice(Double.parseDouble(inPrice.getText().trim()));
            post.setQuantity(Integer.parseInt(inQuantity.getText().trim()));
            post.setCategory(cat);

            postDAO.updatePost(post);

            if (!path.isEmpty()) {
                List<String> sources = new ArrayList<>();
                sources.add(path);
                List<String> urls = imageDAO.uploadImages(sources);
                imageDAO.saveImages(postId, urls);
            }
            UiHelper.showInfo(this, "Cập nhật bài đăng thành công.");
            dispose();
            if (onSaved != null) onSaved.run();
        } catch (NumberFormatException ex) {
            UiHelper.showError(this, "Giá và số lượng phải là số hợp lệ.");
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
