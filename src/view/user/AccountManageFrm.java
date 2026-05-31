package view.user;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/** Module b - Quản lý tài khoản. */
public class AccountManageFrm extends JFrame implements ActionListener {

    private final JTextField inKeyword = new JTextField(20);
    private final JButton btnSearch = new JButton("Tìm kiếm");
    private final JTable tblAccounts = new JTable();
    private final DefaultTableModel tableModel;
    private final AccountDAO accountDAO = new AccountDAO();
    private List<Account> currentList = List.of();

    public AccountManageFrm() {
        super("Quản lý tài khoản");
        setSize(760, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Tên", "Email", "Trạng thái", "Hành động"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tblAccounts.setModel(tableModel);
        tblAccounts.setRowHeight(30);
        tblAccounts.getColumn("Hành động").setCellRenderer(new ButtonRenderer());
        tblAccounts.getColumn("Hành động").setCellEditor(new ButtonEditor(new JCheckBox()));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setBorder(BorderFactory.createEmptyBorder(6, 8, 2, 8));
        top.add(new JLabel("Tìm (tên/email):"));
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
                boolean banned = isBanned(a.getStatus());
                String action = banned ? "Mở khóa" : "Khóa tài khoản";
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
        if (isBanned(acc.getStatus())) {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Mở khóa tài khoản " + acc.getFullName() + "?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try {
                    accountDAO.unlockAccount(acc.getId());
                    UiHelper.showInfo(this, "Mở khóa tài khoản thành công.");
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

    private boolean isBanned(String status) {
        return Account.STATUS_BANNED.equalsIgnoreCase(status);
    }
}
