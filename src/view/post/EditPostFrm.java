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
    private final JButton btnBrowse = new JButton("Chon anh");
    private final JButton btnSave = new JButton("Luu thay doi");
    private final PostDAO postDAO = new PostDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ImageDAO imageDAO = new ImageDAO();

    public EditPostFrm(int postId, Runnable onSaved) {
        super("Sua bai dang #" + postId);
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
        addRow(form, gbc, row++, "Tieu de:", inTitle);
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        form.add(new JLabel("Mo ta:"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(inDescription), gbc);
        row++;
        addRow(form, gbc, row++, "Gia (VND):", inPrice);
        addRow(form, gbc, row++, "So luong:", inQuantity);
        addRow(form, gbc, row++, "Danh muc:", inCategory);

        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(new JLabel("Anh moi:"), gbc);
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
                UiHelper.showError(this, "Khong tim thay bai dang.");
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
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                inImagePath.setText(chooser.getSelectedFile().getAbsolutePath());
            }
            return;
        }
        if (e.getSource() != btnSave) return;

        String title = inTitle.getText().trim();
        Category cat = (Category) inCategory.getSelectedItem();
        if (title.isEmpty() || cat == null) {
            UiHelper.showError(this, "Vui long nhap tieu de va chon danh muc.");
            return;
        }
        try {
            post.setTitle(title);
            post.setDescription(inDescription.getText().trim());
            post.setPrice(Double.parseDouble(inPrice.getText().trim()));
            post.setQuantity(Integer.parseInt(inQuantity.getText().trim()));
            post.setCategory(cat);

            postDAO.updatePost(post);

            String path = inImagePath.getText().trim();
            if (!path.isEmpty()) {
                List<String> sources = new ArrayList<>();
                sources.add(path);
                List<String> urls = imageDAO.uploadImages(sources);
                imageDAO.saveImages(postId, urls);
            }
            UiHelper.showInfo(this, "Cap nhat bai dang thanh cong");
            dispose();
            if (onSaved != null) onSaved.run();
        } catch (NumberFormatException ex) {
            UiHelper.showError(this, "Gia va so luong phai la so hop le.");
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
