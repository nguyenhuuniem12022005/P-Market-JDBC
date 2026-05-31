package view.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class UiHelper {

    private UiHelper() {}

    public static void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
        Font font = new Font("Segoe UI", Font.PLAIN, 13);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font.deriveFont(Font.BOLD));
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
        UIManager.put("TitledBorder.font", titleFont);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("control", new Color(246, 248, 251));
        UIManager.put("nimbusBase", new Color(45, 83, 130));
        UIManager.put("nimbusFocus", new Color(70, 130, 180));
        UIManager.put("Table.alternateRowColor", new Color(242, 246, 250));
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(240, 42));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 14, 8, 14));
        btn.setBackground(new Color(40, 93, 151));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 74, 120)),
                new EmptyBorder(8, 12, 8, 12)));
        return btn;
    }

    public static String statusLabel(String status) {
        if (status == null) return "";
        return switch (status.toUpperCase()) {
            case "ACTIVE" -> "Đang hoạt động";
            case "BANNED" -> "Đã khóa";
            case "DELETED" -> "Đã xóa";
            case "PENDING" -> "Chờ xử lý";
            case "RESOLVED", "PROCESSED" -> "Đã xử lý";
            case "REJECTED" -> "Bị bác bỏ";
            default -> status;
        };
    }
}
