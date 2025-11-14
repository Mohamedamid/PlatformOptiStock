package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.OrderRequestDTO;
import com.optistockplatrorm.dto.OrderResponseDTO;
import com.optistockplatrorm.dto.WarehouseInventoryInfo;
import com.optistockplatrorm.entity.*;
import com.optistockplatrorm.entity.Enums.*;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.mapper.SalesOrderLineMapper;
import com.optistockplatrorm.mapper.SalesOrderMapper;
import com.optistockplatrorm.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final ClientRepository clientRepository;
    private final WareHouseRepository wareHouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesOrderLineRepository salesOrderLineRepository;
    private final ShipmentRepository shipmentRepository;
    private final MovementInventoryRepository movementInventoryRepository;
    private final InventoryService inventoryService;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderLineMapper salesOrderLineMapper;

    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto) {
        Client client = clientRepository.findById(dto.clientId())
                .orElseThrow(() -> new GestionException("Client introuvable."));

        SalesOrder order = SalesOrder.builder()
                .client(client)
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        List<SalesOrderLine> lines = dto.lines().stream()
                .map(lineDto -> {
                    Product product = productRepository.findById(lineDto.productId())
                            .filter(Product::getActive)
                            .orElseThrow(() -> new GestionException("Produit introuvable ou inactif (ID: " + lineDto.productId() + ")."));

                    return SalesOrderLine.builder()
                            .product(product)
                            .price(product.getSellingPrice())
                            .quantityRequested(lineDto.quantityRequested())
                            .quantityReserved(0)
                            .quantityBackorder(0)
                            .status(OrderLineStatus.NOT_RESERVED)
                            .salesOrder(order)
                            .build();
                }).toList();

        order.setOrderLines(lines);
        SalesOrder savedOrder = salesOrderRepository.save(order);

        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getClient().getId(),
                savedOrder.getOrderStatus(),
                salesOrderLineMapper.toDTOs(savedOrder.getOrderLines()),
                "Order created successfully"
        );
    }

    @Transactional
    public OrderResponseDTO reserveOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new GestionException("Commande introuvable."));

        reserveLines(order);

        String message = order.getOrderLines().stream()
                .map(line -> String.format("%s : %d/%d réservés, %d en reliquat",
                        line.getProduct().getName(),
                        line.getQuantityReserved(),
                        line.getQuantityRequested(),
                        line.getQuantityBackorder()))
                .collect(Collectors.joining("; "));

        OrderResponseDTO response = salesOrderMapper.toDTO(order);
        return new OrderResponseDTO(
                response.id(),
                response.clientId(),
                response.orderStatus(),
                response.orderLines(),
                message
        );
    }

    @Transactional
    public OrderResponseDTO confirmOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new GestionException("Commande introuvable."));

        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Impossible de confirmer la commande à ce stade.");
        }

        order.setOrderStatus(OrderStatus.RESERVED);
        order.setConfirmedAt(LocalDateTime.now());
        salesOrderRepository.save(order);

        Shipment shipment = Shipment.builder()
                .salesOrder(order)
                .trackingNumber("TRK-" + order.getId())
                .shipmentStatus(ShipmentStatus.PLANNED)
                .plannedDate(LocalDateTime.now().plusDays(1))
                .build();
        shipmentRepository.save(shipment);

        return salesOrderMapper.toDTO(order);
    }

    @Transactional
    public OrderResponseDTO cancel(long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new GestionException("Commande introuvable."));

        if (order.getOrderStatus() == OrderStatus.SHIPPED) {
            throw new GestionException("Impossible d'annuler une commande déjà expédiée.");
        }

        if (order.getOrderStatus() == OrderStatus.CREATED) {
            order.setOrderStatus(OrderStatus.CANCELED);
        } else if (order.getOrderStatus() == OrderStatus.RESERVED) {
            order.getOrderLines().forEach(line -> {
                if (order.getWarehouse() != null) {
                    Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(
                                    line.getProduct().getId(), order.getWarehouse().getId())
                            .orElseThrow(() -> new GestionException("Inventaire introuvable pour le produit."));

                    int reservedQty = line.getQuantityReserved() != null ? line.getQuantityReserved() : 0;
                    int currentReserved = inventory.getQuantityReserved() != null ? inventory.getQuantityReserved() : 0;

                    inventory.setQuantityReserved(Math.max(0, currentReserved - reservedQty));
                    inventoryRepository.save(inventory);
                }
            });
            order.setOrderStatus(OrderStatus.CANCELED);
        }

        return salesOrderMapper.toDTO(salesOrderRepository.save(order));
    }

    @Transactional
    public OrderResponseDTO getOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
                .orElseThrow(() -> new GestionException("Commande introuvable."));
        return salesOrderMapper.toDTO(order);
    }

    // ========== METHODS PRIVÉES ==========

    private void reserveLines(SalesOrder order) {
        for (SalesOrderLine line : order.getOrderLines()) {
            long warehouseId = consolidateQuantityInOneWarehouse(line.getQuantityRequested(), line.getProduct().getId());

            if (warehouseId == -1L) {
                line.setQuantityReserved(0);
                line.setQuantityBackorder(line.getQuantityRequested());
                line.setStatus(OrderLineStatus.NOT_RESERVED);
                continue;
            }

            inventoryService.reserverQuantite(line, warehouseId);

            if (order.getWarehouse() == null) {
                Warehouse warehouse = wareHouseRepository.findById(warehouseId)
                        .orElseThrow(() -> new GestionException("Entrepôt introuvable."));
                order.setWarehouse(warehouse);
            }
        }

        if (order.getOrderLines().stream().allMatch(l -> l.getStatus() == OrderLineStatus.RESERVED)) {
            order.setOrderStatus(OrderStatus.RESERVED);
        } else if (order.getOrderLines().stream().anyMatch(l -> l.getQuantityReserved() > 0)) {
            order.setOrderStatus(OrderStatus.RESERVED);
        } else {
            order.setOrderStatus(OrderStatus.CREATED);
        }
    }

    @Transactional
    public long consolidateQuantityInOneWarehouse(int quantityRequested, long productId) {
        List<WarehouseInventoryInfo> warehouses = wareHouseRepository.findWareHouse(productId);

        int totalAvailable = warehouses.stream()
                .mapToInt(WarehouseInventoryInfo::quantityHand)
                .sum();

        if (totalAvailable < quantityRequested) {
            return -1L;
        }

        WarehouseInventoryInfo mainWarehouse = warehouses.stream()
                .max(Comparator.comparingInt(WarehouseInventoryInfo::quantityHand))
                .orElseThrow();

        Inventory mainInventory = inventoryRepository.findById(mainWarehouse.inventoryId())
                .orElseThrow(() -> new GestionException("Inventaire principal introuvable."));

        int remainingToTransfer = quantityRequested - mainWarehouse.quantityHand();

        if (remainingToTransfer > 0) {
            for (WarehouseInventoryInfo w : warehouses) {
                if (Objects.equals(w.warehouseId(), mainWarehouse.warehouseId())) continue;

                int transferable = Math.min(w.quantityHand(), remainingToTransfer);
                Inventory sourceInventory = inventoryRepository.findById(w.inventoryId())
                        .orElseThrow(() -> new GestionException("Inventaire source introuvable."));

                mainInventory.setQuantityOnHand(mainInventory.getQuantityOnHand() + transferable);
                sourceInventory.setQuantityOnHand(sourceInventory.getQuantityOnHand() - transferable);

                movementInventoryRepository.save(InventoryMovement.builder()
                        .quantity(transferable)
                        .inventory(mainInventory)
                        .movementType(MovementType.INBOUND)
                        .description("Transfert entrant depuis l'entrepôt " + w.warehouseId())
                        .createdAt(LocalDateTime.now())
                        .build());

                movementInventoryRepository.save(InventoryMovement.builder()
                        .quantity(transferable)
                        .inventory(sourceInventory)
                        .movementType(MovementType.OUTBOUND)
                        .description("Transfert sortant vers l'entrepôt " + mainWarehouse.warehouseId())
                        .createdAt(LocalDateTime.now())
                        .build());

                inventoryRepository.save(sourceInventory);
                remainingToTransfer -= transferable;
                if (remainingToTransfer <= 0) break;
            }
        }

        inventoryRepository.save(mainInventory);
        return mainWarehouse.warehouseId();
    }
}