package com.janapure.microservices.product_service.services;

import com.janapure.common_security_lib.model.EUserDetails;
import com.janapure.microservices.common_lib.constant.ErrorCode;
import com.janapure.microservices.common_lib.exception.PlatformException;
import com.janapure.microservices.product_service.dto.InventoryDTO;
import com.janapure.microservices.product_service.model.Inventory;
import com.janapure.microservices.product_service.repositories.InventoryRepo;
import com.janapure.microservices.product_service.repositories.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepo inventoryRepository;

    @Autowired
    private ProductRepo productRepo;

    public InventoryDTO addInventory(InventoryDTO inventoryDTO) {
        EUserDetails userDetails = (EUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        System.out.println("Adding inventory for product: " + userDetails.getAuthorities());
        // Check if the product exists in the product repository
        if (productRepo.existsByPdId(inventoryDTO.getProductId())) {
            // Create a new Inventory object and set its properties
            Inventory inventory = new Inventory();
            inventory.setProductId(inventoryDTO.getProductId());
            inventory.setReservedQuantity(0);
            inventory.setQuantityAvailable(inventoryDTO.getQuantityAvailable());
            inventory.setCreatedBy(userDetails.getUsername());
            inventory.setUpdatedBy(userDetails.getUsername());
            inventory.setCreatedAt(System.currentTimeMillis());
            inventory.setUpdatedAt(System.currentTimeMillis());

            // Save the inventory to the repository
            inventoryRepository.save(inventory);
//            inventoryDTO.setId(inventory.getId());
//            inventoryDTO.setCreatedAt(inventory.getCreatedAt());
//            inventoryDTO.setUpdatedAt(inventory.getUpdatedAt());
            // Return the saved inventory as a DTO
            return inventoryDTO;
        } else {
            // If the product does not exist, throw an exception or handle it accordingly
            throw new PlatformException(ErrorCode.USER_NOT_FOUND,"Product with ID " + inventoryDTO.getProductId() + " does not exist.");
        }
    }
}
