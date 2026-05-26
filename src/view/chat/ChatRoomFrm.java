package view.chat;

import view.user.UiHelper;

import dao.ChatRoomDAO;
import dao.MessageDAO;
import model.ChatRoom;
import model.Message;
import model.SessionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Module f — Phong chat / gui tin nhan */
public class ChatRoomFrm extends JFrame implements ActionListener {

    private final ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
    private final MessageDAO messageDAO = new MessageDAO();
    private ChatRoom chatRoom;
    private final JTextArea outMessages = new JTextArea();
    private final JTextField inContent = new JTextField(30);
    private final JButton btnSend = new JButton("Gui");

    /** Mo chat tu chi tiet bai dang */
    public ChatRoomFrm(int otherAccountId, String title) {
        super("Chat — " + title);
        initRoom(otherAccountId);
    }

    /** Mo phong da co */
    public ChatRoomFrm(ChatRoom room, String title) {
        super("Chat — " + title);
        this.chatRoom = room;
        buildUi();
        loadMessages();
    }

    private void initRoom(int otherAccountId) {
        try {
            int myId = SessionManager.getCurrentAccount().getId();
            chatRoom = chatRoomDAO.findOrCreateRoom(myId, otherAccountId);
            buildUi();
            loadMessages();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
            dispose();
        }
    }

    private void buildUi() {
        setSize(480, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outMessages.setEditable(false);
        inContent.addActionListener(this);
        btnSend.addActionListener(this);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.add(inContent, BorderLayout.CENTER);
        bottom.add(btnSend, BorderLayout.EAST);

        add(new JScrollPane(outMessages), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadMessages() {
        try {
            List<Message> msgs = messageDAO.getMessagesByRoom(chatRoom.getId());
            StringBuilder sb = new StringBuilder();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
            int myId = SessionManager.getCurrentAccount().getId();
            for (Message m : msgs) {
                String who = m.getAccount().getId() == myId ? "Toi" : m.getAccount().getFullName();
                String time = m.getSentAt() != null ? m.getSentAt().format(fmt) : "";
                sb.append("[").append(time).append("] ").append(who).append(": ")
                        .append(m.getContent()).append("\n");
            }
            outMessages.setText(sb.toString());
            outMessages.setCaretPosition(outMessages.getDocument().getLength());
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String content = inContent.getText().trim();
        if (content.isEmpty()) return;
        try {
            messageDAO.sendMessage(chatRoom.getId(),
                    SessionManager.getCurrentAccount().getId(), content, null);
            inContent.setText("");
            loadMessages();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
