package com.janapure.microservices.user_service.repository;

import com.janapure.microservices.user_service.entities.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepo extends JpaRepository<UserAddress, Long> {

}
