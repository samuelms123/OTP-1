package application.model.entity;

import jakarta.persistence.*;

/**
 * Entity class representing an entry in the "comments" table.
 * This class maps comment data stored in the database.
 * @Column annotations are used to map the fields to the corresponding columns in the database.
 * @Entity annotation is used to mark this class as an entity class.
 * @Table annotation is used to specify the name of the table in the database.
 * @Id annotation is used to mark the id field as the primary key.
 * @GeneratedValue annotation is used to specify the generation strategy for the primary key.
 */

@Entity
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(name="user_id")
    private int userId;
    @Column(name="post_id")
    private int post_id;
    @Column(name="content")
    private String content;

    // parameterless constructor for Hibernate
    public Comment() {}

    public Comment(int userId, int post_id, String content) {
        this.userId = userId;
        this.post_id = post_id;
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }
    public int getPostId() {
        return post_id;
    }
    public String getContent() {
        return content;
    }

    // For CommentDao tests
    public void setId(int id) {
        this.id = id;
    }
}
