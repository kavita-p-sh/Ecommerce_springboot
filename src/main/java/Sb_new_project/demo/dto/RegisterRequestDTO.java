package Sb_new_project.demo.dto;

import Sb_new_project.demo.enums.RoleName;
import Sb_new_project.demo.util.RegexConstant;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Username required")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_ ]{2,49}$", message = "Username must start with a letter and contain only letters, numbers, and underscore")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;


    @NotBlank(message = "Email required")
    @Pattern(regexp = RegexConstant.EMAIL, message = "Email must be valid")
    @Email(message = "Invalid email")
    private String email;


    @NotBlank(message = "Password required")
    @Pattern(regexp = RegexConstant.PASSWORD, message = "Password must contain at least 6 characters, one letter, one number and one special character")
    @Size(min = 6,max = 20, message = "Password must be at least 4 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    @Pattern(regexp = RegexConstant.PHONE, message = "Invalid phone number")
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private RoleName role;

}