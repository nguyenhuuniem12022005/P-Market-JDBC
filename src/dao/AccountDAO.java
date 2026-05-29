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
        String sql = "SELECT * FROM tblAccount WHERE LOWER(email)=LOWER(?) AND password=?";
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
                WHERE role='student' AND (
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
        String sql = "SELECT * FROM tblAccount WHERE role='student' AND status='active'";
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

    /** Module a: lay thong tin ho so ca nhan */
    public Account getProfile(int id) throws SQLException {
        return findById(id);
    }

    /** Module a: cap nhat ho so ca nhan (so dien thoai, dia chi, avatar) */
    public boolean updateProfile(Account account) throws SQLException {
        String sql = "UPDATE tblAccount SET phone=?, address=?, avatarUrl=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, account.getPhone());
            ps.setString(2, account.getAddress());
            ps.setString(3, account.getAvatarUrl());
            ps.setInt(4, account.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Module a: doi mat khau */
    public boolean updatePassword(int id, String newPassword) throws SQLException {
        String sql = "UPDATE tblAccount SET password=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Module c (chat): lay chi tiet tai khoan */
    public Account getAccountDetails(int id) throws SQLException {
        return findById(id);
    }

    /** Module g: kiem tra tai khoan con ton tai (con hoat dong) truoc khi tao bao cao */
    public Account findActiveAccountById(int id) throws SQLException {
        String sql = "SELECT * FROM tblAccount WHERE id=? AND status='active'";
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

    /** Module b: khoa tai khoan vi pham */
    public boolean lockAccount(int accountId, String banReason) throws SQLException {
        String sql = "UPDATE tblAccount SET status='banned', banReason=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, banReason);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Module b: mo khoa tai khoan */
    public boolean unlockAccount(int accountId) throws SQLException {
        String sql = "UPDATE tblAccount SET status='active', banReason=NULL WHERE id=?";
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
        a.setPassword(rs.getString("password"));
        a.setPhone(rs.getString("phone"));
        a.setAddress(rs.getString("address"));
        a.setRole(rs.getString("role"));
        a.setStatus(rs.getString("status"));
        a.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));
        a.setAvatarUrl(rs.getString("avatarUrl"));
        a.setBanReason(rs.getString("banReason"));
        return a;
    }
}
