package dao;

import application.model.entity.Post;
import application.model.entity.User;
import jakarta.persistence.EntityManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PostDao implements IDao<Post>{

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
            List<Post> posts = em.createQuery("select e from Post e").getResultList();
            return posts;
        } catch (Exception e) {
            System.err.println("UserDao.java: Error finding all users. (Check connection to database.)");
            return new LinkedList<>(); //return empty
        }
    }

    public List<Post> findPostsByUsers(Set<User> users) {
        if (users.isEmpty()) {
            return List.of();
        }
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            List<Integer> userIds = users.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            List<Post> posts = em.createQuery(
                            "SELECT p FROM Post p WHERE p.userId IN :userIds", Post.class)
                    .setParameter("userIds", userIds)
                    .getResultList();

            return posts;
        } catch (Exception e) {
            System.err.println("PostDao.java: Error finding posts by users: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public void deleteAll() {

    }
}
