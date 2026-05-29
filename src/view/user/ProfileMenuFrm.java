package view.user;

import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Module a — Menu ho so khi click vao anh dai dien */
public class ProfileMenuFrm extends JDialog implements ActionListener {

    private final JFrame home;
    private final JButton btnProfile = new JButton("Ho so ca nhan");
    private final JButton btnLogout = new JButton("Dang xuat");

    public ProfileMenuFrm(JFrame home) {
        super(home, "Tai khoan", true);
        this.home = home;
        setSize(220, 140);
        setLocationRelativeTo(home);

        JPanel menu = new JPanel(new GridLayout(0, 1, 8, 8));
        menu.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        btnProfile.addActionListener(this);
        btnLogout.addActionListener(this);
        menu.add(btnProfile);
        menu.add(btnLogout);
        add(menu);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnProfile) {
            dispose();
            new ProfileFrm().setVisible(true);
        } else if (e.getSource() == btnLogout) {
            SessionManager.clear();
            dispose();
            if (home != null) home.dispose();
            new LoginFrm().setVisible(true);
        }
    }
}
