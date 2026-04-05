package Sb_new_project.demo.dto;

import Sb_new_project.demo.enums.RoleName;
import Sb_new_project.demo.util.RegexConstant;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Username required")
    @Pattern(regexp = RegexConstant.USERNAME, message = "Username must start with a letter and contain only letters, numbers, and underscore")
    private String username;


    @NotBlank(message = "Email required")
    @Pattern(regexp = RegexConstant.EMAIL, message = "Email must be valid")
    @Email(message = "Invalid email")
    private String email;


    @NotBlank(message = "Password required")
    @Pattern(regexp = RegexConstant.PASSWORD, message = "Password must contain at least 6 characters, one letter, one number and one special character")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = RegexConstant.PHONE, message = "Invalid phone number")
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private RoleName role;

}