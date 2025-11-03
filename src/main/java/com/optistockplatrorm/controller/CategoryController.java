package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.ApiResponse;
import com.optistockplatrorm.dto.CategoryRequestDTO;
import com.optistockplatrorm.dto.CategoryResponseDTO;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<CategoryResponseDTO> categoriesPage = categoryService.getAll(page, size);

        ApiResponse response = ApiResponse.builder()
                .message("Liste paginée des catégories.")
                .data(categoriesPage.getContent())
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(dto);
        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody CategoryRequestDTO dto) {
        categoryService.update(id, dto);
        return ResponseEntity.ok("Catégorie mise à jour avec succès !");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok("Catégorie supprimée avec succès !");
    }
}
