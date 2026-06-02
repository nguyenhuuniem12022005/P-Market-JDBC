package test.unit;

import dao.CategoryDAO;
import dao.ImageDAO;
import model.Category;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
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

    @Test
    public void testDeleteCategoryWithChildReparentsChild() throws Exception {
        String parentName = DbTestUtil.unique("JUnit parent category");
        Category parent = new Category();
        parent.setName(parentName);
        parent.setStatus("ACTIVE");
        parent = categoryDAO.addCategory(parent);

        Category child = new Category();
        child.setName(DbTestUtil.unique("JUnit child category"));
        child.setStatus("ACTIVE");
        child.setParent(parent);
        child = categoryDAO.addCategory(child);

        Assert.assertEquals(1, categoryDAO.countChildCategories(parent.getId()));
        Assert.assertTrue(categoryDAO.deleteCategory(parent.getId()));

        Category childDetail = categoryDAO.getCategoryDetail(child.getId());
        Assert.assertNotNull(childDetail);
        Assert.assertNull(childDetail.getParent());

        Assert.assertTrue(categoryDAO.deleteCategory(child.getId()));
    }

    @Test
    public void testUpdateCategoryRejectsDescendantParent() throws Exception {
        Category parent = new Category();
        parent.setName(DbTestUtil.unique("JUnit cycle parent"));
        parent.setStatus("ACTIVE");
        parent = categoryDAO.addCategory(parent);

        Category child = new Category();
        child.setName(DbTestUtil.unique("JUnit cycle child"));
        child.setStatus("ACTIVE");
        child.setParent(parent);
        child = categoryDAO.addCategory(child);

        parent.setParent(child);
        try {
            categoryDAO.updateCategory(parent);
            Assert.fail("Expected category parent cycle to be rejected");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Danh muc cha khong hop le"));
        }

        Assert.assertTrue(categoryDAO.deleteCategory(parent.getId()));
        Assert.assertTrue(categoryDAO.deleteCategory(child.getId()));
    }

    // getAllCategoties()
    @Test
    public void testGetAllCategoriesFound() throws Exception {

        List<Category> categories =
                new CategoryDAO().getAllCategories();

        Assert.assertNotNull(categories);
        Assert.assertTrue(categories.size() > 0);
    }

    @Test
public void testGetAllCategoriesEmpty() throws Exception {

    List<Category> categories =
            new ArrayList<>();

    Assert.assertEquals(0, categories.size());
}




}
