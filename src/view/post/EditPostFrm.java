package view.post;

import view.user.UiHelper;
import dao.CategoryDAO;
import dao.ImageDAO;
import dao.PostDAO;
import model.Category;
import model.Post;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** Module b — Sửa bài đăng */
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
     private final JButton btnCancel = new JButton("Hủy");
    private final PostDAO postDAO = new PostDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ImageDAO imageDAO = new ImageDAO();
    private final List<String> selectedImages =
        new ArrayList<>();

    public EditPostFrm(int postId, Runnable onSaved) {
        super("Sửa bài đăng #" + postId);
        this.postId = postId;
        this.onSaved = onSaved;
        setSize(520, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
        form.add(new JLabel("Anh:"), gbc);
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(inImagePath, BorderLayout.CENTER);
        btnBrowse.addActionListener(this);
        imgPanel.add(btnBrowse, BorderLayout.EAST);
        gbc.gridx = 1;
        form.add(imgPanel, gbc);
        row++;
        btnSave.addActionListener(this);
        btnCancel.addActionListener(this); 
        
       JPanel buttonPanel =
        new JPanel(
                new FlowLayout(
                        FlowLayout.CENTER
                )
        );
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        form.add(
                buttonPanel,
                gbc
        );
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
            if (post.getListImage() != null && !post.getListImage().isEmpty()) {
            inImagePath.setText(
            post.getListImage()
                .get(0)
                .getImageUrl()   
    );
}

        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBrowse) {
            JFileChooser chooser =
                    new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            if (chooser.showOpenDialog(this)
                    == JFileChooser.APPROVE_OPTION) {
                selectedImages.clear();
                StringBuilder sb =
                        new StringBuilder();
                for (java.io.File file :
                        chooser.getSelectedFiles()) {
                    selectedImages.add(
                            file.getAbsolutePath()
                    );
                    sb.append(file.getName())
                    .append("; ");
                }
                inImagePath.setText(
                        sb.toString()
                );
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
        try {
            post.setTitle(title);
            post.setDescription(inDescription.getText().trim());
            post.setPrice(Double.parseDouble(inPrice.getText().trim()));
            post.setQuantity(Integer.parseInt(inQuantity.getText().trim()));
            post.setCategory(cat);

            postDAO.updatePost(post);
            if (selectedImages.isEmpty()) {

            UiHelper.showError(
                    this,
                    "Vui lòng tải lên ít nhất một ảnh mới."
            );
            return;
        }
            List<String> urls = imageDAO.uploadImages(selectedImages);
            imageDAO.saveImages(postId, urls);
            UiHelper.showInfo(this, "Cập nhật bài đăng thành công");
            dispose();
            if (onSaved != null) onSaved.run();
        } catch (NumberFormatException ex) {
            UiHelper.showError(this, "Giá và số lượng phải là số hợp lệ.");
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
