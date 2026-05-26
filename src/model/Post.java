package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private int id;
    private String title;
    private String description;
    private double price;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private Account account;
    private Category category;
    private List<Image> listImage = new ArrayList<>();

    public Post() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<Image> getListImage() { return listImage; }
    public void setListImage(List<Image> listImage) { this.listImage = listImage; }
}
