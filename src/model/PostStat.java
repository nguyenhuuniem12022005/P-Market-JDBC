package model;

import java.time.LocalDate;

public class PostStat {
    private LocalDate startDate;
    private LocalDate endDate;
    private int newPosts;
    private int deletedPosts;
    private int totalPosts;

    public PostStat() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getNewPosts() {
        return newPosts;
    }

    public void setNewPosts(int newPosts) {
        this.newPosts = newPosts;
    }

    public int getDeletedPosts() {
        return deletedPosts;
    }

    public void setDeletedPosts(int deletedPosts) {
        this.deletedPosts = deletedPosts;
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }
}
