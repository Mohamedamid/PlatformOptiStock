package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.ApiResponse;
import com.optistockplatrorm.dto.ManagerRequestDTO;
import com.optistockplatrorm.dto.ManagerResponseDTO;
import com.optistockplatrorm.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/wareHouseManager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getManagerById(@PathVariable Long id) {
        ManagerResponseDTO manager = managerService.getManagerById(id);
        if (manager == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Gestionnaire introuvable.").status(HttpStatus.NOT_FOUND.value()).build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ApiResponse response = ApiResponse.builder()
                .message("Gestionnaire trouvé.").data(manager).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody ManagerRequestDTO dto) {
        ManagerResponseDTO warehouseManager = managerService.create(dto);
        ApiResponse response = ApiResponse.builder()
                .message("Gestionnaire d’entrepôt créé avec succès !").data(warehouseManager).status(HttpStatus.CREATED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable long id, @Valid @RequestBody ManagerRequestDTO dto) {
        ManagerResponseDTO manager = managerService.updateManager(id, dto);
        if (manager == null) {
            ApiResponse response = ApiResponse.builder()
                    .message("Gestionnaire introuvable.").status(HttpStatus.NOT_FOUND.value()).build();
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ApiResponse response = ApiResponse.builder()
                .message("Gestionnaire mis à jour avec succès !").data(manager).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteManager(@PathVariable Long id) {
        boolean deleted = managerService.deleteManager(id);
        ApiResponse response = ApiResponse.builder()
                .message(deleted ? "Gestionnaire supprimé avec succès !" : "Gestionnaire introuvable.")
                .status(deleted ? HttpStatus.OK.value() : HttpStatus.NOT_FOUND.value()).build();
        return new ResponseEntity<>(response, deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
