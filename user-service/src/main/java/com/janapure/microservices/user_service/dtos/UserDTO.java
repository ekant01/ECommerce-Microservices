package com.janapure.microservices.user_service.dtos;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String password;

    private String phoneNumber;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    private List<AddressDTO> addresses;

}
