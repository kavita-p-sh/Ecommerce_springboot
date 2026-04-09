package com.ecommerce.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoggedInUsersResponseDTO {

    private List<LoggedInUserDTO> users;
}