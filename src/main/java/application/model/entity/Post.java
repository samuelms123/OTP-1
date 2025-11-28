package application.model.entity;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

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
public class Post implements Comparable<Post>{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="user_id")
    private int userId;
    @Column(name="content")
    private String content;
    @Column(name="image_url")
    private String imageUrl;
    @Column(name="created_at")
    private Timestamp createdAt;
    @Column(name="locale")
    private String locale;


    public Post(int userId, String content, String imageUrl, Timestamp createdAt, String locale) {
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.locale = locale;
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

    public Timestamp getCreatedAt() {return createdAt;}

    public String getLocale() {return locale;}

    // compare by timestamp
    @Override
    public int compareTo(@NotNull Post other) {
        // null check to prevent crash if no timestamp
        if (this.createdAt == null && other.createdAt == null) return 0;
        if (this.createdAt == null) return 1; // nulls last
        if (other.createdAt == null) return -1; // nulls last

        return other.createdAt.compareTo(this.createdAt);
    }

    // For PostDao tests
    public void setId(int i) {
        id = i;
    }

    public void setUserId(int i) {
        userId = i;
    }
}
