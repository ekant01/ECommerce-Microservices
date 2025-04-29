package com.janapure.microservices.user_service.repository;

import com.janapure.microservices.user_service.entities.UserToRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserToRoleRepo extends JpaRepository<UserToRoles, Long> {
    // This is a placeholder for the actual implementation
    // You can define custom query methods here if needed
}
