package Sb_new_project.demo.dto;

import Sb_new_project.demo.util.RegexConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @Pattern(regexp = RegexConstant.USERNAME, message = "Username must start with a letter and contain only letters, numbers, spaces, and underscore")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Invalid email format")
    @Pattern(regexp = RegexConstant.EMAIL, message = "Email must be valid")
    private String email;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    @Pattern(regexp = RegexConstant.PASSWORD, message = "Password must contain at least 6 characters, one letter, one number and one special character")
    private String password;

    @Pattern(regexp = RegexConstant.PHONE, message = "Invalid phone number")
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    private String phoneNumber;
}