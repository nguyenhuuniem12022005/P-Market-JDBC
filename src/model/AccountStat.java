package model;

import java.time.LocalDate;

public class AccountStat {
    private LocalDate startDate;
    private LocalDate endDate;
    private int newAccounts;
    private int bannedAccounts;
    private int totalAccounts;

    public AccountStat() {}

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getNewAccounts() { return newAccounts; }
    public void setNewAccounts(int newAccounts) { this.newAccounts = newAccounts; }
    public int getBannedAccounts() { return bannedAccounts; }
    public void setBannedAccounts(int bannedAccounts) { this.bannedAccounts = bannedAccounts; }
    public int getTotalAccounts() { return totalAccounts; }
    public void setTotalAccounts(int totalAccounts) { this.totalAccounts = totalAccounts; }
}
