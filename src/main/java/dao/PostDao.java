package dao;

import application.model.entity.Post;
import application.model.entity.User;
import jakarta.persistence.EntityManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PostDao implements IDao<Post>{
    private final Logger logger = Logger.getLogger(PostDao.class.getName());

    @Override
    public void persist(Post post) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.persist(post);
        em.getTransaction().commit();
    }

    @Override
    public Post find(int id) {
        return null;
    }

    @Override
    public List<Post> findAll() {
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            return em.createQuery("select e from Post e").getResultList();
        } catch (Exception e) {
            logger.log(Level.WARNING, "UserDao.java: Error finding all users. (Check connection to database.)");
            return new LinkedList<>(); //return empty
        }
    }

    public List<Post> findPostsByUsers(Set<User> users) {
        if (users == null) {
            return List.of();
        }
        if (users.isEmpty()) {
            return List.of();
        }

        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            List<Integer> userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            return em.createQuery(
                            "SELECT p FROM Post p WHERE p.userId IN :userIds", Post.class)
                    .setParameter("userIds", userIds)
                    .getResultList();

        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, () -> "PostDao.java: Error finding posts by users: " + e.getMessage());
            }
            return List.of();
        }
    }

    public boolean deletePostById(int id) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        try {
            em.getTransaction().begin();
            Post post = em.find(Post.class, id);
            if (post != null) {
                em.remove(post);
                em.getTransaction().commit();
                return true;
            } else {
                em.getTransaction().rollback();
                return false; // post not found
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteAll() {

    }
}
