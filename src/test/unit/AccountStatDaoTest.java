package test.unit;

import dao.AccountStatDAO;
import model.AccountStat;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class AccountStatDaoTest {

    @Test
    public void testGetAccountStatHasData() throws Exception {
        AccountStat stat = new AccountStatDAO().getAccountStat(LocalDate.now().minusYears(10), LocalDate.now());
        Assert.assertNotNull(stat);
        Assert.assertTrue(stat.getTotalAccounts() > 0);
        Assert.assertTrue(stat.getNewAccounts() >= 0);
        Assert.assertTrue(stat.getBannedAccounts() >= 0);
    }

    @Test
    public void testExportReport() throws Exception {
        AccountStat stat = new AccountStatDAO().exportReport(LocalDate.now().minusYears(10), LocalDate.now());
        Assert.assertNotNull(stat);
        Assert.assertTrue(stat.getTotalAccounts() > 0);
    }
}
