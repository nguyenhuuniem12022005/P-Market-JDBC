package test.unit;

import dao.CategoryDAO;
import model.Category;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CategoryDaoTest {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Test
    public void testGetCategoriesHasData() throws Exception {
        List<Category> categories = categoryDAO.getCategories();
        Assert.assertNotNull(categories);
        Assert.assertTrue(categories.size() > 0);
        for (Category c : categories) {
            Assert.assertEquals("ACTIVE", c.getStatus());
        }
    }

    @Test
    public void testExistsByNameFound() throws Exception {
        Assert.assertTrue(categoryDAO.existsByName("Hoc tap"));
    }

    @Test
    public void testExistsByNameNotFound() throws Exception {
        Assert.assertFalse(categoryDAO.existsByName(DbTestUtil.unique("cat_not_found")));
    }

    @Test
    public void testAddUpdateDeleteCategory() throws Exception {
        String name = DbTestUtil.unique("JUnit category");
        Category c = new Category();
        c.setName(name);
        c.setDescription("JUnit category description");
        c.setStatus("ACTIVE");

        Category saved = categoryDAO.addCategory(c);
        Assert.assertTrue(saved.getId() > 0);
        Assert.assertTrue(categoryDAO.existsByName(name));

        saved.setName(name + " updated");
        saved.setDescription("Updated description");
        Assert.assertTrue(categoryDAO.updateCategory(saved));

        Category detail = categoryDAO.getCategoryDetail(saved.getId());
        Assert.assertEquals(name + " updated", detail.getName());
        Assert.assertEquals("Updated description", detail.getDescription());

        Assert.assertTrue(categoryDAO.deleteCategory(saved.getId()));
        Assert.assertFalse(categoryDAO.existsByName(name + " updated"));
    }
}
