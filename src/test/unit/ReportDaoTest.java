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
        report.setDetail("JUnit report detail");

        int reportId = reportDAO.addReport(report);
        Assert.assertTrue(reportId > 0);

        Report saved = reportDAO.getReportById(reportId);
        Assert.assertNotNull(saved);
        Assert.assertEquals(Integer.valueOf(postId), saved.getPostId());
        Assert.assertEquals("JUnit report detail", saved.getDetail());
        Assert.assertEquals(Report.STATUS_PENDING, saved.getStatus());
    }

    @Test
    public void testGetPendingReports() throws Exception {
        List<Report> reports = reportDAO.getPendingReports();
        Assert.assertNotNull(reports);
        for (Report r : reports) {
            Assert.assertEquals(Report.STATUS_PENDING, r.getStatus());
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

        Assert.assertTrue(reportDAO.updateStatus(reportId, Report.STATUS_PROCESSED));
        Assert.assertEquals(Report.STATUS_PROCESSED, reportDAO.getReportById(reportId).getStatus());

        try {
            reportDAO.updateStatus(reportId, Report.STATUS_REJECTED);
            Assert.fail("Expected processed report status to be terminal");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Khong the chuyen trang thai"));
        }
    }

    @Test
    public void testDuplicatePendingReportForPostIsRejected() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("JUnit duplicate report post"), "AVAILABLE");

        Report first = new Report();
        first.setReporterId(reporterId);
        first.setPostId(postId);
        first.setReason("JUnit duplicate reason");
        int firstReportId = reportDAO.addReport(first);

        Report duplicate = new Report();
        duplicate.setReporterId(reporterId);
        duplicate.setPostId(postId);
        duplicate.setReason("JUnit duplicate reason again");

        try {
            reportDAO.addReport(duplicate);
            Assert.fail("Expected duplicate pending report to be rejected");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("dang cho xu ly"));
        }

        Assert.assertTrue(reportDAO.updateStatus(firstReportId, Report.STATUS_REJECTED));
        int newReportId = reportDAO.addReport(duplicate);
        Assert.assertTrue(newReportId > 0);
    }

    @Test
    public void testDuplicatePendingReportForAccountIsRejected() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int targetAccountId = DbTestUtil.insertStudent(DbTestUtil.unique("duplicate_report_account"));

        Report first = new Report();
        first.setReporterId(reporterId);
        first.setAccountId(targetAccountId);
        first.setReason("JUnit duplicate account reason");
        reportDAO.addReport(first);

        Report duplicate = new Report();
        duplicate.setReporterId(reporterId);
        duplicate.setAccountId(targetAccountId);
        duplicate.setReason("JUnit duplicate account reason again");

        try {
            reportDAO.addReport(duplicate);
            Assert.fail("Expected duplicate pending account report to be rejected");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("dang cho xu ly"));
        }
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
