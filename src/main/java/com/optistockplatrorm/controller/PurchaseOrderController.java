package com.optistockplatrorm.controller;

import com.optistockplatrorm.dto.*;
import com.optistockplatrorm.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<OptiResponse> createPurchaseOrder(@Valid @RequestBody PurchaseOrderRequestDTO dto) {
        PurchaseOrderResponseDTO purchase = purchaseOrderService.create(dto);
        OptiResponse optiResponse = OptiResponse.builder()
                .message("Order created successfully")
                .status(HttpStatus.CREATED.value())
                .data(purchase)
                .build();
        return new ResponseEntity<>(optiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<OptiResponse> getAllPurchaseOrders() {
        List<PurchaseOrderResponseDTO> orders = purchaseOrderService.getAll();
        OptiResponse optiResponse = OptiResponse.builder()
                .message("order found ")
                .status(HttpStatus.CREATED.value())
                .data(orders)
                .build();
        return new ResponseEntity<>(optiResponse, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<OptiResponse> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrderResponseDTO oder = purchaseOrderService.getById(id);
        OptiResponse optiResponse = OptiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Purchase order retrieved successfully")
                .data(oder)
                .build();
        return new ResponseEntity<>(optiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}/approve")
    public ResponseEntity<OptiResponse> approvePurchaseOrder(@PathVariable Long id) {
        PurchaseOrderResponseDTO oder = purchaseOrderService.approvePurchaseOrder(id);
        OptiResponse response = OptiResponse.builder()
                .message("Purchase Order canceled successfully!")
                .status(HttpStatus.OK.value())
                .data(oder)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<OptiResponse> receivePurchaseOrder(@PathVariable Long id , @Valid @RequestBody PurchaseOrderReceptionRequestDTO dto) {
        MovementInventoryResponseDTO inventoryMovement = purchaseOrderService.receiveProduct(id,dto) ;
        OptiResponse response = OptiResponse.builder()
                .message("Product received successfully")
                .status(HttpStatus.OK.value())
                .data(inventoryMovement)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }







//    @PutMapping("/{id}")
//    public  ResponseEntity<ApiResponse> updatePurchaseOrder(@PathVariable Long id,
//        @Valid @RequestBody PurchaseOrderRequestDTO dto) {
//        PurchaseOrderResponseDTO updatedOrder = purchaseOrderService.update(id,dto);
//        ApiResponse apiResponse = ApiResponse.builder()
//                .status(HttpStatus.OK.value())
//                .message("Purchase order updated successfully")
//                .data(updatedOrder)
//                .build();
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }


}
