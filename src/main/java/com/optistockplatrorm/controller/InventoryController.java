package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.InventoryResponseDTO;
import com.optistockplatrorm.dto.InventoryRequestDTO;
import com.optistockplatrorm.dto.MovementInventoryResponseDTO;
import com.optistockplatrorm.dto.MovementInventoryRequestDTO;
import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<OptiResponse> getAllInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<InventoryResponseDTO> inventoryPage = inventoryService.getAllInventory(page, size);

        OptiResponse response = OptiResponse.builder()
                .message("Liste paginée des inventaires récupérée avec succès.")
                .data(inventoryPage.getContent())
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> getInventoryById(@PathVariable Long id) {
        InventoryResponseDTO inventory = inventoryService.getInventoryById(id);

        OptiResponse response = OptiResponse.builder()
                .message("Inventaire récupéré avec succès.")
                .data(inventory)
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OptiResponse> createInventory(@Valid @RequestBody InventoryRequestDTO dto) {
        InventoryResponseDTO createdInventory = inventoryService.createInventory(dto);

        OptiResponse response = OptiResponse.builder()
                .message("Inventaire créé avec succès.")
                .data(createdInventory)
                .status(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptiResponse> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequestDTO dto) {

        inventoryService.updateInventory(id, dto);

        OptiResponse response = OptiResponse.builder()
                .message("Inventaire mis à jour avec succès.")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptiResponse> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);

        OptiResponse response = OptiResponse.builder()
                .message("Inventaire supprimé avec succès.")
                .status(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/entree")
    public ResponseEntity<OptiResponse> recordInbound(@Valid @RequestBody MovementInventoryRequestDTO dto) {
        MovementInventoryResponseDTO movement = inventoryService.enregistrerEntree(dto);

        OptiResponse response = OptiResponse.builder()
                .message("Entrée d’inventaire enregistrée avec succès.")
                .data(movement)
                .status(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sortie")
    public ResponseEntity<OptiResponse> recordOutbound(@Valid @RequestBody MovementInventoryRequestDTO dto) {
        MovementInventoryResponseDTO movement = inventoryService.enregistrerSortie(dto);

        OptiResponse response = OptiResponse.builder()
                .message("Sortie d’inventaire enregistrée avec succès.")
                .data(movement)
                .status(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/adjustment")
    public ResponseEntity<OptiResponse> recordAdjustment(@Valid @RequestBody MovementInventoryRequestDTO dto) {
        MovementInventoryResponseDTO movement = inventoryService.enregistrerAjustement(dto);

        OptiResponse response = OptiResponse.builder()
                .message("Ajustement d’inventaire enregistré avec succès.")
                .data(movement)
                .status(HttpStatus.CREATED.value())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
