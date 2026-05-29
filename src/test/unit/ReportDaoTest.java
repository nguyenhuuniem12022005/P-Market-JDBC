package test.unit;

import dao.ReportDAO;
import dao.ReportEvidenceDAO;
import model.Report;
import model.ReportEvidence;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ReportDaoTest {

    private final ReportDAO reportDAO = new ReportDAO();
    private final ReportEvidenceDAO evidenceDAO = new ReportEvidenceDAO();

    @Test
    public void testAddReportForPost() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("JUnit report post"), "AVAILABLE");

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setPostId(postId);
        report.setAccountId(null);
        report.setReason("JUnit reason");

        int reportId = reportDAO.addReport(report);
        Assert.assertTrue(reportId > 0);

        Report saved = reportDAO.getReportById(reportId);
        Assert.assertNotNull(saved);
        Assert.assertEquals(Integer.valueOf(postId), saved.getPostId());
        Assert.assertEquals("PENDING", saved.getStatus());
    }

    @Test
    public void testGetPendingReports() throws Exception {
        List<Report> reports = reportDAO.getPendingReports();
        Assert.assertNotNull(reports);
        for (Report r : reports) {
            Assert.assertEquals("PENDING", r.getStatus());
        }
    }

    @Test
    public void testUpdateReportStatus() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("JUnit process report post"), "AVAILABLE");

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setPostId(postId);
        report.setReason("JUnit process reason");
        int reportId = reportDAO.createReport(report);

        Assert.assertTrue(reportDAO.updateStatus(reportId, "PROCESSED"));
        Assert.assertEquals("PROCESSED", reportDAO.getReportById(reportId).getStatus());
    }

    @Test
    public void testAddAndGetReportEvidence() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("JUnit evidence report post"), "AVAILABLE");

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setPostId(postId);
        report.setReason("JUnit evidence reason");
        int reportId = reportDAO.addReport(report);

        Assert.assertTrue(evidenceDAO.addEvidenceList(reportId, List.of("uploads/junit_evidence.png")));
        List<ReportEvidence> list = evidenceDAO.getEvidenceByReportId(reportId);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("uploads/junit_evidence.png", list.get(0).getImageUrl());
    }
}
