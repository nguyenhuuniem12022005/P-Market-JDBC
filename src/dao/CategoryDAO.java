package dao;

import model.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DAO {

    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT * FROM tblCategory ORDER BY name";
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
        String sql = "SELECT COUNT(*) FROM tblCategory WHERE LOWER(name)=LOWER(?) AND id<>?";
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
        String sql = "INSERT INTO tblCategory (parentId, name) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParentId(ps, 1, category);
            ps.setString(2, category.getName());
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
        String sql = "UPDATE tblCategory SET name=?, parentId=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category.getName());
            if (category.getParent() != null && category.getParent().getId() > 0) {
                ps.setInt(2, category.getParent().getId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, category.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Module d: xoa danh muc */
    public boolean deleteCategory(int id) throws SQLException {
        String sql = "DELETE FROM tblCategory WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
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
        int parentId = rs.getInt("parentId");
        if (!rs.wasNull()) {
            Category parent = new Category();
            parent.setId(parentId);
            c.setParent(parent);
        }
        return c;
    }
}
