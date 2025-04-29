package com.janapure.microservices.user_service.repository;

import com.janapure.microservices.user_service.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRoleName(String customer);
}
