package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Notification {
    private int id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private List<UserNotification> listUserNotification = new ArrayList<>();

    public Notification() {}

    public Notification(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<UserNotification> getListUserNotification() { return listUserNotification; }
    public void setListUserNotification(List<UserNotification> listUserNotification) {
        this.listUserNotification = listUserNotification;
    }
}
