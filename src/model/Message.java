package model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String content;
    private String imageUrl;
    private LocalDateTime sentAt;
    private ChatRoom chatRoom;
    private Account account;

    public Message() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public ChatRoom getChatRoom() { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}
