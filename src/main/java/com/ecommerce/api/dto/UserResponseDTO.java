package com.ecommerce.api.dto;

import com.ecommerce.api.enums.RoleName;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
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