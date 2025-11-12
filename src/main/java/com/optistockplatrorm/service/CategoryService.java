package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.CategoryRequestDTO;
import com.optistockplatrorm.dto.CategoryResponseDTO;
import com.optistockplatrorm.entity.Category;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.mapper.CategoryMapper;
import com.optistockplatrorm.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private HttpSession session;

    private void checkAccess(String action) {
        Object roleObj = session.getAttribute("role");

        if (roleObj == null) {
            throw new RuntimeException("Utilisateur non connecté.");
        }

        Role role = (Role) roleObj;

        switch (action) {
            case "CREATE":
            case "READ":
                if (role != Role.ADMIN && role != Role.WAREHOUSE_MANAGER) {
                    throw new RuntimeException("Accès refusé : seuls les administrateurs ou les gestionnaires d’entrepôt peuvent effectuer cette action.");
                }
                break;

            case "UPDATE":
                if (role != Role.ADMIN) {
                    throw new RuntimeException("Accès refusé : seul un administrateur peut modifier une catégorie.");
                }
                break;

            case "DELETE":
                if (role != Role.ADMIN) {
                    throw new RuntimeException("Accès refusé : seul un administrateur peut supprimer une catégorie.");
                }
                break;

            default:
                throw new RuntimeException("Action non autorisée.");
        }
    }

    public Page<CategoryResponseDTO> getAllCategory(int page, int size) {
        checkAccess("READ");

        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        checkAccess("READ");

        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'identifiant : " + id));
        return categoryMapper.toDto(category);
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO dto) {
        checkAccess("CREATE");

        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        checkAccess("UPDATE");

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'identifiant : " + id));

        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setActive(dto.active());

        Category updated = categoryRepository.save(category);
        return categoryMapper.toDto(updated);
    }

    public void deleteCategory(Long id) {
        checkAccess("DELETE");

        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Catégorie introuvable avec l'identifiant : " + id);
        }

        categoryRepository.deleteById(id);
    }
}
