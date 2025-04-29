package com.janapure.microservices.product_service.controller;

import com.janapure.microservices.product_service.dto.ProductDTO;
import com.janapure.microservices.product_service.model.Product;

import com.janapure.microservices.product_service.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController()
@Slf4j
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService){
        this.productService=productService;
    }

    @RequestMapping(path = "/api/product",method = GET)
    @ResponseStatus(HttpStatus.OK)
    public List<ProductDTO> getAllProducts(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
            ){

        log.info("Fetching all products with filters: productName={}, category={}, minPrice={}, maxPrice={}",
                productName, category, minPrice, maxPrice);
        return productService.getAllProduct();

    }


    @RequestMapping(path = "/api/product",method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO addProduct(@RequestBody ProductDTO productDTO){
System.out.println(".................."+productDTO);
        log.info("Adding product: {}", productDTO);
        return productService.addProduct(productDTO);
    }

    @RequestMapping(path = "/api/product",method = PUT)
    @ResponseStatus(HttpStatus.OK)
    public ProductDTO updateProduct(@RequestBody ProductDTO productDTO){
        log.info("Updating product: {}", productDTO);
        return productService.updateProduct(productDTO);
    }

    @RequestMapping(path = "/api/product/{productId}",method = DELETE)
    public void deleteProduct(@PathVariable String productId){
        log.info("Deleting product with ID: {}", productId);
        productService.deleteProduct(productId);
    }

    @GetMapping("/ping")
    public String ping() {
        System.out.println("Ping received");
        return "pong";
    }

}
