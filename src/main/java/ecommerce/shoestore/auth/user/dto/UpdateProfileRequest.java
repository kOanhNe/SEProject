package ecommerce.shoestore.auth.user.dto;

import lombok.Data;
import ecommerce.shoestore.auth.user.UserGender;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String email;
    private String avatar;
    private String fullname;
    private String phone;
    private UserGender gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private MultipartFile avatarFile;
    private String province;
    private String district;
    private String commune;
    private String streetDetail;
}