package dao;

import model.Account;
import model.Post;
import model.Report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO extends DAO {

    private final PostDAO postDAO = new PostDAO();

    /** Module g: luu bao cao vao CSDL, tra ve reportId vua tao */
    public int addReport(Report report) throws SQLException {
        String sql = """
                INSERT INTO tblReport (reporterId, postId, accountId, reason, status)
                VALUES (?, ?, ?, ?, 'PENDING')
                """;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, report.getReporterId());
            setNullableInt(ps, 2, report.getPostId());
            setNullableInt(ps, 3, report.getAccountId());
            ps.setString(4, report.getReason());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getInt(1));
                }
            }
        }
        report.setStatus("PENDING");
        return report.getId();
    }

    /** Alias theo ten trong mot so so do tuan tu. */
    public int createReport(Report report) throws SQLException {
        return addReport(report);
    }

    /** Module h: danh sach cho xu ly */
    public List<Report> getPendingReports() throws SQLException {
        return getReportsByStatus("PENDING");
    }

    public List<Report> getReportsByStatus(String status) throws SQLException {
        String sql = """
                SELECT r.*,
                       reporter.fullName AS reporterName,
                       reporter.email AS reporterEmail,
                       reported.fullName AS reportedName,
                       reported.email AS reportedEmail
                FROM tblReport r
                JOIN tblAccount reporter ON r.reporterId = reporter.id
                LEFT JOIN tblAccount reported ON r.accountId = reported.id
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
                SELECT r.*,
                       reporter.fullName AS reporterName,
                       reporter.email AS reporterEmail,
                       reported.fullName AS reportedName,
                       reported.email AS reportedEmail
                FROM tblReport r
                JOIN tblAccount reporter ON r.reporterId = reporter.id
                LEFT JOIN tblAccount reported ON r.accountId = reported.id
                WHERE r.id=?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Report r = mapRow(rs);
                    if (r.getPostId() != null) {
                        r.setPost(postDAO.getPostById(r.getPostId()));
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
        r.setReporterId(rs.getInt("reporterId"));
        r.setPostId(getNullableInt(rs, "postId"));
        r.setAccountId(getNullableInt(rs, "accountId"));
        r.setReason(rs.getString("reason"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));

        Account reporter = new Account();
        reporter.setId(rs.getInt("reporterId"));
        try {
            reporter.setFullName(rs.getString("reporterName"));
            reporter.setEmail(rs.getString("reporterEmail"));
        } catch (SQLException ignored) {
        }
        r.setReporter(reporter);

        if (r.getAccountId() != null) {
            Account account = new Account();
            account.setId(r.getAccountId());
            try {
                account.setFullName(rs.getString("reportedName"));
                account.setEmail(rs.getString("reportedEmail"));
            } catch (SQLException ignored) {
            }
            r.setAccount(account);
        }
        return r;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, java.sql.Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
