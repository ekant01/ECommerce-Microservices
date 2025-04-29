package com.janapure.microservices.user_service.dtos;

import com.janapure.microservices.user_service.entities.UserAddress;
import lombok.Data;

import java.security.PublicKey;

@Data
public class AddressDTO {

    private Long id;

    private Long user_id;

    private UserAddress.AddressType type;// "HOME", "WORK", "OTHER"

    private String addressLine1;

    private String addressLine2;

    private String state;

    private String city;

    private String zipCode;

    private String country;

    private boolean is_default;

}
