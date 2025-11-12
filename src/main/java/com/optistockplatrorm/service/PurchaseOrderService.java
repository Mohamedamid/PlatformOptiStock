package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.MovementInventoryResponseDTO;
import com.optistockplatrorm.dto.PurchaseOrderRequestDTO;
import com.optistockplatrorm.dto.PurchaseOrderResponseDTO;
import com.optistockplatrorm.dto.PurchaseOrderReceptionRequestDTO;
import com.optistockplatrorm.entity.*;
import com.optistockplatrorm.entity.Enums.MovementType;
import com.optistockplatrorm.entity.Enums.PurchaseOrderStatus;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.mapper.MovementInventoryMapper;
import com.optistockplatrorm.mapper.PurchaseOrderMapper;
import com.optistockplatrorm.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final MovementInventoryMapper MovementInventoryMapper;
    private final ProductRepository productRepository;
    private final WareHouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;
    private final MovementInventoryRepository MovementInventoryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderResponseDTO create(PurchaseOrderRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.supplierId())
                .orElseThrow(() -> new GestionException("Fournisseur introuvable avec l’identifiant : " + dto.supplierId()));

        Warehouse warehouse = warehouseRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new GestionException("Entrepôt introuvable avec l’identifiant : " + dto.warehouseId()));

        PurchaseOrder order = PurchaseOrder.builder()
                .supplier(supplier).warehouse(warehouse).orderStatus(PurchaseOrderStatus.CREATED)
                .expectedDate(dto.expectedDate()).orderDate(LocalDateTime.now()).build();

        List<PurchaseOrderLine> lines = dto.liens().stream().map(line -> {
            Product product = productRepository.findById(line.productId())
                    .filter(Product::getActive)
                    .orElseThrow(() -> new GestionException("Produit introuvable avec l’identifiant : " + line.productId()));
            return PurchaseOrderLine.builder()
                    .product(product).unitPrice(product.getPurchasePrice()).quantity(line.quantity())
                    .purchaseOrder(order).build();
        }).toList();

        order.setOrderLines(lines);
        return purchaseOrderMapper.toDto(purchaseOrderRepository.save(order));
    }

    public List<PurchaseOrderResponseDTO> getAll() {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();
        return orders.stream().map(purchaseOrderMapper::toDto).toList();
    }

    public PurchaseOrderResponseDTO getById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new GestionException("Commande d’achat introuvable avec l’identifiant fourni."));
        return purchaseOrderMapper.toDto(purchaseOrder);
    }

    public PurchaseOrderResponseDTO approvePurchaseOrder(long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new GestionException("Commande d’achat introuvable avec l’identifiant fourni."));

        order.setOrderStatus(PurchaseOrderStatus.APPROVED);
        purchaseOrderRepository.save(order);
        return purchaseOrderMapper.toDto(order);
    }

    @Transactional
    public MovementInventoryResponseDTO receiveProduct(Long purchaseOrderId, PurchaseOrderReceptionRequestDTO dto) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new GestionException("Commande d’achat introuvable avec l’identifiant fourni."));

        PurchaseOrderLine line = po.getOrderLines().stream()
                .filter(l -> l.getProduct().getId().equals(dto.productId())).findFirst()
                .orElseThrow(() -> new GestionException("Produit introuvable dans la commande."));

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(line.getProduct().getId(), po.getWarehouse().getId())
                .orElseThrow(() -> new GestionException("Aucun inventaire trouvé pour ce produit dans l’entrepôt."));

        InventoryMovement inventoryMovement = InventoryMovement.builder()
                .movementType(MovementType.INBOUND).inventory(inventory).quantity(dto.receivedQty())
                .createdAt(LocalDateTime.now()).build();

        MovementInventoryRepository.save(inventoryMovement);

        inventory.setQuantityOnHand(inventory.getQuantityOnHand() + dto.receivedQty());
        inventoryRepository.save(inventory);

        int totalOrdered = po.getOrderLines().stream().mapToInt(PurchaseOrderLine::getQuantity).sum();
        int totalReceived = MovementInventoryRepository.calculerQuantiteTotaleReçue(
                line.getProduct().getId(), po.getWarehouse().getId(), po.getId(), MovementType.INBOUND
        );

        po.setOrderStatus(totalReceived >= totalOrdered ? PurchaseOrderStatus.RECEIVED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
        purchaseOrderRepository.save(po);
        return MovementInventoryMapper.toDTO(inventoryMovement);
    }
}