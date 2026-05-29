package dao;

import model.Image;
import model.Post;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    /**
     * Module c: tai anh len he thong luu tru (Cloudinary/S3 mo phong bang thu muc uploads),
     * tra ve danh sach URL anh.
     */
    public List<String> uploadImages(List<String> sourcePaths) throws SQLException {
        List<String> urls = new ArrayList<>();
        if (sourcePaths == null) {
            return urls;
        }
        try {
            Path uploads = Paths.get("uploads");
            Files.createDirectories(uploads);
            for (String src : sourcePaths) {
                if (src == null || src.isBlank()) {
                    continue;
                }
                Path source = Paths.get(src);
                if (Files.exists(source)) {
                    String name = System.currentTimeMillis() + "_" + new File(src).getName();
                    Path dest = uploads.resolve(name);
                    Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                    urls.add("uploads/" + name);
                } else {
                    urls.add(src);
                }
            }
        } catch (Exception ex) {
            throw new SQLException("Khong tai duoc anh: " + ex.getMessage(), ex);
        }
        return urls;
    }

    /** Module c: luu URL anh lien ket voi bai dang */
    public boolean saveImages(int postId, List<String> imageUrls) throws SQLException {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return true;
        }
        String sql = "INSERT INTO tblImage (postId, imageUrl) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String url : imageUrls) {
                ps.setInt(1, postId);
                ps.setString(2, url);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return true;
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
