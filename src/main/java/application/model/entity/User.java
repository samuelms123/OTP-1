package application.model.entity;
import jakarta.persistence.*;

/**
 * Entity class representing an entry in the "Users" table.
 * This class maps user data stored in the database.
 * @Column annotations are used to map the fields to the corresponding columns in the database.
 * @Entity annotation is used to mark this class as an entity class.
 * @Table annotation is used to specify the name of the table in the database.
 * @Id annotation is used to mark the id field as the primary key.
 * @GeneratedValue annotation is used to specify the generation strategy for the primary key.
 */
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    //private LocalDate date;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="username")
    private String username;

    @Column(name="birthdate")
    private String birthdate;

    @Column(name="password")
    private String password;

    // shorter constructor for login attempt
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String username, String birthdate, String password) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.birthdate = birthdate;
        this.password = password;
    }

    /**
     * Default constructor for hibernate to instantiate empty objects.
     */
    public User() {

    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
