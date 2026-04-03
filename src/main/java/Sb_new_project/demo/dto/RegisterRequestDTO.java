package Sb_new_project.demo.dto;

import Sb_new_project.demo.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Username required")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{2,49}$", message = "Username must start with a letter and contain only letters, numbers, and underscore")
    private String username;

    @NotBlank(message = "Email required")
    @Email(message = "Invalid email")
    private String email;


    @NotBlank(message = "Password required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[@$!%*#?&]).{6,}$", message = "Password must contain at least 6 characters, one letter, one number and one special character")
    @Size(min = 4, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Indian phone number")
    private String phoneNumber;

    private RoleName role;



}