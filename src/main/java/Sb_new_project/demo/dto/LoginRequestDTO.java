package Sb_new_project.demo.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "{username.required}")
    private String username;

    @NotBlank(message = "{user.password.required}")
    private String password;
}
