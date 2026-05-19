package practical.post.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import practical.post.model.enums.RegistrationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    public static final String ID_FIELD = "id";
    public static final String EMAIL_FIELD = "email";
    public static final String USERNAME_FIELD = "username";
    public static final String CREATED_FIELD = "created";
    public static final String UPDATED_FIELD = "updated";
    public static final String DELETED_FIELD = "deleted";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "email")
    private String email;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();
    @Column(name = "updated")
    private LocalDateTime updated = LocalDateTime.now();
    @Column(name = "deleted")
    private boolean deleted = false;
    @Column(name = "registration_status")
    @Enumerated(value = EnumType.STRING)
    private RegistrationStatus registrationStatus;
    @Column(name = "last_login")
    private LocalDateTime lastLogin = LocalDateTime.now();
    @OneToMany(mappedBy = "user")
    private List<Post> posts;
}
