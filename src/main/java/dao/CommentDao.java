package dao;

import application.model.entity.Comment;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CommentDao implements IDao<Comment>{

    @Override
    public void persist(Comment entity) {
       EntityManager em = datasource.MariaDbJpaConnection.getInstance();
       em.getTransaction().begin();
       em.persist(entity);
       em.getTransaction().commit();
    }

    @Override
    public Comment find(int id) {
        return null;
    }

    @Override
    public List<Comment> findAll() {
        return List.of();
    }

    @Override
    public void deleteAll() {

    }

    // find all comments by given post id
    public List<Comment> findByPostId(int postId) {
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            List<Comment> comments = em.createQuery("SELECT c FROM Comment c WHERE c.post.id = :postId", Comment.class)
                    .setParameter("postId", postId)
                    .getResultList();
            return comments;
        } catch (Exception e) {
            System.err.println("CommentDao.java: Error finding comments by post ID. (Check connection to database.)");
            return List.of(); // return empty list
        }
    }
}
