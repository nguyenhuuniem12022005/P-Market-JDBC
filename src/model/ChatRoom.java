package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private int id;
    private LocalDateTime createdAt;
    private List<ChatRoomMember> listMember = new ArrayList<>();
    private List<Message> listMessage = new ArrayList<>();

    public ChatRoom() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<ChatRoomMember> getListMember() { return listMember; }
    public void setListMember(List<ChatRoomMember> listMember) { this.listMember = listMember; }
    public List<Message> getListMessage() { return listMessage; }
    public void setListMessage(List<Message> listMessage) { this.listMessage = listMessage; }
}
