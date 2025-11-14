package com.optistockplatrorm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.dto.ProductRequestDTO;
import com.optistockplatrorm.dto.ProductResponseDTO;
import com.optistockplatrorm.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testObtenirTousLesProduits() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO(1L, "Electronics", "Laptop", "SKU001", 500, 700, true, LocalDateTime.now());
        Mockito.when(productService.getAllProducts(anyInt(), anyInt())).thenReturn(new PageImpl<>(List.of(product)));

        mockMvc.perform(get("/api/admin/products")
                        .param("page","0")
                        .param("taille","5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Laptop"));
    }

    @Test
    void testCreerProduit() throws Exception {
        ProductRequestDTO requestDTO = new ProductRequestDTO("Laptop","SKU001",500,700,true,1L);
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Electronics", "Laptop", "SKU001", 500, 700, true, LocalDateTime.now());

        Mockito.when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Laptop"))
                .andExpect(jsonPath("$.message").value("Produit créé avec succès."));
    }
}
