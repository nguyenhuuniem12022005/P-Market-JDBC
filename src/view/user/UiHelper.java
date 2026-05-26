package view.user;

import javax.swing.*;
import java.awt.*;

public final class UiHelper {

    private UiHelper() {}

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Loi", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thong bao", JOptionPane.INFORMATION_MESSAGE);
    }

    public static JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(220, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    public static String statusLabel(String status) {
        if (status == null) return "";
        return switch (status.toLowerCase()) {
            case "active" -> "Dang hoat dong";
            case "banned" -> "Da khoa";
            case "available" -> "Dang ban";
            case "sold" -> "Da ban";
            case "hidden" -> "Da an";
            case "pending" -> "Cho xu ly";
            case "resolved" -> "Da xu ly";
            case "rejected" -> "Bi bac bo";
            default -> status;
        };
    }
}
