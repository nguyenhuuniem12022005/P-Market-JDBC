package test.unit;

import dao.ImageDAO;
import model.Image;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ImageDaoTest {

    private final ImageDAO imageDAO = new ImageDAO();

    @Test
    public void testUploadImagesReturnsSourceWhenFileDoesNotExist() throws Exception {
        String path = "uploads/not_exists_junit.png";
        List<String> urls = imageDAO.uploadImages(List.of(path));
        Assert.assertEquals(1, urls.size());
        Assert.assertEquals(path, urls.get(0));
    }

    @Test
    public void testSaveAndGetImagesByPostId() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        int postId = DbTestUtil.insertPost(accountId, categoryId, DbTestUtil.unique("image_post"), "AVAILABLE");

        Assert.assertTrue(imageDAO.saveImages(postId, List.of("uploads/junit_1.png", "uploads/junit_2.png")));
        List<Image> images = imageDAO.getImagesByPostId(postId);
        Assert.assertEquals(2, images.size());
        Assert.assertTrue(images.stream().anyMatch(img -> "uploads/junit_1.png".equals(img.getImageUrl())));
        Assert.assertTrue(images.stream().anyMatch(img -> "uploads/junit_2.png".equals(img.getImageUrl())));
    }
}
