package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.dto.CategoryRequestDTO;
import com.optistockplatrorm.dto.CategoryResponseDTO;
import com.optistockplatrorm.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<OptiResponse> getAllCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CategoryResponseDTO> categoryPage = categoryService.getAllCategory(page, size);

        OptiResponse response = OptiResponse.builder()
                .message("Liste paginée des catégories récupérée avec succès.")
                .data(categoryPage.getContent())
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);

        OptiResponse response = OptiResponse.builder()
                .message("Catégorie trouvée avec succès.")
                .data(category)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OptiResponse> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(dto);

        OptiResponse response = OptiResponse.builder()
                .message("Catégorie créée avec succès.")
                .data(createdCategory)
                .status(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptiResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO dto) {

        categoryService.updateCategory(id, dto);

        OptiResponse response = OptiResponse.builder()
                .message("Catégorie mise à jour avec succès.")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptiResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        OptiResponse response = OptiResponse.builder()
                .message("Catégorie supprimée avec succès.")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }
}
