package dao;

import model.Account;
import model.Category;
import model.Post;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostDAO extends DAO {

    private final ImageDAO imageDAO = new ImageDAO();

    /** Module c: dang bai (luu bai dang, anh duoc luu rieng qua ImageDAO.saveImages) */
    public Post createPost(Post post) throws SQLException {
        String sql = """
                INSERT INTO tblPost (accountId, categoryId, title, description, price, quantity, status, updatedAt)
                VALUES (?, ?, ?, ?, ?, ?, 'available', CURRENT_TIMESTAMP)
                """;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, post.getAccount().getId());
            ps.setInt(2, post.getCategory().getId());
            ps.setString(3, post.getTitle());
            ps.setString(4, post.getDescription());
            ps.setDouble(5, post.getPrice());
            ps.setInt(6, post.getQuantity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    post.setId(keys.getInt(1));
                }
            }
        }
        post.setStatus("available");
        return post;
    }

    /** Module d: tim kiem */
    public List<Post> searchPosts(String keyword, Integer categoryId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT p.*, a.fullName AS sellerName, a.email AS sellerEmail, c.name AS categoryName
                FROM tblPost p
                JOIN tblAccount a ON p.accountId = a.id
                JOIN tblCategory c ON p.categoryId = c.id
                WHERE p.status IN ('available','sold')
                """);
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(p.title) LIKE LOWER(?) OR LOWER(p.description) LIKE LOWER(?))");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.categoryId=?");
            params.add(categoryId);
        }
        sql.append(" ORDER BY p.createdAt DESC");

        List<Post> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) {
                    ps.setString(i + 1, s);
                } else {
                    ps.setInt(i + 1, (Integer) p);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowWithJoin(rs));
                }
            }
        }
        return list;
    }

    /** Module e: chi tiet */
    public Post getPostById(int id) throws SQLException {
        String sql = """
                SELECT p.*, a.fullName AS sellerName, a.email AS sellerEmail, a.id AS sellerId,
                       c.name AS categoryName, c.id AS catId
                FROM tblPost p
                JOIN tblAccount a ON p.accountId = a.id
                JOIN tblCategory c ON p.categoryId = c.id
                WHERE p.id=?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Post post = mapRowWithJoin(rs);
                    post.setListImage(imageDAO.getImagesByPostId(id));
                    return post;
                }
            }
        }
        return null;
    }

    /** Module e: chi tiet bai dang (alias getPostById) */
    public Post getPostDetails(int id) throws SQLException {
        return getPostById(id);
    }

    public List<Post> getPostsByAccount(int accountId) throws SQLException {
        String sql = """
                SELECT p.*, a.fullName AS sellerName, a.email AS sellerEmail, a.id AS sellerId,
                       c.name AS categoryName
                FROM tblPost p
                JOIN tblAccount a ON p.accountId = a.id
                JOIN tblCategory c ON p.categoryId = c.id
                WHERE p.accountId=? AND p.status != 'deleted'
                ORDER BY p.createdAt DESC
                """;
        List<Post> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post post = mapRowWithJoin(rs);
                    post.setListImage(imageDAO.getImagesByPostId(post.getId()));
                    list.add(post);
                }
            }
        }
        return list;
    }

    /** Module g: kiem tra bai dang con ton tai truoc khi tao bao cao */
    public Post findActivePostById(int id) throws SQLException {
        String sql = "SELECT id FROM tblPost WHERE id=? AND status != 'deleted'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getPostById(id);
                }
            }
        }
        return null;
    }

    /** Module b: cap nhat bai dang */
    public boolean updatePost(Post post) throws SQLException {
        String sql = """
                UPDATE tblPost
                SET title=?, description=?, price=?, quantity=?, categoryId=?, updatedAt=CURRENT_TIMESTAMP
                WHERE id=?
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setDouble(3, post.getPrice());
            ps.setInt(4, post.getQuantity());
            ps.setInt(5, post.getCategory().getId());
            ps.setInt(6, post.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Module b & h: xoa bai dang (chuyen trang thai sang da xoa) */
    public boolean deletePost(int postId) throws SQLException {
        String sql = "UPDATE tblPost SET status='deleted', updatedAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Module d (quan ly danh muc): dem so bai dang thuoc mot danh muc */
    public int countPostByCategory(int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tblPost WHERE categoryId=? AND status != 'deleted'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /** Module d (quan ly danh muc): chuyen bai dang sang danh muc khac khi xoa danh muc */
    public boolean transferPostsToCategory(int fromCategoryId, int toCategoryId) throws SQLException {
        String sql = "UPDATE tblPost SET categoryId=? WHERE categoryId=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, toCategoryId);
            ps.setInt(2, fromCategoryId);
            return ps.executeUpdate() >= 0;
        }
    }

    public boolean markSold(int postId) throws SQLException {
        String sql = "UPDATE tblPost SET status='sold', updatedAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate() > 0;
        }
    }

    private Post mapRowWithJoin(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setId(rs.getInt("id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(toLocalDateTime(rs.getTimestamp("createdAt")));
        p.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updatedAt")));

        Account seller = new Account();
        try {
            seller.setId(rs.getInt("sellerId"));
        } catch (SQLException ex) {
            seller.setId(rs.getInt("accountId"));
        }
        try {
            seller.setFullName(rs.getString("sellerName"));
            seller.setEmail(rs.getString("sellerEmail"));
        } catch (SQLException ignored) {
        }
        p.setAccount(seller);

        Category cat = new Category();
        cat.setId(rs.getInt("categoryId"));
        try {
            cat.setName(rs.getString("categoryName"));
        } catch (SQLException ignored) {
        }
        p.setCategory(cat);
        return p;
    }
}
