package Sb_new_project.demo.dto;

import Sb_new_project.demo.enums.RoleName;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO implements Serializable {

    private String username;
    private String email;
    private RoleName role;
    private String phoneNumber;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;

}