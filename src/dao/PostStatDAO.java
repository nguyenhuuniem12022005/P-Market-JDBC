package dao;

import model.PostStat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PostStatDAO extends DAO {

    /** Module i */
    public PostStat getPostStat(LocalDate startDate, LocalDate endDate) throws SQLException {
        PostStat stat = new PostStat();
        stat.setStartDate(startDate);
        stat.setEndDate(endDate);

        String newSql = """
                SELECT COUNT(*) FROM tblPost
                WHERE createdAt >= ? AND createdAt < ?
                """;
        try (PreparedStatement ps = con.prepareStatement(newSql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate.plusDays(1)));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stat.setNewPosts(rs.getInt(1));
                }
            }
        }

        String deletedSql = "SELECT COUNT(*) FROM tblPost WHERE status='DELETED'";
        try (PreparedStatement ps = con.prepareStatement(deletedSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stat.setDeletedPosts(rs.getInt(1));
            }
        }

        String totalSql = "SELECT COUNT(*) FROM tblPost WHERE status='ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(totalSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stat.setTotalPosts(rs.getInt(1));
            }
        }
        return stat;
    }

    public PostStat exportReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        return getPostStat(startDate, endDate);
    }
}
