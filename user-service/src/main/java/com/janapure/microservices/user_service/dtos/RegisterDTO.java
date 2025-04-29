package com.janapure.microservices.user_service.dtos;

import lombok.Data;
import java.util.List;

@Data
public class RegisterDTO {

    // User basic info
    private String firstName;
    private String lastName;
    private String mobileNo;

    // Credentials
    private String username;
    private String password;

    // Optional address info
    private List<AddressDTO> addresses;


}
