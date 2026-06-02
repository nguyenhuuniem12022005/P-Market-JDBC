package test.unit;

import dao.PostDAO;
import model.Post;
import org.junit.Assert;
import org.junit.Test;

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
        Post post = DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("JUnit create post"));

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
        Post post = DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("JUnit no image post"));
        post.getListImage().clear();

        try {
            postDAO.createPost(post);
            Assert.fail("Expected createPost to reject a post without images");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("anh"));
        }
    }

    @Test
    public void testUpdatePostExists() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        Post post = postDAO.createPost(DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("JUnit update post")));

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
                    "JUnit Update Fail");

    post.setId(-1); // id không tồn tại

    boolean result =
            postDAO.updatePost(post);

    Assert.assertFalse(result);
}

    @Test
    public void testDeletePost() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int categoryId = DbTestUtil.firstCategoryId();
        Post post = postDAO.createPost(DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("JUnit delete post")));

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
        Post post = postDAO.createPost(DbTestUtil.postFixture(accountId, categoryId, DbTestUtil.unique("JUnit lifecycle post")));

        Assert.assertEquals(Post.STATUS_ACTIVE, postDAO.getPostById(post.getId()).getStatus());
        Assert.assertTrue(postDAO.deletePost(post.getId()));
        Assert.assertEquals(Post.STATUS_DELETED, postDAO.getPostById(post.getId()).getStatus());

        try {
            postDAO.updateStatus(post.getId(), Post.STATUS_ACTIVE);
            Assert.fail("Expected deleted posts to be terminal");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Khong the chuyen trang thai"));
        }
    }

    @Test
    public void testCountAndTransferPostByCategory() throws Exception {
        int accountId = DbTestUtil.firstActiveStudentId();
        int fromCategoryId = DbTestUtil.insertCategory(DbTestUtil.unique("JUnit from category"), null);
        int toCategoryId = DbTestUtil.insertCategory(DbTestUtil.unique("JUnit to category"), null);
        postDAO.createPost(DbTestUtil.postFixture(accountId, fromCategoryId, DbTestUtil.unique("JUnit transfer post")));

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
            DbTestUtil.unique("Account Posts")));

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
                            DbTestUtil.unique("Detail Post")));

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
