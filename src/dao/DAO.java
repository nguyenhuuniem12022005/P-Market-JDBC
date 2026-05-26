package dao;

import dao.DatabaseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Lop DAO chung — khoi tao ket noi CSDL (theo giao trinh CNPM).
 */
public class DAO {

    protected Connection con;

    public DAO() {
        try {
            this.con = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Khong ket noi duoc CSDL: " + e.getMessage(), e);
        }
    }

    protected LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    protected Timestamp toTimestamp(LocalDateTime dt) {
        return dt == null ? null : Timestamp.valueOf(dt);
    }
}
