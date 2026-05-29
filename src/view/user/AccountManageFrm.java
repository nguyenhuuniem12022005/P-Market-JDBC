package view.user;

import view.user.UiHelper;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/** Module b — Quan ly tai khoan (Admin) — AccountManageFrm */
public class AccountManageFrm extends JFrame implements ActionListener {

    private final JTextField inKeyword = new JTextField(20);
    private final JButton btnSearch = new JButton("Tim kiem");
    private final JTable tblAccounts = new JTable();
    private final DefaultTableModel tableModel;
    private final AccountDAO accountDAO = new AccountDAO();
    private List<Account> currentList = List.of();

    public AccountManageFrm() {
        super("Quan ly tai khoan");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Ten", "Email", "Trang thai", "Hanh dong"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tblAccounts.setModel(tableModel);
        tblAccounts.getColumn("Hanh dong").setCellRenderer(new ButtonRenderer());
        tblAccounts.getColumn("Hanh dong").setCellEditor(new ButtonEditor(new JCheckBox()));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tim (ten/email):"));
        top.add(inKeyword);
        btnSearch.addActionListener(this);
        top.add(btnSearch);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tblAccounts), BorderLayout.CENTER);

        loadAccounts("");
    }

    private void loadAccounts(String keyword) {
        try {
            currentList = accountDAO.searchAccounts(keyword);
            tableModel.setRowCount(0);
            for (Account a : currentList) {
                String action = "banned".equalsIgnoreCase(a.getStatus()) ? "Mo khoa" : "Khoa tai khoan";
                tableModel.addRow(new Object[]{
                        a.getId(),
                        a.getFullName(),
                        a.getEmail(),
                        UiHelper.statusLabel(a.getStatus()),
                        action
                });
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            loadAccounts(inKeyword.getText());
        }
    }

    private void handleRowAction(int row) {
        if (row < 0 || row >= currentList.size()) return;
        Account acc = currentList.get(row);
        if ("banned".equalsIgnoreCase(acc.getStatus())) {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Mo khoa tai khoan " + acc.getFullName() + "?",
                    "Xac nhan", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    accountDAO.unlockAccount(acc.getId());
                    UiHelper.showInfo(this, "Mo khoa tai khoan thanh cong.");
                    loadAccounts(inKeyword.getText());
                } catch (Exception ex) {
                    UiHelper.showError(this, ex.getMessage());
                }
            }
        } else {
            new LockConfirmFrm(this, acc, () -> loadAccounts(inKeyword.getText())).setVisible(true);
        }
    }

    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        ButtonRenderer() { setOpaque(true); }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private int editingRow = -1;
        ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            JButton btn = new JButton();
            btn.addActionListener(e -> {
                fireEditingStopped();
                handleRowAction(editingRow);
            });
            editorComponent = btn;
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            editingRow = row;
            ((JButton) editorComponent).setText(value.toString());
            return editorComponent;
        }
    }
}
