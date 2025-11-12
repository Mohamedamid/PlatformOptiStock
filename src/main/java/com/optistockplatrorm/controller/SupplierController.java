package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.dto.SupplierRequestDTO;
import com.optistockplatrorm.dto.SupplierResponseDTO;
import com.optistockplatrorm.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<OptiResponse> create(@RequestBody @Valid SupplierRequestDTO dto) {
        SupplierResponseDTO supplier = supplierService.create(dto);
        OptiResponse response = OptiResponse.builder()
                .message("Le fournisseur a été créé avec succès.")
                .status(HttpStatus.CREATED.value())
                .data(supplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<OptiResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SupplierResponseDTO> suppliersPage = supplierService.getAll(page, size);

        OptiResponse response = OptiResponse.builder()
                .status(HttpStatus.OK.value())
                .data(suppliersPage.getContent())
                .message("Liste des fournisseurs récupérée avec succès.")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> getById(@PathVariable Long id) {
        SupplierResponseDTO supplier = supplierService.getById(id);
        OptiResponse response = OptiResponse.builder()
                .message("Fournisseur trouvé avec succès.")
                .status(HttpStatus.OK.value())
                .data(supplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptiResponse> update(@PathVariable Long id, @RequestBody @Valid SupplierRequestDTO dto) {
        SupplierResponseDTO supplier = supplierService.update(id, dto);
        OptiResponse response = OptiResponse.builder()
                .message("Les informations du fournisseur ont été mises à jour avec succès.")
                .status(HttpStatus.OK.value())
                .data(supplier)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptiResponse> delete(@PathVariable Long id) throws BadRequestException {
        SupplierResponseDTO deletedSupplier = supplierService.delete(id);
        OptiResponse response = OptiResponse.builder()
                .message("Le fournisseur a été supprimé avec succès.")
                .data(deletedSupplier)
                .status(HttpStatus.OK.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

