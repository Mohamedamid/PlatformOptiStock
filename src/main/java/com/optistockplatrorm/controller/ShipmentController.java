package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.ApiResponse;
import com.optistockplatrorm.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @PutMapping("/assignCarrier")
    public ResponseEntity<ApiResponse> assignCarrier(@PathVariable Long id, @RequestParam Long carrierId) {
        shipmentService.assignCarrier(id, carrierId);
        ApiResponse response = ApiResponse.builder().message("Transporteur assigné à l'expédition avec succès").status(HttpStatus.OK.value()).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}