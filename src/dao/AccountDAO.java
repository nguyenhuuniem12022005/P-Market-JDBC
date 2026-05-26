package dao;

import model.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO extends DAO {

    /** Module a: dang nhap */
    public Account login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM tblAccount WHERE LOWER(email)=LOWER(?) AND pwd=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /** Module b: tim kiem tai khoan */
    public List<Account> searchAccounts(String keyword) throws SQLException {
        String sql = """
                SELECT * FROM tblAccount
                WHERE userRole='student' AND (
                    LOWER(fullName) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)
                )
                ORDER BY id
                """;
        String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<Account> getAllStudents() throws SQLException {
        return searchAccounts("");
    }

    /** Module k */
    public List<Account> getAllStudentIds() throws SQLException {
        String sql = "SELECT * FROM tblAccount WHERE userRole='student' AND userStatus='active'";
        List<Account> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Account findById(int id) throws SQLException {
        String sql = "SELECT * FROM tblAccount WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /** Module b: khoa tai khoan */
    public boolean banAccount(int accountId, String banReason) throws SQLException {
        String sql = "UPDATE tblAccount SET userStatus='banned', banReason=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, banReason);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Module b: mo khoa */
    public boolean unbanAccount(int accountId) throws SQLException {
        String sql = "UPDATE tblAccount SET userStatus='active', banReason=NULL WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setId(rs.getInt("id"));
        a.setFullName(rs.getString("fullName"));
        a.setEmail(rs.getString("email"));
        a.setPassword(rs.getString("pwd"));
        a.setPhone(rs.getString("phone"));
        a.setAddress(rs.getString("address"));
        a.setRole(rs.getString("userRole"));
        a.setStatus(rs.getString("userStatus"));
        a.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));
        a.setAvatarUrl(rs.getString("avatarUrl"));
        a.setBanReason(rs.getString("banReason"));
        return a;
    }
}
