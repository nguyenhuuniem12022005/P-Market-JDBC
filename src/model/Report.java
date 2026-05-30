package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Report {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSED = "PROCESSED";
    public static final String STATUS_REJECTED = "REJECTED";

    private int id;
    private int reporterId;
    private Integer postId;
    private Integer accountId;
    private String reason;
    private String detail;
    private String status;
    private LocalDateTime createdAt;
    private Account reporter;
    private Account account;
    private Post post;
    private List<ReportEvidence> listEvidence = new ArrayList<>();

    public Report() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getReporterId() { return reporterId; }
    public void setReporterId(int reporterId) { this.reporterId = reporterId; }
    public Integer getPostId() { return postId; }
    public void setPostId(Integer postId) { this.postId = postId; }
    public Integer getAccountId() { return accountId; }
    public void setAccountId(Integer accountId) { this.accountId = accountId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Account getReporter() { return reporter; }
    public void setReporter(Account reporter) { this.reporter = reporter; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public List<ReportEvidence> getListEvidence() { return listEvidence; }
    public void setListEvidence(List<ReportEvidence> listEvidence) { this.listEvidence = listEvidence; }
}
