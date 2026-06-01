package dao;

import model.AccountStat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AccountStatDAO extends DAO {

    public AccountStat getAccountStat(LocalDate startDate, LocalDate endDate) throws SQLException {
        AccountStat stat = new AccountStat();
        stat.setStartDate(startDate);
        stat.setEndDate(endDate);

        String newSql = """
                SELECT COUNT(*) FROM tblAccount
                WHERE role='member' AND createdAt >= ? AND createdAt < ?
                """;
        try (PreparedStatement ps = con.prepareStatement(newSql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate.plusDays(1)));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stat.setNewAccounts(rs.getInt(1));
                }
            }
        }

        String bannedSql = "SELECT COUNT(*) FROM tblAccount WHERE status='BANNED'";
        try (PreparedStatement ps = con.prepareStatement(bannedSql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                stat.setBannedAccounts(rs.getInt(1));
            }
        }

        String totalSql = "SELECT COUNT(*) FROM tblAccount WHERE role='member'";
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
