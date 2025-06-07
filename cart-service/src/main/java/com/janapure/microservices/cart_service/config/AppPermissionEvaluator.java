package com.janapure.microservices.cart_service.config;

import com.janapure.common_security_lib.model.EUserDetails;
import com.janapure.microservices.cart_service.resourceaccess.UserResourceAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class AppPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private UserResourceAccess userResourceAccess;
    /**
     * @param authentication
     * @param targetDomainObject
     * @param permission
     * @return
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || permission == null) {
            return false;
        }
        EUserDetails userDetails = (EUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        switch (permission.toString()) {
            case "CART_READ":
                System.out.println("Checking CART_READ permission for user: " + username + " on target: " + targetDomainObject);
                return userResourceAccess.hasPermissionToCart(authentication, targetDomainObject);
            case "CART_WRITE":
                System.out.println("Checking CART_WRITE permission for user: " + username + " on target: " + targetDomainObject);
                return userResourceAccess.hasPermissionToCart(authentication, targetDomainObject);
            default:
                return false;
        }
    }

    /**
     * @param authentication
     * @param targetId
     * @param targetType
     * @param permission
     * @return
     */
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
