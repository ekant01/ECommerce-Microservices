package com.janapure.microservices.user_service.repository;


import com.janapure.microservices.user_service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {


    //User findUserCredentialByUserName(String username);

   //User findByUserCredential_UserName(String username);
    User findByUserCredentialUsername(String username);
}
