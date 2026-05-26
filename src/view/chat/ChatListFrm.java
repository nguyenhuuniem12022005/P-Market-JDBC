package view.chat;

import view.user.UiHelper;

import dao.ChatRoomDAO;
import model.Account;
import model.ChatRoom;
import model.SessionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Module f — Danh sach phong chat */
public class ChatListFrm extends JFrame {

    private final ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
    private List<ChatRoom> rooms = List.of();

    public ChatListFrm() {
        super("Hop thu noi bo");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Phong", "Doi tac"}, 0);
        JTable table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    openRoom(row);
                }
            }
        });

        try {
            int myId = SessionManager.getCurrentAccount().getId();
            rooms = chatRoomDAO.getRoomsForAccount(myId);
            for (ChatRoom room : rooms) {
                Account other = chatRoomDAO.getOtherMember(room, myId);
                String name = other != null ? other.getFullName() : "Phong #" + room.getId();
                model.addRow(new Object[]{"#" + room.getId(), name});
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
        JLabel hint = new JLabel("Double-click de mo phong chat");
        hint.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(hint, BorderLayout.SOUTH);
    }

    private void openRoom(int row) {
        if (row < 0 || row >= rooms.size()) return;
        ChatRoom room = rooms.get(row);
        try {
            Account other = chatRoomDAO.getOtherMember(room, SessionManager.getCurrentAccount().getId());
            String title = other != null ? other.getFullName() : "Chat";
            new ChatRoomFrm(room, title).setVisible(true);
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
