package dao;

import model.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DAO {

    public CategoryDAO() {
        ensureDescriptionColumn();
    }

    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT * FROM tblCategory WHERE status='ACTIVE' ORDER BY name";
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Module d: lay danh sach danh muc (alias getAllCategories) */
    public List<Category> getCategories() throws SQLException {
        return getAllCategories();
    }

    /** Module d: lay chi tiet mot danh muc */
    public Category getCategoryDetail(int id) throws SQLException {
        String sql = "SELECT * FROM tblCategory WHERE id=?";
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

    /** Module d: kiem tra ten danh muc da ton tai */
    public boolean existsByName(String name) throws SQLException {
        return existsByName(name, -1);
    }

    /** Module d: kiem tra trung ten (bo qua chinh danh muc dang sua) */
    public boolean existsByName(String name, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tblCategory WHERE LOWER(name)=LOWER(?) AND id<>? AND status='ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name == null ? "" : name.trim());
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /** Module d: them danh muc moi */
    public Category addCategory(Category category) throws SQLException {
        validateCategory(category, -1);
        String sql = "INSERT INTO tblCategory (parentId, name, description, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParentId(ps, 1, category);
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());
            ps.setString(4, normalizeStatus(category.getStatus()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setId(keys.getInt(1));
                }
            }
        }
        return category;
    }

    /** Module d: cap nhat danh muc */
    public boolean updateCategory(Category category) throws SQLException {
        validateCategory(category, category != null ? category.getId() : -1);
        String sql = "UPDATE tblCategory SET name=?, description=?, parentId=?, status=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            if (category.getParent() != null && category.getParent().getId() > 0) {
                ps.setInt(3, category.getParent().getId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, normalizeStatus(category.getStatus()));
            ps.setInt(5, category.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Module d: xoa danh muc */
    public boolean deleteCategory(int id) throws SQLException {
        Category deleting = getCategoryDetail(id);
        if (deleting == null) {
            return false;
        }

        boolean oldAutoCommit = con.getAutoCommit();
        try {
            con.setAutoCommit(false);
            reparentChildren(id, deleting.getParent());
            String sql = "DELETE FROM tblCategory WHERE id=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                boolean deleted = ps.executeUpdate() > 0;
                con.commit();
                return deleted;
            }
        } catch (SQLException ex) {
            con.rollback();
            throw ex;
        } finally {
            con.setAutoCommit(oldAutoCommit);
        }
    }

    public int countChildCategories(int parentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tblCategory WHERE parentId=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean isDescendant(int categoryId, int possibleDescendantId) throws SQLException {
        Integer currentParentId = parentIdOf(possibleDescendantId);
        while (currentParentId != null) {
            if (currentParentId == categoryId) {
                return true;
            }
            currentParentId = parentIdOf(currentParentId);
        }
        return false;
    }

    private void setParentId(PreparedStatement ps, int idx, Category category) throws SQLException {
        if (category.getParent() != null && category.getParent().getId() > 0) {
            ps.setInt(idx, category.getParent().getId());
        } else {
            ps.setNull(idx, java.sql.Types.INTEGER);
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        int parentId = rs.getInt("parentId");
        if (!rs.wasNull()) {
            Category parent = new Category();
            parent.setId(parentId);
            c.setParent(parent);
        }
        return c;
    }

    private void ensureDescriptionColumn() {
        String sql = "ALTER TABLE IF EXISTS tblCategory ADD COLUMN IF NOT EXISTS description CLOB";
        try (Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("Khong cap nhat duoc cot description cho tblCategory: "
                    + ex.getMessage(), ex);
        }
    }

    private String normalizeStatus(String status) {
        return status == null || status.isBlank() ? "ACTIVE" : status;
    }

    private void validateCategory(Category category, int excludeId) throws SQLException {
        if (category == null) {
            throw new SQLException("Danh muc khong hop le.");
        }
        String name = category.getName();
        if (name == null || name.trim().isEmpty()) {
            throw new SQLException("Ten danh muc khong duoc rong.");
        }
        category.setName(name.trim());
        if (existsByName(category.getName(), excludeId)) {
            throw new SQLException("Ten danh muc da ton tai.");
        }
        Category parent = category.getParent();
        if (parent != null && parent.getId() > 0 && excludeId > 0) {
            if (parent.getId() == excludeId || isDescendant(excludeId, parent.getId())) {
                throw new SQLException("Danh muc cha khong hop le.");
            }
        }
    }

    private void reparentChildren(int deletingId, Category newParent) throws SQLException {
        String sql = "UPDATE tblCategory SET parentId=? WHERE parentId=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (newParent != null && newParent.getId() > 0) {
                ps.setInt(1, newParent.getId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setInt(2, deletingId);
            ps.executeUpdate();
        }
    }

    private Integer parentIdOf(int categoryId) throws SQLException {
        String sql = "SELECT parentId FROM tblCategory WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int parentId = rs.getInt("parentId");
                    return rs.wasNull() ? null : parentId;
                }
            }
        }
        return null;
    }
}
