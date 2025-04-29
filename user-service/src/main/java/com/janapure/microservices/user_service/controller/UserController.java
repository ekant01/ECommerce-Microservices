package com.janapure.microservices.user_service.controller;


import com.janapure.microservices.user_service.dtos.LoginDTO;
import com.janapure.microservices.user_service.dtos.RegisterDTO;
import com.janapure.microservices.user_service.dtos.UserDTO;
import com.janapure.microservices.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "user/register", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterDTO registerUser(@RequestBody RegisterDTO registerDTO) {
        // Logic to register a user

        return userService.registerUser(registerDTO);
    }

    @RequestMapping(value = "user/login", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> loginUser(@RequestBody LoginDTO loginDTO) {
        // Logic to login a user
        return userService.loginUser(loginDTO);
    }

    @RequestMapping(value = "user/my-self", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<UserDTO> getUserDetails() {
        // Logic to get user details
        UserDTO userDTO = userService.getUserDetails();
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }


    @RequestMapping(value = "user/logout", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<String> logoutUser() {
        // Logic to logout a user
        String response = "Logout successful";
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
