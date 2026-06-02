package test.unit;

import dao.PostDAO;
import model.Post;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

public class PostDaoTest {

    private final PostDAO postDAO = new PostDAO();

    @Test
    public void testSearchPostFound() throws Exception {
        List<Post> posts = postDAO.searchPosts("Laptop", null);
        Assert.assertNotNull(posts);
        Assert.assertTrue(posts.size() > 0);
        for (Post p : posts) {
            String text = (p.getTitle() + " " + p.getDescription()).toLowerCase();
            Assert.assertTrue(text.contains("laptop"));
        }
    }

    @Test
    public void testSearchPostNotFound() throws Exception {
        List<Post> posts = postDAO.searchPosts("xxxxxxxxxx_junit", null);
        Assert.assertNotNull(posts);
        Assert.assertEquals(0, posts.size());
    }

    @Test
    public void testCreatePost() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        Post post = DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("Kiểm thử tạo bài đăng"));

        Post saved = postDAO.createPost(post);
        Assert.assertTrue(saved.getId() > 0);
        Assert.assertEquals(Post.STATUS_ACTIVE, saved.getStatus());

        Post detail = postDAO.getPostById(saved.getId());
        Assert.assertNotNull(detail);
        Assert.assertEquals(saved.getTitle(), detail.getTitle());
        Assert.assertFalse(detail.getListImage().isEmpty());
    }

    @Test
    public void testCreatePostRequiresImage() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        Post post = DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("Kiểm thử thiếu ảnh bài đăng"));

        post.getListImage().clear();

        try {
            postDAO.createPost(post);
            Assert.fail("Kỳ vọng hệ thống phải từ chối bài đăng không có ảnh và ném ra lỗi");
        } catch (SQLException ex) {
            Assert.assertNotNull("Ngoại lệ ném ra bị thiếu message", ex.getMessage());
            Assert.assertTrue("Thông báo lỗi chưa chính xác. Thực tế nhận được: " + ex.getMessage(),
                    ex.getMessage().contains("Bài đăng phải có ít nhất một ảnh"));
        }
    }

    @Test
    public void testUpdatePostExists() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        Post post = postDAO.createPost(DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("Kiểm thử cập nhật bài đăng")));

        post.setTitle(post.getTitle() + " updated");
        post.setPrice(456000);
        Assert.assertTrue(postDAO.updatePost(post));

        Post updated = postDAO.getPostById(post.getId());
        Assert.assertEquals(post.getTitle(), updated.getTitle());
        Assert.assertEquals(456000, updated.getPrice(), 0.001);
    }

@Test
public void testUpdatePostFail() throws Exception {

    int accountId = DbTestUtil.firstActiveStudentId();
    int categoryId = DbTestUtil.firstCategoryId();

    Post post =
            DbTestUtil.postFixture(
                    accountId,
                    categoryId,
                    "Kiểm thử cập nhật lỗi");

    post.setId(-1); // id không tồn tại

    boolean result =
            postDAO.updatePost(post);

    Assert.assertFalse(result);
}

    @Test
    public void testDeletePost() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        Post post = postDAO.createPost(DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("Kiểm thử xóa bài đăng")));

        Assert.assertTrue(postDAO.deletePost(post.getId()));
        Assert.assertNull(postDAO.findActivePostById(post.getId()));
        Assert.assertEquals(Post.STATUS_DELETED, postDAO.getPostById(post.getId()).getStatus());
    }

    @Test
    public void testDeletePostNotFound() throws Exception {
        int fakePostId = -999;
        boolean isDeleted = postDAO.deletePost(fakePostId);
        Assert.assertFalse("Kỳ vọng trả về false khi xóa bài đăng không tồn tại", isDeleted);
    }

    @Test
    public void testPostStatusLifecycle() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        Post post = postDAO.createPost(DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("Kiểm thử vòng đời bài đăng")));

        Assert.assertEquals(Post.STATUS_ACTIVE, postDAO.getPostById(post.getId()).getStatus());
        Assert.assertTrue(postDAO.deletePost(post.getId()));
        Assert.assertEquals(Post.STATUS_DELETED, postDAO.getPostById(post.getId()).getStatus());

        try {
            postDAO.updateStatus(post.getId(), Post.STATUS_ACTIVE);
            Assert.fail("Kỳ vọng bài đăng đã xóa không được đổi trạng thái");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Không thể chuyển trạng thái"));
        }
    }

    @Test
    public void testCountAndTransferPostByCategory() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int fromCategoryId = DbTestUtil.insertCategory(DbTestUtil.unique("Kiểm thử danh mục nguồn"), null);
        int toCategoryId = DbTestUtil.insertCategory(DbTestUtil.unique("Kiểm thử danh mục đích"), null);
        postDAO.createPost(DbTestUtil.postFixture(accountId, fromCategoryId, DbTestUtil.unique("Kiểm thử chuyển danh mục bài đăng")));

        Assert.assertEquals(1, postDAO.countPostByCategory(fromCategoryId));
        Assert.assertTrue(postDAO.transferPostsToCategory(fromCategoryId, toCategoryId));
        Assert.assertEquals(0, postDAO.countPostByCategory(fromCategoryId));
        Assert.assertEquals(1, postDAO.countPostByCategory(toCategoryId));
    }

    // getPosstByAccount
    @Test
public void testGetPostsByAccountFound() throws Exception {

    int accountId = DbTestUtil.firstActiveStudentId();
    int categoryId = DbTestUtil.firstCategoryId();

    postDAO.createPost(
        DbTestUtil.postFixture(
            accountId,
            categoryId,
            DbTestUtil.unique("Bài đăng tài khoản")));

    List<Post> posts =
            postDAO.getPostsByAccount(accountId);

    Assert.assertNotNull(posts);
    Assert.assertTrue(posts.size() > 0);
}

@Test
public void testGetPostsByAccountNotFound() throws Exception {

    String token = DbTestUtil.unique("student_no_post");
    int accountId = DbTestUtil.insertStudent(token);

    List<Post> posts =
            postDAO.getPostsByAccount(accountId);

    Assert.assertNotNull(posts);
    Assert.assertEquals(0, posts.size());
}

    // getPostById
    @Test
public void testGetPostDetailsFound() throws Exception {

    int accountId = DbTestUtil.firstActiveStudentId();
    int categoryId = DbTestUtil.firstCategoryId();

    Post created =
            postDAO.createPost(
                    DbTestUtil.postFixture(
                            accountId,
                            categoryId,
                            DbTestUtil.unique("Chi tiết bài đăng")));

    Post result =
            postDAO.getPostById(
                    created.getId());

    Assert.assertNotNull(result);
    Assert.assertEquals(
            created.getId(),
            result.getId());
}
@Test
public void testGetPostDetailsNotFound()
        throws Exception {

    Post result =
            postDAO.getPostById(
                    -1);

    Assert.assertNull(result);
}



}
