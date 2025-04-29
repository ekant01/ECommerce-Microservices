package com.janapure.microservices.user_service.service;


import com.janapure.microservices.user_service.dtos.AddressDTO;
import com.janapure.microservices.user_service.dtos.LoginDTO;
import com.janapure.microservices.user_service.dtos.RegisterDTO;
import com.janapure.microservices.user_service.dtos.UserDTO;
import com.janapure.microservices.user_service.entities.*;
import com.janapure.microservices.user_service.repository.*;
import com.janapure.microservices.user_service.utils.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private UserCredentialRepo credentialRepository;

    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private UserAddressRepo addressRepository;

    @Autowired
    private UserToRoleRepo userToRolesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JWTUtility jwtUtil;

    @Transactional
    public RegisterDTO registerUser(RegisterDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setMobileNo(userDTO.getMobileNo());
        user.setCreatedAt(LocalDateTime.now());
        user.setModifiedAt(LocalDateTime.now());

        Roles customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Default role CUSTOMER not found"));
        user.setRoles(List.of(customerRole));

        User savedUser = userRepository.save(user);

        // Create Credential
        UserCredential credential = new UserCredential();
        credential.setEmail(userDTO.getUsername());
        credential.setUsername(userDTO.getUsername());
        credential.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        credential.setEnabled(true);
        credential.setCreatedAt(LocalDateTime.now());
        credential.setModifiedAt(LocalDateTime.now());
        credential.setUser(savedUser);
        credentialRepository.save(credential);

        // Assign default role "CUSTOMER"
//        Roles customerRole = roleRepository.findByRoleName("CUSTOMER")
//                .orElseThrow(() -> new RuntimeException("Default role CUSTOMER not found"));
//
//        UserToRoles userToRoles = new UserToRoles();
//        UserToRoles.UserRolePrimaryKey primaryKey = new UserToRoles.UserRolePrimaryKey();
//        primaryKey.setUserId(savedUser.getId());
//        primaryKey.setRoleId(customerRole.getId());
//        userToRoles.setUserRolePrimaryKey(primaryKey);
//        userToRoles.setUser(savedUser);
//        userToRoles.setRole(customerRole);
//        userToRolesRepository.save(userToRoles);

        // Save addresses (if provided)
        if (userDTO.getAddresses() != null) {
            List<UserAddress> addresses = userDTO.getAddresses().stream().map(addrDto -> {
                UserAddress addr = new UserAddress();
                addr.setUser(savedUser);
                addr.setAddressType(UserAddress.AddressType.valueOf(String.valueOf(addrDto.getType())));
                addr.setAddressLine1(addrDto.getAddressLine1());
                addr.setAddressLine2(addrDto.getAddressLine2());
                addr.setState(addrDto.getState());
                addr.setZipCode(addrDto.getZipCode());
                addr.setCountry(addrDto.getCountry());
                addr.setCreatedAt(LocalDateTime.now());
                addr.setModifiedAt(LocalDateTime.now());
                addr.setCity(addrDto.getCity());
                return addr;
            }).collect(Collectors.toList());

            addressRepository.saveAll(addresses);
        }

        return userDTO;
    }

    public ResponseEntity<String> loginUser(LoginDTO loginDTO) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());

            String jwt = jwtUtil.generateToken(userDetails.getUsername(),userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            //log.error("Exception occurred while createAuthenticationToken ", e);
            System.out.println("Exception occurred while createAuthenticationToken "+ e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }

    public UserDTO getUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String username = securityContext.getAuthentication().getName();
        User user = userRepository.findByUserCredentialUsername(username);

        if (user != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setPhoneNumber(user.getMobileNo());
            userDTO.setUsername(user.getUserCredential().getUsername());
            userDTO.setEmail(user.getUserCredential().getEmail());
            userDTO.setCreatedAt(user.getCreatedAt());
            userDTO.setModifiedAt(user.getModifiedAt());
            userDTO.setAddresses(user.getAddresses().stream().map(addr -> {
                AddressDTO addrDto = new AddressDTO();
                addrDto.setUser_id(addr.getUser().getId());
                addrDto.setId(addr.getId());
                addrDto.setAddressLine1(addr.getAddressLine1());
                addrDto.setAddressLine2(addr.getAddressLine2());
                addrDto.setCity(addr.getCity());
                addrDto.setState(addr.getState());
                addrDto.setZipCode(addr.getZipCode());
                addrDto.setCountry(addr.getCountry());
                addrDto.setType(addr.getAddressType());
                return addrDto;
            }).collect(Collectors.toList()));
            return userDTO;
        }
        return null;
    }

}
