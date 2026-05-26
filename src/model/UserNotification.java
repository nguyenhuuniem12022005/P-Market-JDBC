package model;

import java.time.LocalDateTime;

public class UserNotification {
    private int id;
    private boolean isRead;
    private LocalDateTime readAt;
    private Notification notification;
    private Account account;

    public UserNotification() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    public Notification getNotification() { return notification; }
    public void setNotification(Notification notification) { this.notification = notification; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
}
