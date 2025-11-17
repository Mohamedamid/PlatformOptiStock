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
    private ProductResponseDTO productResponseDTO;

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

        // DTO for response mapping
        productResponseDTO = new ProductResponseDTO(1L, "Electronics", "Laptop", "SKU001", 500.0, 700.0, true, LocalDateTime.now());

        productRequestDTO = new ProductRequestDTO(
                "Laptop",
                "SKU001",
                500.0,
                700.0,
                true,
                1L
        );
    }

    // --- Tests for getAllProducts ---
    @Test
    void testGetAllProductsSuccess() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(List.of(product));

        when(productRepository.findAll(pageable)).thenReturn(page);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        Page<ProductResponseDTO> result = productService.getAllProducts(0,5);

        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).name());
        verify(verifyRole).checkAccess("READ");
        verify(productRepository).findAll(pageable);
    }

    // --- Tests for getProductById ---
    @Test
    void testGetProductByIdSuccess() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Laptop", result.name());
        verify(verifyRole).checkAccess("READ");
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
        assertEquals("Produit introuvable avec l'identifiant : 99", ex.getMessage());
        verify(verifyRole).checkAccess("READ");
    }

    // --- Tests for createProduct ---
    @Test
    void testCreateProductSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Use ArgumentCaptor to capture the Product object before saving
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        when(productRepository.save(productCaptor.capture())).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productResponseDTO);

        ProductResponseDTO dto = productService.createProduct(productRequestDTO);

        assertNotNull(dto);
        assertEquals("Laptop", dto.name());

        // Verify creation details
        Product capturedProduct = productCaptor.getValue();
        assertEquals("SKU001", capturedProduct.getSku());
        assertEquals(category, capturedProduct.getCategory());

        verify(verifyRole).checkAccess("CREATE");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateProductCategoryNotFound() {
        ProductRequestDTO invalidDTO = new ProductRequestDTO(
                "Monitor",
                "SKU002",
                100.0,
                150.0,
                true,
                99L // Invalid category ID
        );

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.createProduct(invalidDTO));
        assertEquals("Catégorie introuvable avec l'identifiant : 99", ex.getMessage());
        verify(verifyRole).checkAccess("CREATE");
        verify(productRepository, never()).save(any(Product.class)); // Ensure save is not called
    }

    // --- Tests for updateProduct ---
    @Test
    void testUpdateProductSuccess() {
        ProductRequestDTO updatedDTO = new ProductRequestDTO(
                "Laptop Updated",
                "SKU001_UPD",
                550.0,
                750.0,
                false, // set to inactive
                1L
        );

        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("IT Gear");

        ProductResponseDTO updatedResponseDTO = new ProductResponseDTO(1L, "IT Gear", "Laptop Updated", "SKU001_UPD", 550.0, 750.0, false, product.getCreatedAt());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Capture the Product object before saving to verify changes
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        when(productRepository.save(productCaptor.capture())).thenReturn(product);

        when(productMapper.toDTO(product)).thenReturn(updatedResponseDTO);

        ProductResponseDTO result = productService.updateProduct(1L, updatedDTO);

        assertNotNull(result);
        assertEquals("Laptop Updated", result.name());
        assertEquals("SKU001_UPD", result.sku());
        assertFalse(result.active());

        // Verify that the captured product was updated correctly
        Product capturedProduct = productCaptor.getValue();
        assertEquals("Laptop Updated", capturedProduct.getName());
        assertEquals(550.0, capturedProduct.getPurchasePrice());

        verify(verifyRole).checkAccess("UPDATE");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.updateProduct(99L, productRequestDTO));
        assertEquals("Produit introuvable avec l'identifiant : 99", ex.getMessage());
        verify(verifyRole).checkAccess("UPDATE");
        verify(categoryRepository, never()).findById(anyLong()); // Ensure category lookup isn't attempted
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProductCategoryNotFound() {
        ProductRequestDTO invalidCategoryDTO = new ProductRequestDTO(
                "Monitor", "SKU002", 100.0, 150.0, true, 99L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, invalidCategoryDTO));
        assertEquals("Catégorie introuvable avec l'identifiant : 99", ex.getMessage());
        verify(verifyRole).checkAccess("UPDATE");
        verify(productRepository, never()).save(any(Product.class)); // Ensure save is not called
    }

    // --- Tests for deleteProduct ---
    @Test
    void testDeleteProductSuccess() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        boolean result = productService.deleteProduct(1L);

        assertTrue(result);
        verify(verifyRole).checkAccess("DELETE");
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProductFail() {
        when(productRepository.existsById(2L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> productService.deleteProduct(2L));
        assertEquals("Produit introuvable avec l'identifiant : 2", ex.getMessage());
        verify(verifyRole).checkAccess("DELETE");
        verify(productRepository).existsById(2L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}