package test.unit;

import dao.ReportDAO;
import dao.ReportEvidenceDAO;
import model.Report;
import model.ReportEvidence;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ReportEvidenceDaoTest {

    private final ReportDAO reportDAO = new ReportDAO();
    private final ReportEvidenceDAO evidenceDAO = new ReportEvidenceDAO();

    @Test
    public void testAddEvidenceListAndGetEvidenceByReportId() throws Exception {
        int reporterId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(reporterId, categoryId, DbTestUtil.unique("evidence_post"), "AVAILABLE");

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setPostId(postId);
        report.setReason("JUnit evidence");
        int reportId = reportDAO.addReport(report);

        Assert.assertTrue(evidenceDAO.addEvidenceList(reportId, List.of("uploads/evidence_a.png", "uploads/evidence_b.png")));
        List<ReportEvidence> evidence = evidenceDAO.getEvidenceByReportId(reportId);
        Assert.assertEquals(2, evidence.size());
    }
}
