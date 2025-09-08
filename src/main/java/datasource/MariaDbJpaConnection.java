package datasource;

import jakarta.persistence.*;

/**
 * Class for creating a connection to the MariaDB database using JPA
 */
public class MariaDbJpaConnection {

    private static EntityManagerFactory emf = null;
    private static EntityManager em = null;

    /**
     * Method for getting the EntityManager object
     *
     * @return EntityManager object
     */
    public static EntityManager getInstance() {
        try {
            if (em == null) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory("ShoutMariaDbUnit");
                }
                em = emf.createEntityManager();
            }
        } catch (Exception e) {
            System.err.println("MariaDbJpaConnection.java: Error in creating/finding/creating database.");
        }
        return em;
    }
}