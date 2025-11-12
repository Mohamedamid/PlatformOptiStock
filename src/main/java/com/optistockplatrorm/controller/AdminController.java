package com.optistockplatrorm.controller;

import com.optistockplatrorm.service.ProductService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/products")

public class AdminController {

//    @Autowired
//    private ProductService productService;
//
//    @PatchMapping("/{sku}/deactivate")
//    public ResponseEntity<Void> deactivateProduct(@PathVariable @NotBlank String sku) {
//        productService.deactivateProduct(sku);
//        return ResponseEntity.ok().build();
//    }

}