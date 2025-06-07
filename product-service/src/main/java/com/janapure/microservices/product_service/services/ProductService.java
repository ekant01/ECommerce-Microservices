package com.janapure.microservices.product_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.janapure.common_security_lib.model.EUserDetails;
import com.janapure.microservices.product_service.dto.ProductDTO;
import com.janapure.microservices.product_service.model.Inventory;
import com.janapure.microservices.product_service.model.Product;
import com.janapure.microservices.product_service.repositories.InventoryRepo;
import com.janapure.microservices.product_service.repositories.ProductRepo;
import com.janapure.microservices.proto.OrderServiceGrpc;
import com.janapure.microservices.proto.ProductListRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ProductService is a service class that handles product-related operations.
 * It provides methods to fetch, add, update, and delete products.
 */

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepo productRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceBlockingStub;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    /**
     * Adds a new product to the system.
     *
     * @param productDTO The product data transfer object containing product details.
     * @return The added product data transfer object.
     */
    public ProductDTO addProduct(ProductDTO productDTO) {
        // Here you would typically save the product to the database
         EUserDetails userDetails = (EUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        Product product = new Product();

        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setAttributes(productDTO.getAttributes());
        product.setSellerId(userDetails.getUsername());
        product.setPdId(generateProductId());
        product.setDeleted(false);
        product.setAttributes(productDTO.getAttributes());
        product.setCreatedAt(System.currentTimeMillis());
        product.setModifiedAt(System.currentTimeMillis());

        productRepo.save(product);

        // Set additional fields in the DTO
        productDTO.setPdId(product.getPdId());
        productDTO.setCreatedAt(product.getCreatedAt());
        productDTO.setModifiedAt(product.getModifiedAt());
        productDTO.setId(product.getId());
        productDTO.setSellerId(product.getSellerId());

        return productDTO;
    }

    /**
     * Updates an existing product in the system.
     *
     * @param productDTO The product data transfer object containing updated product details.
     * @return The updated product data transfer object.
     */
    public ProductDTO updateProduct(ProductDTO productDTO) {
        // Here you would typically update the product in the database
        Product product = productRepo.findById(productDTO.getId()).orElse(null);
        if (product != null) {
            product.setProductName(productDTO.getProductName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setCategory(productDTO.getCategory());
            product.setAttributes(productDTO.getAttributes());
            product.setModifiedAt(System.currentTimeMillis());

            productRepo.save(product);
        }
        return productDTO;
    }

    /**
     * Deletes a product from the system.
     *
     * @param productId The ID of the product to be deleted.
     */
    public void deleteProduct(String productId) {
        // Here you would typically delete the product from the database
        Product product = productRepo.findById(productId).orElse(null);
        if (product != null) {
            product.setDeleted(true);
            productRepo.save(product);
        }
    }

    /**
     * Generates a unique product ID.
     *
     * @return A unique product ID as a string.
     */
    public String generateProductId() {
        // Generate a unique product ID
        return UUID.randomUUID().toString().replace("-", "");
    }

    public Page<ProductDTO> getAllProducts(String productName, String category, double minPrice, double maxPrice) {

        return null;
    }

    public List<ProductDTO> getAllProduct() {
        // Here you would typically fetch all products from the database
        List<Product> products = productRepo.findAll();
        return products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setPdId(product.getPdId());
            productDTO.setProductName(product.getProductName());
            productDTO.setDescription(product.getDescription());
            productDTO.setPrice(product.getPrice());
            productDTO.setCategory(product.getCategory());
            productDTO.setSellerId(product.getSellerId());
            productDTO.setAttributes(product.getAttributes());
            productDTO.setCreatedAt(product.getCreatedAt());
            productDTO.setModifiedAt(product.getModifiedAt());
            return productDTO;
        }).toList();
    }

    public ProductDTO getProductById(String productId) {
        // Here you would typically fetch the product by ID from the database
        Product product = productRepo.findById(productId).orElse(null);
        if (product != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId(product.getId());
            productDTO.setProductName(product.getProductName());
            productDTO.setDescription(product.getDescription());
            productDTO.setPrice(product.getPrice());
            productDTO.setCategory(product.getCategory());
            productDTO.setAttributes(product.getAttributes());
            productDTO.setCreatedAt(product.getCreatedAt());
            productDTO.setModifiedAt(product.getModifiedAt());
            return productDTO;
        }else {
            throw new RuntimeException("Product not found");
        }
    }

    @KafkaListener(topics = "product.release.stock", groupId = "product-service")
    public void releaseStock(String event) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(event);
        String productId = jsonNode.get("orderId").asText();
        String status = jsonNode.get("status").asText();
        Map<String,Integer> productIds = getProductIdsBySellerId(productId);
        if (status != null && status.equalsIgnoreCase("COMPLETED")) {
            // Logic to release stock for the products
            productIds.forEach(
                    (id, quantity) -> {
                        Inventory inventory = inventoryRepo.findByProductId(id);
                        if (inventory != null) {
                            try {
                                inventory.confirmOrder(quantity);
                                inventoryRepo.save(inventory);
                            } catch (RuntimeException e) {
                                System.out.println("Error releasing stock for product ID " + id + ": " + e.getMessage());
                                logger.info("Error releasing stock for product ID " + id + ": " + e.getMessage());
                            }
                        } else {
                            logger.info("Inventory not found for product ID: " + id);
                        }
                    }
            );
        }
        if (status!=null && status.equalsIgnoreCase("CANCELLED")) {
            productIds.forEach(
                    (id, quantity) -> {
                        Inventory inventory = inventoryRepo.findByProductId(id);
                        if (inventory != null) {
                            try {
                                inventory.releaseStock(quantity);
                                inventoryRepo.save(inventory);
                            } catch (RuntimeException e) {
                                logger.info("Error releasing stock for product ID " + id + ": " + e.getMessage());
                            }
                        } else {
                            logger.info("Inventory not found for product ID: " + id);
                        }
                    }
            );
        }
    }

    public Map<String,Integer> getProductIdsBySellerId(String orderId) {
        // Fetch product IDs by seller ID
        ProductListRequest request = ProductListRequest.newBuilder()
                .setOrderId(orderId)
                .build();
        return orderServiceBlockingStub.getProductList(request).getProductIdsMap();
    }

}

