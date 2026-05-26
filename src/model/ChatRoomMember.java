package model;

import java.time.LocalDateTime;

public class ChatRoomMember {
    private int id;
    private LocalDateTime joinedAt;
    private ChatRoom chatRoom;
    private Account account;

    public ChatRoomMember() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    public ChatRoom getChatRoom() { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}
