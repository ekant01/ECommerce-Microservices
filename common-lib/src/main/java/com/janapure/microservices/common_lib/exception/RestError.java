package com.janapure.microservices.common_lib.exception;


import lombok.Data;

@Data
public class RestError {

    private String status;
    private String code;
    private String error;
    private String message;;

}
