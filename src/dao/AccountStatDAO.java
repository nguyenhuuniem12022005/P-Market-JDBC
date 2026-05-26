package dao;

import model.AccountStat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AccountStatDAO extends DAO {

    /** Module i */
    public AccountStat getAccountStat(LocalDate startDate, LocalDate endDate) throws SQLException {
        AccountStat stat = new AccountStat();
        stat.setStartDate(startDate);
        stat.setEndDate(endDate);

        String newSql = """
                SELECT COUNT(*) FROM tblAccount
                WHERE userRole='student' AND createdAt >= ? AND createdAt < DATEADD('DAY', 1, ?)
                """;
        try (PreparedStatement ps = con.prepareStatement(newSql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stat.setNewAccounts(rs.getInt(1));
                }
            }
        }

        String bannedSql = "SELECT COUNT(*) FROM tblAccount WHERE userStatus='banned'";
        try (PreparedStatement ps = con.prepareStatement(bannedSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stat.setBannedAccounts(rs.getInt(1));
            }
        }

        String totalSql = "SELECT COUNT(*) FROM tblAccount WHERE userRole='student'";
        try (PreparedStatement ps = con.prepareStatement(totalSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stat.setTotalAccounts(rs.getInt(1));
            }
        }
        return stat;
    }

    public AccountStat exportReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        return getAccountStat(startDate, endDate);
    }
}
