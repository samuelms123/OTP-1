package dao;

import application.model.entity.User;
import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO class for User entity
 * Implements generic full IDao interface
 */

public class UserDao implements IDao<User>, IReadOnlyDao<User> {
    private final Logger logger = Logger.getLogger(UserDao.class.getName());

    public boolean isUserNameUnique(String userName) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        Long count = (Long) em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username"
                )
                .setParameter("username", userName)
                .getSingleResult();

        return count == 0;

    }

    public boolean isUserEmailUnique(String userEmail) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        Long count = (Long) em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email"
                )
                .setParameter("email", userEmail)
                .getSingleResult();

        return count == 0;

    }

    /**
     * Method for persisting User object in the database.
     *
     * @param user User object to be persisted.
     */
    public void persist(User user) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }

    // Save updated user ENTITY
    public boolean merge(User user) {
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            return true;
        }catch(Exception e) {
            logger.log(Level.SEVERE, "Failed to merge user ", e);
            return false;
        }

    }

    // find user by id
    @Override
    public User find(int id) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        return em.find(User.class, id);
    }

    /**
     * Method for finding individual User objects from the database with name and password.
     *
     * @param name     name of the User object to be found.
     * @return User object with the given name and password.
     */
    public User findUser(String name) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        try {
            return (User) em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username"
                    )
                    .setParameter("username", name)
                    .getSingleResult();
        } catch (Exception e) {
            logger.log(Level.WARNING, "UserDao.java: Error finding user. (Check connection to database.)");
            return null;
        }
    }

    public List<String> findUsersByQuery(String query, String exclude) {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        try {
            return em.createQuery(
                            "SELECT u.username FROM User u WHERE u.username LIKE :username AND u.username <> :exclude", String.class
                    )
                    .setParameter("username", query + "%")
                    .setParameter("exclude", exclude)
                    .setMaxResults(5)
                    .getResultList();
        } catch (Exception e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, () -> "UserDao.java: Error finding users. " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Method for finding all User objects from the database.
     *
     * @return List of all User objects.
     */
    public List<User> findAll() {
        try {
            EntityManager em = datasource.MariaDbJpaConnection.getInstance();
            return em.createQuery("select e from User e").getResultList();
        } catch (Exception e) {
            logger.log(Level.WARNING, "UserDao.java: Error finding all users. (Check connection to database.)");
            return new LinkedList<>(); //return empty
        }
    }

    /**
     * Method for deleting all User objects from the database.
     * Method also resets the AUTO_INCREMENT value of the Users table to 1.
     * Catch block rolls back the transaction if it is active to prevent data corruption.
     */
    public void deleteAll() {
        EntityManager em = datasource.MariaDbJpaConnection.getInstance();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createNativeQuery("ALTER TABLE Users AUTO_INCREMENT = 1").executeUpdate();
            em.clear();
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (logger.isLoggable(Level.WARNING)) {

                logger.log(Level.WARNING, () -> "UserDao.java: Error deleting all users. " + e.getMessage());
            }
        }
    }
}
