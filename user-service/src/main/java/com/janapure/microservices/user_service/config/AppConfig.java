package com.janapure.microservices.user_service.config;

import com.janapure.common_security_lib.JWTUitil;
import com.janapure.common_security_lib.config.SecurityConfig;
import com.janapure.common_security_lib.filter.AuthenticationFilter;
import com.janapure.microservices.user_service.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AppConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public JWTUitil jwtUtil() {
        return new JWTUitil();
    }

    @Bean
    public com.janapure.common_security_lib.config.SecurityConfig securityConfig() {
        return new SecurityConfig();
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(jwtUtil(),securityConfig());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationFilter authenticationFilter) throws Exception {


        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register", "/user/login").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
