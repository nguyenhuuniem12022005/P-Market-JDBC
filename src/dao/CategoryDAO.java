package dao;

import model.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DAO {

    public List<Category> getAllCategories() throws SQLException {
        String sql = "SELECT * FROM tblCategory ORDER BY name";
        List<Category> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                int parentId = rs.getInt("parentId");
                if (!rs.wasNull()) {
                    Category parent = new Category();
                    parent.setId(parentId);
                    c.setParent(parent);
                }
                list.add(c);
            }
        }
        return list;
    }
}
