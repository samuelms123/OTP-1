package datasource;

import jakarta.persistence.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for creating a connection to the MariaDB database using JPA
 */
public final class MariaDbJpaConnection {
    private final static Logger LOGGER = Logger.getLogger(MariaDbJpaConnection.class.getName());
    private static EntityManagerFactory emFactory;
    private static EntityManager entityManager;

    private MariaDbJpaConnection() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Method for getting the EntityManager object
     *
     * @return EntityManager object
     */
    public static EntityManager getInstance() {
        try {
            if (entityManager == null) {
                if (emFactory == null) {
                    emFactory = Persistence.createEntityManagerFactory("ShoutMariaDbUnit");
                }
                entityManager = emFactory.createEntityManager();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "MariaDbJpaConnection.java: Error in creating/finding/creating database.");
        }
        return entityManager;
    }
}