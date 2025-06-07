package com.janapure.microservices.common_lib.constant;


import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND("ECP-404", "User not found"),
    UNAUTHORIZED("USR-401", "Unauthorized access");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

