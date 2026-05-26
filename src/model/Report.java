package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private int id;
    private String targetType;
    private int targetId;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private Account account;
    private Post post;
    private List<ReportEvidence> listEvidence = new ArrayList<>();

    public Report() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public int getTargetId() { return targetId; }
    public void setTargetId(int targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public List<ReportEvidence> getListEvidence() { return listEvidence; }
    public void setListEvidence(List<ReportEvidence> listEvidence) { this.listEvidence = listEvidence; }
}
