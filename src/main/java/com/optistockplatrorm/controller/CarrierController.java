package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.OptiResponse;
import com.optistockplatrorm.dto.CarrierRequestDTO;
import com.optistockplatrorm.dto.CarrierResponseDTO;
import com.optistockplatrorm.service.CarrierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carriers")
public class CarrierController {

    @Autowired
    private CarrierService carrierService;

    @GetMapping
    public ResponseEntity<OptiResponse> getAllCarriers(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size) {

        Page<CarrierResponseDTO> carriersPage = carrierService.getAllCarriers(page, size);
        OptiResponse response = OptiResponse.builder().message("Liste paginée des transporteurs.")
                .data(carriersPage.getContent()).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> getCarrierById(@PathVariable Long id) {
        CarrierResponseDTO carrier = carrierService.getCarrierById(id);
        OptiResponse response = OptiResponse.builder().message("Transporteur trouvé.")
                .data(carrier).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OptiResponse> createCarrier(@RequestBody @Valid CarrierRequestDTO dto) {
        CarrierResponseDTO carrier = carrierService.createCarrier(dto);
        OptiResponse response = OptiResponse.builder().message("Transporteur créé avec succès !")
                .data(carrier).status(HttpStatus.CREATED.value()).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptiResponse> updateCarrier(@PathVariable Long id, @RequestBody CarrierRequestDTO dto) {
        CarrierResponseDTO carrier = carrierService.updateCarrier(id, dto);
        OptiResponse response = OptiResponse.builder().message("Transporteur mis à jour avec succès.")
                .data(carrier).status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptiResponse> deleteCarrier(@PathVariable Long id) {
        carrierService.deleteCarrier(id);
        OptiResponse response = OptiResponse.builder().message("Transporteur supprimé avec succès.")
                .status(HttpStatus.OK.value()).build();
        return ResponseEntity.ok(response);
    }
}