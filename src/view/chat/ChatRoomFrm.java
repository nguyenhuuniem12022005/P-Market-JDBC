package view.chat;

import view.user.UiHelper;

import dao.ChatRoomDAO;
import dao.ImageDAO;
import dao.MessageDAO;
import model.ChatRoom;
import model.Message;
import model.SessionManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Module f - Phòng chat / gửi tin nhắn. */
public class ChatRoomFrm extends JFrame implements ActionListener {

    private final ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
    private final ImageDAO imageDAO = new ImageDAO();
    private final MessageDAO messageDAO = new MessageDAO();
    private ChatRoom chatRoom;
    private final JTextArea outMessages = new JTextArea();
    private final JTextField inContent = new JTextField(30);
    private final JLabel outAttachment = new JLabel("Chưa chọn ảnh");
    private final JButton btnAttach = new JButton("Đính kèm ảnh");
    private final JButton btnClearImage = new JButton("Bỏ ảnh");
    private final JButton btnSend = new JButton("Gửi");
    private String selectedImagePath;

    /** Mở chat từ chi tiết bài đăng. */
    public ChatRoomFrm(int otherAccountId, String title) {
        super("Chat - " + title);
        initRoom(otherAccountId);
    }

    /** Mở phòng đã có. */
    public ChatRoomFrm(ChatRoom room, String title) {
        super("Chat - " + title);
        this.chatRoom = room;
        buildUi();
        loadMessages();
    }

    private void initRoom(int otherAccountId) {
        try {
            int myId = SessionManager.getCurrentAccount().getId();
            chatRoom = chatRoomDAO.getOrCreateRoom(myId, otherAccountId);
            buildUi();
            loadMessages();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
            dispose();
        }
    }

    private void buildUi() {
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        outMessages.setEditable(false);
        outMessages.setLineWrap(true);
        outMessages.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inContent.addActionListener(this);
        btnAttach.addActionListener(this);
        btnClearImage.addActionListener(this);
        btnSend.addActionListener(this);
        btnClearImage.setEnabled(false);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottom.add(inContent, BorderLayout.CENTER);
        bottom.add(btnSend, BorderLayout.EAST);

        JPanel attachment = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        attachment.add(btnAttach);
        attachment.add(btnClearImage);
        attachment.add(outAttachment);
        bottom.add(attachment, BorderLayout.SOUTH);

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
                String who = m.getAccount().getId() == myId ? "Tôi" : m.getAccount().getFullName();
                String time = m.getSentAt() != null ? m.getSentAt().format(fmt) : "";
                sb.append("[").append(time).append("] ").append(who).append(": ");
                boolean hasContent = m.getContent() != null && !m.getContent().isBlank();
                if (hasContent) {
                    sb.append(m.getContent());
                }
                if (m.getImageUrl() != null && !m.getImageUrl().isBlank()) {
                    if (hasContent) {
                        sb.append(" ");
                    }
                    sb.append("[Ảnh: ").append(m.getImageUrl()).append("]");
                }
                sb.append("\n");
            }
            outMessages.setText(sb.toString());
            outMessages.setCaretPosition(outMessages.getDocument().getLength());
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAttach) {
            chooseImage();
            return;
        }
        if (e.getSource() == btnClearImage) {
            clearSelectedImage();
            return;
        }

        String content = inContent.getText().trim();
        if (content.isEmpty() && selectedImagePath == null) {
            UiHelper.showError(this, "Vui lòng nhập nội dung hoặc chọn ảnh.");
            return;
        }
        try {
            String imageUrl = null;
            if (selectedImagePath != null) {
                List<String> urls = imageDAO.uploadImages(List.of(selectedImagePath));
                if (!urls.isEmpty()) {
                    imageUrl = urls.get(0);
                }
            }
            messageDAO.sendMessage(chatRoom.getId(),
                    SessionManager.getCurrentAccount().getId(),
                    content.isEmpty() ? null : content,
                    imageUrl);
            inContent.setText("");
            clearSelectedImage();
            loadMessages();
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Ảnh (*.png, *.jpg, *.jpeg, *.gif)", "png", "jpg", "jpeg", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImagePath = chooser.getSelectedFile().getAbsolutePath();
            outAttachment.setText(chooser.getSelectedFile().getName());
            btnClearImage.setEnabled(true);
        }
    }

    private void clearSelectedImage() {
        selectedImagePath = null;
        outAttachment.setText("Chưa chọn ảnh");
        btnClearImage.setEnabled(false);
    }
}
