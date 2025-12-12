package ecommerce.shoestore.auth.user;

import ecommerce.shoestore.auth.user.UserGender;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
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

}
