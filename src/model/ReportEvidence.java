package model;

public class ReportEvidence {
    private int id;
    private String imageUrl;
    private Report report;

    public ReportEvidence() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Report getReport() { return report; }
    public void setReport(Report report) { this.report = report; }
}
