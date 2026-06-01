
package view.post;

import dao.CategoryDAO;
import dao.ImageDAO;
import dao.PostDAO;
import model.Account;
import model.Category;
import model.Post;
import model.SessionManager;
import view.user.UiHelper;

import javax.swing.*;
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
    private final JButton btnBrowse = new JButton("Chon anh");
    private final JButton btnSubmit = new JButton("Dang bai");
    private final JButton btnCancel = new JButton("Huy");
    private final PostDAO postDAO = new PostDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ImageDAO imageDAO = new ImageDAO();
    private final Runnable onSaved;
    private final List<String> selectedImages = new ArrayList<>();
    public CreatePostFrm() {
        this(null);
    }
    public CreatePostFrm(
            Runnable onSaved
    ) {
        super("Dang bai ban hang");
        this.onSaved = onSaved;
        setSize(550, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        JPanel form = new JPanel(  new GridBagLayout() );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(6, 6, 6, 6);

        gbc.anchor =
                GridBagConstraints.WEST;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        int row = 0;

        addRow(
                form,
                gbc,
                row++,
                "Tieu de:",
                inTitle
        );

        gbc.gridy = row;
        gbc.gridx = 0;

        form.add(
                new JLabel("Mo ta:"),
                gbc
        );

        gbc.gridx = 1;

        form.add(
                new JScrollPane(
                        inDescription
                ),
                gbc
        );

        row++;

        addRow(
                form,
                gbc,
                row++,
                "Gia (VND):",
                inPrice
        );

        addRow(
                form,
                gbc,
                row++,
                "So luong:",
                inQuantity
        );

        inQuantity.setText("1");

        addRow(
                form,
                gbc,
                row++,
                "Danh muc:",
                inCategory
        );

        gbc.gridy = row;
        gbc.gridx = 0;

        form.add(
                new JLabel("Anh:"),
                gbc
        );

        JPanel imgPanel =
                new JPanel(
                        new BorderLayout()
                );

        imgPanel.add(
                inImagePath,
                BorderLayout.CENTER
        );

        imgPanel.add(
                btnBrowse,
                BorderLayout.EAST
        );

        gbc.gridx = 1;

        form.add(
                imgPanel,
                gbc
        );

        row++;

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER
                        )
                );

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancel);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        form.add(
                buttonPanel,
                gbc
        );
        

        btnBrowse.addActionListener(this);
        btnSubmit.addActionListener(this);
        btnCancel.addActionListener(this);

        add(form, BorderLayout.CENTER);

        loadCategories();
    }

    private void addRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent field
    ) {

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 1;

        panel.add(
                new JLabel(label),
                gbc
        );

        gbc.gridx = 1;

        panel.add(
                field,
                gbc
        );
    }

    private void loadCategories() {

        try {

            inCategory.removeAllItems();

            for (Category c :
                    categoryDAO
                            .getAllCategories()) {

                inCategory.addItem(c);
            }

        } catch (Exception ex) {

            UiHelper.showError(
                    this,
                    ex.getMessage()
            );
        }
    }

    @Override
    public void actionPerformed(
            ActionEvent e
    ) {
        if (e.getSource()== btnCancel) {
            dispose();
            return;
        }
        if (e.getSource()
                == btnBrowse) {
            JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);         
            int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
                selectedImages.clear();
                StringBuilder sb = new StringBuilder();
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

        if (e.getSource()
                != btnSubmit) {
            return;
        }

        String title =
                inTitle.getText()
                        .trim();

        String description =
                inDescription.getText()
                        .trim();

        Category category =
                (Category)
                        inCategory
                                .getSelectedItem();
        if (selectedImages.isEmpty()) {
        UiHelper.showError(
            this,
            "Vui long tai len it nhat mot anh san pham."
        );

    return;
}

        if (title.isEmpty()
                || inPrice.getText()
                .trim()
                .isEmpty()
                || category == null) {

            UiHelper.showError(
                    this,
                    "Vui long nhap day du thong tin."
            );

            return;
        }

        double price;
        int quantity;

        try {

            price =
                    Double.parseDouble(
                            inPrice
                                    .getText()
                                    .trim()
                    );

            quantity =
                    Integer.parseInt(
                            inQuantity
                                    .getText()
                                    .trim()
                    );

        } catch (
                NumberFormatException ex
        ) {

            UiHelper.showError(
                    this,
                    "Gia va so luong phai la so hop le."
            );

            return;
        }

        Account me = SessionManager.getCurrentAccount();
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(
                description
        );
        post.setPrice(price);
        post.setQuantity(
                quantity
        );
        post.setAccount(me);
        post.setCategory(
                category
        );

        try {

           List<String> urls =
        imageDAO.uploadImages(
                selectedImages
        );
            postDAO.createPost(
                    post
            );
            imageDAO.saveImages(
                    post.getId(),
                    urls
            );
            UiHelper.showInfo(
                    this,
                    "Dang bai thanh cong!"
            );
            dispose();

            if (onSaved != null) {
                onSaved.run();
            }
        } catch (Exception ex) {

            UiHelper.showError(
                    this,
                    ex.getMessage()
            );
        }
    }
}