package com.janapure.microservices.user_service.service;

import com.janapure.microservices.user_service.entities.User;
import com.janapure.microservices.user_service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserCredentialUsername(username);

        if (user != null) {
            Set<GrantedAuthority> authorities = user
                    .getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                    .collect(Collectors.toSet());
             return new org.springframework.security.core.userdetails.User(
                    user.getUserCredential().getUsername(),
                    user.getUserCredential().getPassword(),
                    user.getUserCredential().isEnabled(),
                    true, true, true,
                    authorities
            );
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}

