package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.CategoryRequestDTO;
import com.optistockplatrorm.dto.CategoryResponseDTO;
import com.optistockplatrorm.entity.Category;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.mapper.CategoryMapper;
import com.optistockplatrorm.repository.CategoryRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequestDTO categoryRequestDTO;
    private CategoryResponseDTO categoryResponseDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices")
                .active(true)
                .build();

        categoryRequestDTO = new CategoryRequestDTO("Electronics", "Electronic devices", true);
        categoryResponseDTO = new CategoryResponseDTO(1L, "Electronics", "Electronic devices", true);
    }

    // =======================================================
    // ======== SECTION 1: CORE CRUD TESTS (Access Granted) ===
    // =======================================================

    private void mockAdminAccess() {
        when(session.getAttribute("role")).thenReturn(Role.ADMIN);
    }

    @Test
    void testGetAllCategorySuccess() {
        mockAdminAccess();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDTO);

        Page<CategoryResponseDTO> result = categoryService.getAllCategory(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void testGetCategoryByIdSuccess() {
        mockAdminAccess();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Electronics", result.name());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testGetCategoryByIdNotFound() {
        mockAdminAccess();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.getCategoryById(99L));
        assertEquals("Catégorie introuvable avec l'identifiant : 99", ex.getMessage());
    }

    @Test
    void testCreateCategorySuccess() {
        mockAdminAccess();

        when(categoryMapper.toEntity(categoryRequestDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryResponseDTO);

        CategoryResponseDTO result = categoryService.createCategory(categoryRequestDTO);

        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(categoryRepository).save(category);
    }

    @Test
    void testUpdateCategorySuccess() {
        mockAdminAccess();
        CategoryRequestDTO updatedDTO = new CategoryRequestDTO("Updated Name", "New Desc", false);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(new CategoryResponseDTO(1L, "Updated Name", "New Desc", false));

        CategoryResponseDTO result = categoryService.updateCategory(1L, updatedDTO);

        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertFalse(result.active());
        verify(categoryRepository).save(category);
    }

    @Test
    void testUpdateCategoryNotFound() {
        mockAdminAccess();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.updateCategory(99L, categoryRequestDTO));
        assertEquals("Catégorie introuvable avec l'identifiant : 99", ex.getMessage());
    }

    @Test
    void testDeleteCategorySuccess() {
        mockAdminAccess();

        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteCategoryNotFound() {
        mockAdminAccess();

        when(categoryRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.deleteCategory(99L));
        assertEquals("Catégorie introuvable avec l'identifiant : 99", ex.getMessage());
    }

    // =======================================================
    // ======== SECTION 2: ACCESS CONTROL TESTS (checkAccess) ==
    // =======================================================

    private void mockRole(Role role) {
        when(session.getAttribute("role")).thenReturn(role);
    }

    @Nested
    class AccessControlTests {

        // --- Global Access Check ---
        @Test
        void testAccessNotLoggedIn() {
            when(session.getAttribute("role")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> categoryService.getAllCategory(0, 1)); // Test READ action
            assertEquals("Utilisateur non connecté.", ex.getMessage());
        }

        // --- READ Access Tests ---
        @Test
        void testReadAccessGrantedForAdmin() {
            mockRole(Role.ADMIN);
            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
            assertDoesNotThrow(() -> categoryService.getAllCategory(0, 1));
        }

        @Test
        void testReadAccessGrantedForWarehouseManager() {
            mockRole(Role.WAREHOUSE_MANAGER);
            when(categoryRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
            assertDoesNotThrow(() -> categoryService.getAllCategory(0, 1));
        }

        @Test
        void testReadAccessDeniedForUser() {
            mockRole(Role.CLIENT);
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> categoryService.getAllCategory(0, 1));
            assertTrue(ex.getMessage().contains("Accès refusé"));
        }

        // --- CREATE Access Tests ---
        @Test
        void testCreateAccessGrantedForAdmin() {
            mockRole(Role.ADMIN);
            when(categoryMapper.toEntity(any())).thenReturn(category);
            when(categoryRepository.save(any())).thenReturn(category);
            when(categoryMapper.toDto(any())).thenReturn(categoryResponseDTO);
            assertDoesNotThrow(() -> categoryService.createCategory(categoryRequestDTO));
        }

        @Test
        void testCreateAccessGrantedForWarehouseManager() {
            mockRole(Role.WAREHOUSE_MANAGER);
            when(categoryMapper.toEntity(any())).thenReturn(category);
            when(categoryRepository.save(any())).thenReturn(category);
            when(categoryMapper.toDto(any())).thenReturn(categoryResponseDTO);
            assertDoesNotThrow(() -> categoryService.createCategory(categoryRequestDTO));
        }

        @Test
        void testCreateAccessDeniedForUser() {
            mockRole(Role.CLIENT);
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> categoryService.createCategory(categoryRequestDTO));
            assertTrue(ex.getMessage().contains("Accès refusé"));
        }

        // --- UPDATE Access Tests ---
        @Test
        void testUpdateAccessGrantedForAdmin() {
            mockRole(Role.ADMIN);
            when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any())).thenReturn(category);
            when(categoryMapper.toDto(any())).thenReturn(categoryResponseDTO);
            assertDoesNotThrow(() -> categoryService.updateCategory(1L, categoryRequestDTO));
        }

        @Test
        void testUpdateAccessDeniedForWarehouseManager() {
            mockRole(Role.WAREHOUSE_MANAGER);
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> categoryService.updateCategory(1L, categoryRequestDTO));
            assertTrue(ex.getMessage().contains("seul un administrateur peut modifier"));
        }

        // --- DELETE Access Tests ---
        @Test
        void testDeleteAccessGrantedForAdmin() {
            mockRole(Role.ADMIN);
            when(categoryRepository.existsById(anyLong())).thenReturn(true);
            assertDoesNotThrow(() -> categoryService.deleteCategory(1L));
        }

        @Test
        void testDeleteAccessDeniedForWarehouseManager() {
            mockRole(Role.WAREHOUSE_MANAGER);
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> categoryService.deleteCategory(1L));
            assertTrue(ex.getMessage().contains("seul un administrateur peut supprimer"));
        }

        // --- FINAL TEST FOR 100% COVERAGE (Default Case) ---
        @Test
        void testCheckAccessDefaultAction() throws NoSuchMethodException {
            // Mock a valid role so we pass the initial null check
            when(session.getAttribute("role")).thenReturn(Role.ADMIN);

            // Use Reflection to access the private checkAccess method
            Method checkAccess = CategoryService.class.getDeclaredMethod("checkAccess", String.class);
            checkAccess.setAccessible(true); // Make the private method accessible

            // Call the method with an unauthorized/unknown action
            InvocationTargetException ex = assertThrows(InvocationTargetException.class,
                    () -> checkAccess.invoke(categoryService, "UNKNOWN_ACTION"));

            // The underlying exception should be RuntimeException from the default case
            Throwable targetException = ex.getTargetException();
            assertTrue(targetException instanceof RuntimeException);
            assertEquals("Action non autorisée.", targetException.getMessage());
        }
    }
}