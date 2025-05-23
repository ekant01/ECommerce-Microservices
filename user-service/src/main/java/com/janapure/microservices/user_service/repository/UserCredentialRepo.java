package com.janapure.microservices.user_service.repository;

import com.janapure.microservices.user_service.entities.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepo extends JpaRepository<UserCredential, Long> {

    boolean existsByUsername(String username);


}
