package ecommerce.shoestore.auth.account;

import jakarta.persistence.*;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

}
