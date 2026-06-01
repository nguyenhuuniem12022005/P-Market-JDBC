package test.unit;

import dao.DatabaseUtil;
import model.Account;
import model.Category;
import model.Image;
import model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DbTestUtil {

    private DbTestUtil() {
    }

    static String unique(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "_" + Math.abs((int) System.nanoTime());
    }

    static Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection();
    }

    static int insertStudent(String token) throws SQLException {
        String sql = """
                INSERT INTO tblAccount (fullName, email, password, phone, address, role, status, avatarUrl)
                VALUES (?, ?, 'student123', '0999999999', 'JUnit', 'member', 'ACTIVE', '')
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "JUnit " + token);
            ps.setString(2, token + "@stu.ptit.edu.vn");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    static int insertCategory(String name, Integer parentId) throws SQLException {
        String sql = "INSERT INTO tblCategory (parentId, name, status) VALUES (?, ?, 'ACTIVE')";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            if (parentId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, parentId);
            }
            ps.setString(2, name);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    static int insertPost(int accountId, int categoryId, String title, String status) throws SQLException {
        String sql = """
                INSERT INTO tblPost (accountId, categoryId, title, description, price, quantity, status)
                VALUES (?, ?, ?, ?, 100000, 1, ?)
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, accountId);
            ps.setInt(2, categoryId);
            ps.setString(3, title);
            ps.setString(4, "Mo ta test " + title);
            ps.setString(5, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    static int firstActiveStudentId() throws SQLException {
        return scalarInt("SELECT id FROM tblAccount WHERE role='member' AND status='ACTIVE' ORDER BY id LIMIT 1");
    }

    static int firstCategoryId() throws SQLException {
        return scalarInt("SELECT id FROM tblCategory WHERE status='ACTIVE' ORDER BY id LIMIT 1");
    }

    static int scalarInt(String sql) throws SQLException {
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    static Account accountRef(int id) {
        Account a = new Account();
        a.setId(id);
        return a;
    }

    static Category categoryRef(int id) {
        Category c = new Category();
        c.setId(id);
        return c;
    }

    static Post postFixture(int accountId, int categoryId, String title) {
        Post p = new Post();
        p.setAccount(accountRef(accountId));
        p.setCategory(categoryRef(categoryId));
        p.setTitle(title);
        p.setDescription("Mo ta test " + title);
        p.setPrice(123000);
        p.setQuantity(2);
        Image image = new Image();
        image.setImageUrl("uploads/junit_post.png");
        p.getListImage().add(image);
        return p;
    }
}
