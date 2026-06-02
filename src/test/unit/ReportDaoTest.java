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
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("Kiểm thử báo cáo bài đăng"), "ACTIVE");

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setPostId(postId);
        report.setAccountId(null);
        report.setReason("Lý do kiểm thử");
        report.setDetail("Chi tiết báo cáo kiểm thử");

        int reportId = reportDAO.addReport(report);
        Assert.assertTrue(reportId > 0);

        Report saved = reportDAO.getReportById(reportId);
        Assert.assertNotNull(saved);
        Assert.assertEquals(Integer.valueOf(postId), saved.getPostId());
        Assert.assertEquals("Chi tiết báo cáo kiểm thử", saved.getDetail());
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
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("Kiểm thử xử lý báo cáo bài đăng"), "ACTIVE");

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setPostId(postId);
        report.setReason("Lý do xử lý kiểm thử");
        int reportId = reportDAO.createReport(report);

        Assert.assertTrue(reportDAO.updateStatus(reportId, Report.STATUS_RESOLVED));
        Assert.assertEquals(Report.STATUS_RESOLVED, reportDAO.getReportById(reportId).getStatus());

        try {
            reportDAO.updateStatus(reportId, Report.STATUS_REJECTED);
            Assert.fail("Kỳ vọng báo cáo đã xử lý không được đổi trạng thái");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Không thể chuyển trạng thái"));
        }
    }

    @Test
    public void testDuplicatePendingReportForPostIsRejected() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("Kiểm thử trùng báo cáo bài đăng"), "ACTIVE");

        Report first = new Report();
        first.setReporterId(reporterId);
        first.setPostId(postId);
        first.setReason("Lý do trùng kiểm thử");
        int firstReportId = reportDAO.addReport(first);

        Report duplicate = new Report();
        duplicate.setReporterId(reporterId);
        duplicate.setPostId(postId);
        duplicate.setReason("Lý do trùng kiểm thử lần nữa");

        try {
            reportDAO.addReport(duplicate);
            Assert.fail("Kỳ vọng từ chối báo cáo trùng đang chờ xử lý");
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
        int targetAccountId = DbTestUtil.insertStudent(DbTestUtil.unique("tai_khoan_bi_bao_cao_trung"));

        Report first = new Report();
        first.setReporterId(reporterId);
        first.setAccountId(targetAccountId);
        first.setReason("Lý do trùng tài khoản kiểm thử");
        reportDAO.addReport(first);

        Report duplicate = new Report();
        duplicate.setReporterId(reporterId);
        duplicate.setAccountId(targetAccountId);
        duplicate.setReason("Lý do trùng tài khoản kiểm thử lần nữa");

        try {
            reportDAO.addReport(duplicate);
            Assert.fail("Kỳ vọng từ chối báo cáo tài khoản trùng đang chờ xử lý");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("dang cho xu ly"));
        }
    }

    @Test
    public void testAddAndGetReportEvidence() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("Kiểm thử bằng chứng báo cáo bài đăng"), "ACTIVE");

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setPostId(postId);
        report.setReason("Lý do bằng chứng kiểm thử");
        int reportId = reportDAO.addReport(report);

        Assert.assertTrue(evidenceDAO.addEvidenceList(reportId, List.of("uploads/junit_evidence.png")));
        List<ReportEvidence> list = evidenceDAO.getEvidenceByReportId(reportId);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("uploads/junit_evidence.png", list.get(0).getImageUrl());
    }
}
