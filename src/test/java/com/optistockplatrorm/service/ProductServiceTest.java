package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.ProductRequestDTO;
import com.optistockplatrorm.dto.ProductResponseDTO;
import com.optistockplatrorm.entity.Category;
import com.optistockplatrorm.entity.Product;
import com.optistockplatrorm.mapper.ProductMapper;
import com.optistockplatrorm.repository.CategoryRepository;
import com.optistockplatrorm.repository.ProductRepository;
import com.optistockplatrorm.util.VerifyRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private VerifyRole verifyRole;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private ProductRequestDTO productRequestDTO;

    @BeforeEach
    void setup() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = Product.builder()
                .id(1L)
                .sku("SKU001")
                .name("Laptop")
                .category(category)
                .purchasePrice(500.0)
                .sellingPrice(700.0)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        productRequestDTO = new ProductRequestDTO(
                "Laptop",
                "SKU001",
                500.0,
                700.0,
                true,
                1L
        );
    }

    @Test
    void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(page);
        when(productMapper.toDTO(product)).thenReturn(new ProductResponseDTO(1L, "Electronics", "Laptop", "SKU001", 500, 700, true, LocalDateTime.now()));

        Page<ProductResponseDTO> result = productService.getAllProducts(0,5);

        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void testCreateProduct() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(new ProductResponseDTO(1L, "Electronics", "Laptop", "SKU001", 500, 700, true, LocalDateTime.now()));

        ProductResponseDTO dto = productService.createProduct(productRequestDTO);

        assertNotNull(dto);
        assertEquals("Laptop", dto.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testDeleteProductSuccess() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        boolean result = productService.deleteProduct(1L);

        assertTrue(result);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProductFail() {
        when(productRepository.existsById(2L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.deleteProduct(2L));
        assertEquals("Produit introuvable avec l'identifiant : 2", ex.getMessage());
    }
}
