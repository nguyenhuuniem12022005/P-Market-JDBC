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

    /** Module c: dang bai */
    public Post createPost(Post post, List<String> imageUrls) throws SQLException {
        String sql = """
                INSERT INTO tblPost (accountId, categoryId, title, description, price, quantity, postStatus, updatedAt)
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
        if (imageUrls != null) {
            for (String url : imageUrls) {
                imageDAO.insertImage(post.getId(), url);
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
                WHERE p.postStatus IN ('available','sold')
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

    public List<Post> getPostsByAccount(int accountId) throws SQLException {
        return searchPosts("", null).stream()
                .filter(p -> p.getAccount() != null && p.getAccount().getId() == accountId)
                .toList();
    }

    /** Module h: xoa bai vi pham */
    public boolean hidePost(int postId) throws SQLException {
        String sql = "UPDATE tblPost SET postStatus='hidden', updatedAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean markSold(int postId) throws SQLException {
        String sql = "UPDATE tblPost SET postStatus='sold', updatedAt=CURRENT_TIMESTAMP WHERE id=?";
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
        p.setStatus(rs.getString("postStatus"));
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
