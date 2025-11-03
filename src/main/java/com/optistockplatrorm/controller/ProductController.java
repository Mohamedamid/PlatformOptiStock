package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.ApiResponse;
import com.optistockplatrorm.dto.ProductRequestDTO;
import com.optistockplatrorm.dto.ProductResponseDTO;
import com.optistockplatrorm.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO product = productService.createProduct(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Produit créé avec succès !")
                .data(product)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<ProductResponseDTO> productsPage = productService.getAllProducts(page, size);

        ApiResponse response = ApiResponse.builder()
                .message("Liste paginée des produits.")
                .data(productsPage.getContent())
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        ApiResponse response = ApiResponse.builder()
                .message("Produit trouvé.")
                .data(product)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO product = productService.updateProduct(id, dto);
        ApiResponse response = ApiResponse.builder()
                .message("Produit mis à jour avec succès.")
                .data(product)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        ApiResponse response = ApiResponse.builder()
                .message("Produit supprimé avec succès.")
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(response);
    }
}
