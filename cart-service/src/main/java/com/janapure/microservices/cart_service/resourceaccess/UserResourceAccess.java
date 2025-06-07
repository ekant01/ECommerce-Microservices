package com.janapure.microservices.cart_service.resourceaccess;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserResourceAccess {

    public boolean hasPermissionToCart(Authentication auth, Object obj){
        if(obj instanceof String) {
            String userId = (String) obj;
            return auth.getName().equals(userId);
        }
        return false;
    }
}
