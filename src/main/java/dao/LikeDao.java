package dao;

import application.model.entity.Like;
import jakarta.persistence.EntityManager;

import java.util.List;

public class LikeDao implements IDao<Like>{

    @Override
    public void persist(Like entity) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
    }

    @Override
    public Like find(int id) {
        return null;
    }

    @Override
    public List<Like> findAll() {
        return List.of();
    }

    @Override
    public void deleteAll() {

    }

    // find all comments by given post id
    public List<Like> findLikesByPostId(int postId) {
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            List<Like> likes = em.createQuery("SELECT c FROM Comment c WHERE c.post_id = :postId", Like.class)
                    .setParameter("postId", postId)
                    .getResultList();
            return likes;
        } catch (Exception e) {
            System.err.println("CommentDao.java: Error finding comments by post ID. (Check connection to database.)");
            return List.of(); // return empty list
        }
    }

    public boolean checkIfUserLikedPost(int userId, int postId) {
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();

            // Create the embedded ID
            Like.LikeId likeId = new Like.LikeId(userId, postId);

            // Try to find the Like entity by its embedded ID
            Like like = em.find(Like.class, likeId);

            return like != null;

        } catch (Exception e) {
            return false;
        }
    }
}
