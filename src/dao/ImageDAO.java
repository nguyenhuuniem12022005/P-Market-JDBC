package dao;

import model.Image;
import model.Post;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImageDAO extends DAO {

    public void insertImage(int postId, String imageUrl) throws SQLException {
        String sql = "INSERT INTO tblImage (postId, imageUrl) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setString(2, imageUrl);
            ps.executeUpdate();
        }
    }

    public List<Image> getImagesByPostId(int postId) throws SQLException {
        String sql = "SELECT * FROM tblImage WHERE postId=?";
        List<Image> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Image img = new Image();
                    img.setId(rs.getInt("id"));
                    img.setImageUrl(rs.getString("imageUrl"));
                    Post p = new Post();
                    p.setId(postId);
                    img.setPost(p);
                    list.add(img);
                }
            }
        }
        return list;
    }
}
