package model;

public class Image {
    private int id;
    private String imageUrl;
    private Post post;

    public Image() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}
