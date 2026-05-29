package dao;

import model.Account;
import model.Post;
import model.Report;
import model.ReportEvidence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO extends DAO {

    private final ReportEvidenceDAO evidenceDAO = new ReportEvidenceDAO();
    private final PostDAO postDAO = new PostDAO();

    /** Module g: luu bao cao vao CSDL, tra ve reportId vua tao */
    public int addReport(Report report) throws SQLException {
        String sql = """
                INSERT INTO tblReport (accountId, targetType, targetId, reason, status)
                VALUES (?, ?, ?, ?, 'pending')
                """;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, report.getAccount().getId());
            ps.setString(2, report.getTargetType());
            ps.setInt(3, report.getTargetId());
            ps.setString(4, report.getReason());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getInt(1));
                }
            }
        }
        report.setStatus("pending");
        return report.getId();
    }

    /** Module h: danh sach cho xu ly */
    public List<Report> getPendingReports() throws SQLException {
        return getReportsByStatus("pending");
    }

    public List<Report> getReportsByStatus(String status) throws SQLException {
        String sql = """
                SELECT r.*, a.fullName AS reporterName, a.email AS reporterEmail
                FROM tblReport r
                JOIN tblAccount a ON r.accountId = a.id
                WHERE r.status=?
                ORDER BY r.createdAt DESC
                """;
        List<Report> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Report getReportById(int id) throws SQLException {
        String sql = """
                SELECT r.*, a.fullName AS reporterName, a.email AS reporterEmail
                FROM tblReport r
                JOIN tblAccount a ON r.accountId = a.id
                WHERE r.id=?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Report r = mapRow(rs);
                    r.setListEvidence(evidenceDAO.getEvidenceByReportId(id));
                    if ("post".equalsIgnoreCase(r.getTargetType())) {
                        r.setPost(postDAO.getPostById(r.getTargetId()));
                    }
                    return r;
                }
            }
        }
        return null;
    }

    public boolean updateStatus(int reportId, String status) throws SQLException {
        String sql = "UPDATE tblReport SET status=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reportId);
            return ps.executeUpdate() > 0;
        }
    }

    private Report mapRow(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setId(rs.getInt("id"));
        r.setTargetType(rs.getString("targetType"));
        r.setTargetId(rs.getInt("targetId"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));
        Account reporter = new Account();
        reporter.setId(rs.getInt("accountId"));
        try {
            reporter.setFullName(rs.getString("reporterName"));
            reporter.setEmail(rs.getString("reporterEmail"));
        } catch (SQLException ignored) {
        }
        r.setAccount(reporter);
        return r;
    }
}
