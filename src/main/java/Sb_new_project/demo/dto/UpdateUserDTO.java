package Sb_new_project.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*#?&]).{6,}$", message = "Password must contain at least 6 characters, one letter, one number and one special character")
    private String password;

    @Pattern(regexp = "^[6-9]{10}$", message = "Invalid phone number")
    private String phoneNumber;
}