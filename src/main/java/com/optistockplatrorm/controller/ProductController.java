package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.dto.ProductRequestDTO;
import com.optistockplatrorm.dto.ProductResponseDTO;
import com.optistockplatrorm.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<OptiResponse> obtenirTousLesProduits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int taille) {

        Page<ProductResponseDTO> products = productService.getAllProducts(page, taille);

        OptiResponse response = OptiResponse.builder().message("Liste des produits récupérée avec succès.").data(products.getContent())
                .status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> obtenirProduitParId(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);

        if (product == null) {
            OptiResponse response = OptiResponse.builder().message("Aucun produit trouvé avec l’identifiant fourni.")
                    .status(HttpStatus.NOT_FOUND.value()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        OptiResponse response = OptiResponse.builder().message("Produit trouvé.").data(product)
                .status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OptiResponse> creerProduit(@Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO productCree = productService.createProduct(dto);

        OptiResponse response = OptiResponse.builder().message("Produit créé avec succès.").data(productCree)
                .status(HttpStatus.CREATED.value()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptiResponse> modifierProduit(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO dto) {
        ProductResponseDTO product = productService.updateProduct(id, dto);

        if (product == null) {
            OptiResponse response = OptiResponse.builder().message("Produit introuvable, mise à jour impossible.")
                    .status(HttpStatus.NOT_FOUND.value()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        OptiResponse response = OptiResponse.builder().message("Produit mis à jour avec succès.").data(product)
                .status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptiResponse> supprimerProduit(@PathVariable Long id) {
        boolean supprime = productService.deleteProduct(id);

        OptiResponse response = OptiResponse.builder().message(supprime ? "Produit supprimé avec succès." : "Produit introuvable.")
                .status(supprime ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value()).build();
        return new ResponseEntity<>(response, supprime ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}