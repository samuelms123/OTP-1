package application.model.entity;

import jakarta.persistence.*;

/* Entity representing a "like" on a post.
 */
@Entity
@Table(name="likes")
public class Like {
    @EmbeddedId
    private LikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Constructors
    public Like() {}

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Embeddable
    public static class LikeId {
        private int userId;
        private int postId;

        // Constructors, getters, setters
        public LikeId() {}
        public LikeId(int userId, int postId) {
            this.userId = userId;
            this.postId = postId;
        }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public int getPostId() { return postId; }
        public void setPostId(int postId) { this.postId = postId; }

    }
}
