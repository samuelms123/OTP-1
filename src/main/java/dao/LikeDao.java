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
            List<Like> likes = em.createQuery("SELECT l FROM Like l WHERE l.post.id = :postId", Like.class)
                    .setParameter("postId", postId)
                    .getResultList();
            return likes;
        } catch (Exception e) {
            System.err.println("LikeDao.java: Error finding likes by post id. (Check connection to database.)");
            return List.of(); //return empty
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

    public Like findLike(int userId, int postId) {
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            Like.LikeId likeId = new Like.LikeId(userId, postId);
            return em.find(Like.class, likeId);

        } catch (Exception e) {
            // Log the exception to aid debugging
            System.err.println("Error finding Like for userId=" + userId + ", postId=" + postId);
            e.printStackTrace();
            return null;
        }
    }

    public void deleteLike(Like like) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.remove(like);
        em.getTransaction().commit();
    }
}
