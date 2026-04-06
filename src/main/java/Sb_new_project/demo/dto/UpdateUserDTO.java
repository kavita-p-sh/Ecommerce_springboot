package Sb_new_project.demo.dto;

import Sb_new_project.demo.util.RegexConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @Pattern(regexp = RegexConstant.USERNAME, message = "Username must start with a letter and contain only letters, numbers, spaces, and underscore")
    private String username;

    @Pattern(regexp = RegexConstant.EMAIL, message = "Email must be valid")
    private String email;

    @Pattern(regexp = RegexConstant.PASSWORD, message = "Password must contain at least 6 characters, one letter, one number and one special character")
    private String password;

    @Pattern(regexp = RegexConstant.PHONE, message = "Invalid phone number")
    private String phoneNumber;
}