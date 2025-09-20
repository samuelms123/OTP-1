package application.model.entity;

import jakarta.persistence.*;

/**
 * Entity class representing an entry in the "posts" table.
 * This class maps post data stored in the database.
 * @Column annotations are used to map the fields to the corresponding columns in the database.
 * @Entity annotation is used to mark this class as an entity class.
 * @Table annotation is used to specify the name of the table in the database.
 * @Id annotation is used to mark the id field as the primary key.
 * @GeneratedValue annotation is used to specify the generation strategy for the primary key.
 */
@Entity
@Table(name="posts")
public class Post {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="user_id")
    private int userId;
    @Column(name="content")
    private String content;
    @Column(name="image_url")
    private String imageUrl;


    public Post(int userId, String content, String imageUrl) {
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    /**
     * Default constructor for hibernate to instantiate empty objects.
     */
    public Post() {
    }

    public int getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public int getId() {
        return id;
    }

    public String imageUrl() {
        return imageUrl;
    }
}
