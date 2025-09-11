package dao;

import application.model.entity.Post;
import application.model.entity.User;
import jakarta.persistence.EntityManager;

import java.util.LinkedList;
import java.util.List;

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

    @Override
    public void deleteAll() {

    }
}
