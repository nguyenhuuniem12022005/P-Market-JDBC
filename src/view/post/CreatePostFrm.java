package view.post;

import view.user.UiHelper;
import dao.CategoryDAO;
import dao.ImageDAO;
import dao.PostDAO;
import model.Account;
import model.Category;
import model.Post;
import model.SessionManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** Module c — Dang bai */
public class CreatePostFrm extends JFrame implements ActionListener {

    private final JTextField inTitle = new JTextField(30);
    private final JTextArea inDescription = new JTextArea(5, 30);
    private final JTextField inPrice = new JTextField(10);
    private final JTextField inQuantity = new JTextField(5);
    private final JComboBox<Category> inCategory = new JComboBox<>();
    private final JTextField inImagePath = new JTextField(25);
    private final JButton btnBrowse = new JButton("Chọn ảnh");
    private final JButton btnSubmit = new JButton("Đăng bài");
    private final PostDAO postDAO = new PostDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ImageDAO imageDAO = new ImageDAO();
    private final Runnable onSaved;

    public CreatePostFrm() {
        this(null);
    }

    public CreatePostFrm(Runnable onSaved) {
        super("Đăng bài bán hàng");
        this.onSaved = onSaved;
        setSize(520, 420);
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
        form.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(inDescription), gbc);
        row++;
        addRow(form, gbc, row++, "Giá (VND):", inPrice);
        addRow(form, gbc, row++, "Số lượng:", inQuantity);
        inQuantity.setText("1");
        addRow(form, gbc, row++, "Danh mục:", inCategory);

        gbc.gridy = row;
        gbc.gridx = 0;
        form.add(new JLabel("Ảnh (*):"), gbc);
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
        btnSubmit.addActionListener(this);
        form.add(btnSubmit, gbc);

        add(form, BorderLayout.CENTER);
        loadCategories();
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(field, gbc);
    }

    private void loadCategories() {
        try {
            inCategory.removeAllItems();
            for (Category c : categoryDAO.getAllCategories()) {
                inCategory.addItem(c);
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
        if (e.getSource() != btnSubmit) return;

        String title = inTitle.getText().trim();
        String desc = inDescription.getText().trim();
        Category cat = (Category) inCategory.getSelectedItem();
        if (title.isEmpty() || cat == null) {
            UiHelper.showError(this, "Vui lòng nhập tiêu đề và chọn danh mục.");
            return;
        }
        String path = inImagePath.getText().trim();
        if (path.isEmpty()) {
            UiHelper.showError(this, "Bài đăng phải có ít nhất một ảnh.");
            return;
        }
        double price;
        int qty;
        try {
            price = Double.parseDouble(inPrice.getText().trim());
            qty = Integer.parseInt(inQuantity.getText().trim());
        } catch (NumberFormatException ex) {
            UiHelper.showError(this, "Giá và số lượng phải là số hợp lệ.");
            return;
        }

        Account me = SessionManager.getCurrentAccount();
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(desc);
        post.setPrice(price);
        post.setQuantity(qty);
        post.setAccount(me);
        post.setCategory(cat);

        try {
            List<String> sources = new ArrayList<>();
            sources.add(path);
            List<String> urls = imageDAO.uploadImages(sources);
            postDAO.createPost(post, urls);
            UiHelper.showInfo(this, "Đăng bài thành công!");
            dispose();
            if (onSaved != null) onSaved.run();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
