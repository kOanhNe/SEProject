package ecommerce.shoestore.auth.user;

import ecommerce.shoestore.auth.user.UserGender;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
<<<<<<< HEAD
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long userId;
    protected String fullname;
    protected String email;
    protected LocalDate dateOfBirth;

    //Annotation chỉ định rằng trường này lưu dưới dạng String
    @Enumerated(EnumType.STRING)
    protected UserGender gender;

    protected String phone;
    protected String avatar;

=======
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String avatar;

    @Column(name = "registrationDate", updatable = false)
    private LocalDateTime registrationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "accountId", referencedColumnName = "accountId")
    private Account account;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "addressId", referencedColumnName = "addressId")
    private Address address;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
    }
>>>>>>> a9097a0 (Update Cart and CartItem object)
}
