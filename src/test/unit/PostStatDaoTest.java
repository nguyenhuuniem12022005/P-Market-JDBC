package test.unit;

import dao.PostStatDAO;
import model.PostStat;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class PostStatDaoTest {

    @Test
    public void testGetPostStatHasData() throws Exception {
        PostStat stat = new PostStatDAO().getPostStat(LocalDate.now().minusYears(10), LocalDate.now());
        Assert.assertNotNull(stat);
        Assert.assertTrue(stat.getTotalPosts() > 0);
        Assert.assertTrue(stat.getNewPosts() >= 0);
        Assert.assertTrue(stat.getSoldPosts() >= 0);
    }

    @Test
    public void testExportReport() throws Exception {
        PostStat stat = new PostStatDAO().exportReport(LocalDate.now().minusYears(10), LocalDate.now());
        Assert.assertNotNull(stat);
        Assert.assertTrue(stat.getTotalPosts() > 0);
    }
}
