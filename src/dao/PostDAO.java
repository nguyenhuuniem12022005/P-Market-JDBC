package dao;

import model.Account;
import model.Category;
import model.Image;
import model.Post;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class PostDAO extends DAO {

    private final ImageDAO imageDAO = new ImageDAO();

    /** Module c: dang bai, bat buoc co it nhat mot anh. */
    public Post createPost(Post post) throws SQLException {
        return createPost(post, imageUrlsFromPost(post));
    }

    public Post createPost(Post post, List<String> imageUrls) throws SQLException {
        List<String> requiredImages = requireImageUrls(imageUrls);
        boolean oldAutoCommit = con.getAutoCommit();
        try {
            con.setAutoCommit(false);
            insertPost(post);
            saveImagesForPost(post.getId(), requiredImages);
            con.commit();
            post.setStatus(Post.STATUS_ACTIVE);
            return post;
        } catch (Exception ex) {
            con.rollback();
            if (ex instanceof SQLException sqlEx) {
                throw sqlEx;
            }
            throw new SQLException("Không tạo được bài đăng: " + ex.getMessage(), ex);
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    private void insertPost(Post post) throws SQLException {
        String sql = """
                INSERT INTO tblPost (accountId, categoryId, title, description, price, quantity, status, updatedAt)
                VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE', CURRENT_TIMESTAMP)
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
    }

    /** Module d: tim kiem */
    public List<Post> searchPosts(String keyword, Integer categoryId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, a.fullName AS sellerName, a.email AS sellerEmail, c.name AS categoryName
            FROM tblPost p
            JOIN tblAccount a ON p.accountId = a.id
            JOIN tblCategory c ON p.categoryId = c.id
            WHERE p.status='ACTIVE'
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
    /** Lấy danh sách toàn bộ bài đăng (Không có tính năng tìm kiếm) */
    public List<Post> getAllPosts() throws SQLException {
        String sql = """
                SELECT p.*, a.fullName AS sellerName, a.email AS sellerEmail, c.name AS categoryName
                FROM tblPost p
                JOIN tblAccount a ON p.accountId = a.id
                JOIN tblCategory c ON p.categoryId = c.id
                WHERE p.status='ACTIVE'
                ORDER BY p.createdAt DESC
                """;

        List<Post> list = new ArrayList<>();

        // Vì không có tham số (dấu ?) nào cần truyền, ta gộp luôn khai báo ResultSet vào try-with-resources
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowWithJoin(rs));
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

    /** Module e: chi tiết bài đăng (alias getPostById) */
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
                WHERE p.accountId=? AND p.status='ACTIVE'
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

    /** Module g: kiểm tra bài đăng còn tồn tại trước khi tạo báo cáo */
    public Post findActivePostById(int id) throws SQLException {
        String sql = "SELECT id FROM tblPost WHERE id=? AND status=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, Post.STATUS_ACTIVE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getPostById(id);
                }
            }
        }
        return null;
    }

    /** Module b: cập nhật bài đăng */
    public boolean updatePost(Post post) throws SQLException {
        String sql = """
                UPDATE tblPost
                SET title=?, description=?, price=?, quantity=?, categoryId=?, updatedAt=CURRENT_TIMESTAMP
                WHERE id=? AND status='ACTIVE'
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

    /** Module b & h: xóa bài đăng (chuyển trạng thái sang đã xóa) */
    public boolean deletePost(int postId) throws SQLException {
        return updateStatus(postId, Post.STATUS_DELETED);
    }

    /** Module d (quản lý danh mục): đếm số bài đăng thuộc một danh mục */
    public int countPostByCategory(int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tblPost WHERE categoryId=?";
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

    /** Module d (quản lý danh mục): chuyển bài đăng sang danh mục khác khi xóa danh mục */
    public boolean transferPostsToCategory(int fromCategoryId, int toCategoryId) throws SQLException {
        String sql = "UPDATE tblPost SET categoryId=? WHERE categoryId=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, toCategoryId);
            ps.setInt(2, fromCategoryId);
            return ps.executeUpdate() >= 0;
        }
    }

    /** Alias theo ten trong kich ban quản lý danh mục. */
    public boolean transferPosts(int fromCategoryId, int toCategoryId) throws SQLException {
        return transferPostsToCategory(fromCategoryId, toCategoryId);
    }

    public boolean updateStatus(int postId, String newStatus) throws SQLException {
        String currentStatus = getPostStatus(postId);
        if (currentStatus == null) {
            return false;
        }
        String normalizedStatus = normalizeStatus(newStatus);
        if (!canTransition(currentStatus, normalizedStatus)) {
            throw new SQLException("Không thể chuyển trạng thái bài đăng từ "
                    + currentStatus + " sang " + normalizedStatus + ".");
        }
        String sql = "UPDATE tblPost SET status=?, updatedAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalizedStatus);
            ps.setInt(2, postId);
            return ps.executeUpdate() > 0;
        }
    }

    private String getPostStatus(int postId) throws SQLException {
        String sql = "SELECT status FROM tblPost WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    private String normalizeStatus(String status) throws SQLException {
        if (status == null || status.isBlank()) {
            throw new SQLException("Trạng thái bài đăng không hợp lệ.");
        }
        String normalized = status.trim().toUpperCase();
        return switch (normalized) {
            case Post.STATUS_ACTIVE, Post.STATUS_DELETED -> normalized;
            default -> throw new SQLException("Trạng thái bài đăng không hợp lệ: " + status);
        };
    }

    private boolean canTransition(String currentStatus, String newStatus) throws SQLException {
        String current = normalizeStatus(currentStatus);
        if (current.equals(newStatus)) {
            return true;
        }
        return switch (current) {
            case Post.STATUS_ACTIVE -> Post.STATUS_DELETED.equals(newStatus);
            case Post.STATUS_DELETED -> false;
            default -> false;
        };
    }

    private List<String> imageUrlsFromPost(Post post) {
        List<String> imageUrls = new ArrayList<>();
        if (post.getListImage() == null) {
            return imageUrls;
        }
        for (Image image : post.getListImage()) {
            if (image != null) {
                imageUrls.add(image.getImageUrl());
            }
        }
        return imageUrls;
    }

    private List<String> requireImageUrls(List<String> imageUrls) throws SQLException {
        List<String> cleaned = new ArrayList<>();
        if (imageUrls != null) {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isBlank()) {
                    cleaned.add(imageUrl.trim());
                }
            }
        }
        if (cleaned.isEmpty()) {
            throw new SQLException("Bài đăng phải có ít nhất một ảnh.");
        }
        return cleaned;
    }

    private void saveImagesForPost(int postId, List<String> imageUrls) throws SQLException {
        String sql = "INSERT INTO tblImage (postId, imageUrl) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String url : imageUrls) {
                ps.setInt(1, postId);
                ps.setString(2, url);
                ps.addBatch();
            }
            ps.executeBatch();
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
