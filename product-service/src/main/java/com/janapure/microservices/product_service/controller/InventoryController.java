package com.janapure.microservices.product_service.controller;


import com.janapure.microservices.product_service.dto.InventoryDTO;
import com.janapure.microservices.product_service.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController()
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // Define endpoints for inventory management here

    // Add inventory for a product
    @PostMapping(path = "/products/inventory", consumes = "application/json")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public InventoryDTO addInventory(@RequestBody InventoryDTO inventoryDTO) {
        System.out.println("Adding inventory for product: " + inventoryDTO.getProductId());
        return inventoryService.addInventory(inventoryDTO);
    }

    @GetMapping("/products/inventory")
    public String getInventory() {
        return "Inventory endpoint is working";
    }

//    @PostMapping("/products/inventory")
//    @ResponseStatus(org.springframework.http.HttpStatus.OK)
//    public String updateInventory(@RequestBody InventoryDTO inventoryDTO) {
//        return "Inventory update endpoint is working"+inventoryDTO.getProductId();
//    }
}
