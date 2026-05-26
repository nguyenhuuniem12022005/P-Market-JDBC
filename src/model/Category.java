package model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int id;
    private String name;
    private Category parent;
    private List<Category> listChild = new ArrayList<>();
    private List<Post> listPost = new ArrayList<>();

    public Category() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }
    public List<Category> getListChild() { return listChild; }
    public void setListChild(List<Category> listChild) { this.listChild = listChild; }
    public List<Post> getListPost() { return listPost; }
    public void setListPost(List<Post> listPost) { this.listPost = listPost; }

    @Override
    public String toString() {
        return name;
    }
}
