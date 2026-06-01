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

    public ReportDAO() {
        ensureDetailColumn();
    }

    /** Module g: luu bao cao vao CSDL, tra ve reportId vua tao */
    public int addReport(Report report) throws SQLException {
        validateNewReport(report);
        if (hasPendingDuplicate(report.getReporterId(), report.getPostId(), report.getAccountId())) {
            throw new SQLException("Ban da gui bao cao cho doi tuong nay va dang cho xu ly.");
        }
        String sql = """
                INSERT INTO tblReport (reporterId, postId, accountId, reason, detail, status)
                VALUES (?, ?, ?, ?, ?, 'PENDING')
                """;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, report.getReporterId());
            setNullableInt(ps, 2, report.getPostId());
            setNullableInt(ps, 3, report.getAccountId());
            ps.setString(4, report.getReason());
            ps.setString(5, report.getDetail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getInt(1));
                }
            }
        }
        report.setStatus(Report.STATUS_PENDING);
        return report.getId();
    }

    /** Alias theo ten trong mot so so do tuan tu. */
    public int createReport(Report report) throws SQLException {
        return addReport(report);
    }

    /** Module h: danh sach cho xu ly */
    public List<Report> getPendingReports() throws SQLException {
        return getReportsByStatus(Report.STATUS_PENDING);
    }

    public List<Report> getAllReports() throws SQLException {
        String sql = baseReportQuery() + " ORDER BY r.createdAt DESC";
        List<Report> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Report> getReportsByStatus(String status) throws SQLException {
        String normalizedStatus = normalizeStatus(status);
        String baseSql = baseReportQuery();
        String sql = Report.STATUS_RESOLVED.equals(normalizedStatus)
                ? baseSql + " WHERE r.status IN (?, ?) ORDER BY r.createdAt DESC"
                : baseSql + " WHERE r.status=? ORDER BY r.createdAt DESC";
        List<Report> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalizedStatus);
            if (Report.STATUS_RESOLVED.equals(normalizedStatus)) {
                ps.setString(2, "PROCESSED");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public Report getReportById(int id) throws SQLException {
        String sql = baseReportQuery() + " WHERE r.id=?";
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
        String currentStatus = getReportStatus(reportId);
        if (currentStatus == null) {
            return false;
        }
        String normalizedStatus = normalizeStatus(status);
        if (!canTransition(currentStatus, normalizedStatus)) {
            throw new SQLException("Khong the chuyen trang thai bao cao tu "
                    + currentStatus + " sang " + normalizedStatus + ".");
        }
        String sql = "UPDATE tblReport SET status=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalizedStatus);
            ps.setInt(2, reportId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean hasPendingDuplicate(int reporterId, Integer postId, Integer accountId) throws SQLException {
        if (postId == null && accountId == null) {
            return false;
        }
        String sql;
        if (postId != null) {
            sql = """
                    SELECT COUNT(*)
                    FROM tblReport
                    WHERE reporterId=? AND postId=? AND status=?
                    """;
        } else {
            sql = """
                    SELECT COUNT(*)
                    FROM tblReport
                    WHERE reporterId=? AND accountId=? AND status=?
                    """;
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reporterId);
            ps.setInt(2, postId != null ? postId : accountId);
            ps.setString(3, Report.STATUS_PENDING);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void validateNewReport(Report report) throws SQLException {
        if (report == null) {
            throw new SQLException("Bao cao khong hop le.");
        }
        boolean hasPost = report.getPostId() != null;
        boolean hasAccount = report.getAccountId() != null;
        if (hasPost == hasAccount) {
            throw new SQLException("Bao cao phai gan voi dung mot bai dang hoac mot tai khoan.");
        }
        if (report.getReporterId() <= 0) {
            throw new SQLException("Nguoi gui bao cao khong hop le.");
        }
        if (report.getReason() == null || report.getReason().isBlank()) {
            throw new SQLException("Ly do bao cao khong duoc rong.");
        }
    }

    private String getReportStatus(int reportId) throws SQLException {
        String sql = "SELECT status FROM tblReport WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    private void ensureDetailColumn() {
        String sql = "ALTER TABLE IF EXISTS tblReport ADD COLUMN IF NOT EXISTS detail CLOB";
        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("Khong cap nhat duoc cot detail cho tblReport: " + ex.getMessage(), ex);
        }
    }

    private String normalizeStatus(String status) throws SQLException {
        if (status == null || status.isBlank()) {
            throw new SQLException("Trang thai bao cao khong hop le.");
        }
        String normalized = status.trim().toUpperCase();
        if ("PROCESSED".equals(normalized)) {
            return Report.STATUS_RESOLVED;
        }
        return switch (normalized) {
            case Report.STATUS_PENDING, Report.STATUS_RESOLVED, Report.STATUS_REJECTED -> normalized;
            default -> throw new SQLException("Trang thai bao cao khong hop le: " + status);
        };
    }

    private boolean canTransition(String currentStatus, String newStatus) throws SQLException {
        String current = normalizeStatus(currentStatus);
        if (current.equals(newStatus)) {
            return true;
        }
        return Report.STATUS_PENDING.equals(current)
            && (Report.STATUS_RESOLVED.equals(newStatus)
            || Report.STATUS_REJECTED.equals(newStatus));
    }

    private String baseReportQuery() {
        return """
                SELECT r.*,
                       reporter.fullName AS reporterName,
                       reporter.email AS reporterEmail,
                       reported.fullName AS reportedName,
                       reported.email AS reportedEmail
                FROM tblReport r
                JOIN tblAccount reporter ON r.reporterId = reporter.id
                LEFT JOIN tblAccount reported ON r.accountId = reported.id
                """;
    }

    private Report mapRow(ResultSet rs) throws SQLException {
        Report r = new Report();
        r.setId(rs.getInt("id"));
        r.setReporterId(rs.getInt("reporterId"));
        r.setPostId(getNullableInt(rs, "postId"));
        r.setAccountId(getNullableInt(rs, "accountId"));
        r.setReason(rs.getString("reason"));
        r.setDetail(rs.getString("detail"));
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
