package Sb_new_project.demo.dto;

import Sb_new_project.demo.entity.Role;
import lombok.*;

import java.io.Serializable;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO implements Serializable {

    private Long userId;
    private String username;
    private String email;
    private String role;





}