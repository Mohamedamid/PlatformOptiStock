package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.dto.WarehouseRequestDTO;
import com.optistockplatrorm.dto.WarehouseResponseDTO;
import com.optistockplatrorm.service.WareHouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/warehouses")
public class WareHouseController {

    @Autowired
    private WareHouseService warehouseService;

    @PostMapping
    public ResponseEntity<OptiResponse> createWarehouse(@Valid @RequestBody WarehouseRequestDTO request) {
        WarehouseResponseDTO created = warehouseService.create(request);
        OptiResponse response = OptiResponse.builder().message("Entrepôt créé avec succès !").data(created).status(HttpStatus.CREATED.value()).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<OptiResponse> getAllWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<WarehouseResponseDTO> list = warehouseService.getAll(page, size);

        OptiResponse response = OptiResponse.builder()
                .message("Liste paginée des produits.").data(list.getContent()).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> getWarehouseById(@PathVariable Long id) {
        WarehouseResponseDTO warehouse = warehouseService.getWarehouseById(id);
        if (warehouse == null) {
            OptiResponse notFound = OptiResponse.builder().message("Aucun entrepôt trouvé pour l’ID : " + id).status(HttpStatus.NOT_FOUND.value()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
        }
        OptiResponse response = OptiResponse.builder()
                .message("Entrepôt trouvé avec succès").data(warehouse).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptiResponse> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseRequestDTO request) {
        WarehouseResponseDTO updated = warehouseService.updateWarehouse(id, request);
        if (updated == null) {
            OptiResponse notFound = OptiResponse.builder().message("Impossible de mettre à jour : entrepôt introuvable.").status(HttpStatus.NOT_FOUND.value()).build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
        }
        OptiResponse response = OptiResponse.builder().message("Entrepôt mis à jour avec succès !").data(updated).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptiResponse> deleteWarehouse(@PathVariable Long id) {
        boolean deleted = warehouseService.deleteWarehouse(id);
        OptiResponse response = OptiResponse.builder()
                .message(deleted ? "Entrepôt supprimé avec succès." : "Suppression impossible : entrepôt lié à des mouvements, il a été désactivé.")
                .status(deleted ? HttpStatus.OK.value() : HttpStatus.ACCEPTED.value()).build();
        return ResponseEntity.status(deleted ? HttpStatus.OK : HttpStatus.ACCEPTED).body(response);
    }
}
