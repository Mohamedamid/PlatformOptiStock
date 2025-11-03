package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.ProductRequestDTO;
import com.optistockplatrorm.dto.ProductResponseDTO;
import com.optistockplatrorm.entity.Category;
import com.optistockplatrorm.entity.Product;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.mapper.ProductMapper;
import com.optistockplatrorm.repository.CategoryRepository;
import com.optistockplatrorm.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductMapper productMapper;

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
                if (role != Role.ADMIN && role != Role.WAREHOUSE_MANAGER && role != Role.CLIENT) {
                    throw new RuntimeException("Accès refusé : seuls les administrateurs ou les gestionnaires d’entrepôt peuvent " +
                            "effectuer cette action.");
                }
                break;

            case "UPDATE":
                if (role != Role.ADMIN) {
                    throw new RuntimeException("Accès refusé : seul un administrateur peut modifier un produit.");
                }
                break;

            case "DELETE":
                if (role != Role.ADMIN) {
                    throw new RuntimeException("Accès refusé : seul un administrateur peut supprimer un produit.");
                }
                break;

            default:
                throw new RuntimeException("Action non autorisée.");
        }
    }

    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        checkAccess("READ");

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productsPage = productRepository.findAll(pageable);

        return productsPage.map(productMapper::toDTO);
    }

    public ProductResponseDTO getProductById(Long id) {
        checkAccess("READ");

        Optional<Product> product = productRepository.findById(id);
        return product.map(productMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'identifiant : " + id));
    }

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        checkAccess("CREATE");

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'identifiant : " + dto.categoryId()));

        Product product = Product.builder()
                .sku(dto.sku())
                .name(dto.name())
                .category(category)
                .purchasePrice(dto.purchasePrice())
                .sellingPrice(dto.sellingPrice())
                .active(dto.active())
                .createdAt(LocalDateTime.now())
                .build();

        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        checkAccess("UPDATE");

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'identifiant : " + id));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec l'identifiant : " + dto.categoryId()));

        product.setName(dto.name());
        product.setSku(dto.sku());
        product.setPurchasePrice(dto.purchasePrice());
        product.setSellingPrice(dto.sellingPrice());
        product.setCategory(category);
        product.setActive(dto.active());

        Product updated = productRepository.save(product);
        return productMapper.toDTO(updated);
    }

    public boolean deleteProduct(Long id) {
        checkAccess("DELETE");

        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produit introuvable avec l'identifiant : " + id);
        }

        productRepository.deleteById(id);
        return true;
    }
}
