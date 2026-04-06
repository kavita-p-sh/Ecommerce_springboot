package Sb_new_project.demo.dto;

import Sb_new_project.demo.enums.RoleName;
import Sb_new_project.demo.util.RegexConstant;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "{username.required}")
    @Pattern(regexp = RegexConstant.USERNAME, message = "{username.valid}")
    private String username;


    @NotBlank(message = "{email.required}")
    @Pattern(regexp = RegexConstant.EMAIL, message = "{email.valid}")
    private String email;


    @NotBlank(message ="{password.required}")
    @Pattern(regexp = RegexConstant.PASSWORD, message = "{user.password.pattern}")
    private String password;

    @NotBlank(message = "{phone.required}")
    @Pattern(regexp = RegexConstant.PHONE, message = "{phone.format}")
    private String phoneNumber;

    @NotNull(message = "{role.required}")
    @Enumerated(EnumType.STRING)
    private RoleName role;

}