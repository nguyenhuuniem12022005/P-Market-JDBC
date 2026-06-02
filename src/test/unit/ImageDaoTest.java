package test.unit;

import dao.ImageDAO;
import dao.PostDAO;
import model.Image;
import model.Post;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImageDaoTest {

    private final ImageDAO imageDAO = new ImageDAO();


    @Test
    public void testSaveAndGetImagesByPostId() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(accountId, categoryId, DbTestUtil.unique("image_post"), "ACTIVE");

        Assert.assertTrue(imageDAO.saveImages(postId, List.of("uploads/junit_1.png", "uploads/junit_2.png")));
        List<Image> images = imageDAO.getImagesByPostId(postId);
        Assert.assertEquals(2, images.size());
        Assert.assertTrue(images.stream().anyMatch(img -> "uploads/junit_1.png".equals(img.getImageUrl())));
        Assert.assertTrue(images.stream().anyMatch(img -> "uploads/junit_2.png".equals(img.getImageUrl())));
    }

    // uploadImages()
@Test
public void testUploadImagesSuccess() throws Exception {

    File file = File.createTempFile("junit", ".jpg");

    List<String> paths = new ArrayList<>();
    paths.add(file.getAbsolutePath());

    ImageDAO dao = new ImageDAO();

    List<String> result = dao.uploadImages(paths);

    Assert.assertNotNull(result);
    Assert.assertEquals(1, result.size());

    file.delete();
}
@Test
public void testUploadImagesFail() throws Exception {

    ImageDAO dao = new ImageDAO();

    List<String> paths = new ArrayList<>();
    paths.add("uploads/not_exists_junit.png");

    try {
        dao.uploadImages(paths);
        Assert.fail();
    } catch (SQLException ex) {
        Assert.assertTrue(
                ex.getMessage().contains("Khong tim thay file anh"));
    }
}

// saveImages() 
@Test
public void testSaveImagesSuccess()
        throws Exception {

    int accountId =
            DbTestUtil.firstActiveStudentId();

    int categoryId =
            DbTestUtil.firstCategoryId();

    PostDAO postDAO = new PostDAO();

    Post post =
            postDAO.createPost(
                    DbTestUtil.postFixture(
                            accountId,
                            categoryId,
                            DbTestUtil.unique("Save Image")));

    List<String> urls = new ArrayList<>();
    urls.add("uploads/test1.jpg");
    urls.add("uploads/test2.jpg");

    ImageDAO dao = new ImageDAO();

    boolean result =
            dao.saveImages(
                    post.getId(),
                    urls);

    Assert.assertTrue(result);
}

@Test
public void testSaveImagesEmptyList()
        throws Exception {

    ImageDAO dao = new ImageDAO();

    boolean result =
            dao.saveImages(
                    1,
                    new ArrayList<>());

    Assert.assertFalse(result);
}
}
