package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.MovementInventoryResponseDTO;
import com.optistockplatrorm.dto.PurchaseOrderRequestDTO;
import com.optistockplatrorm.dto.PurchaseOrderResponseDTO;
import com.optistockplatrorm.dto.PurchaseOrderReceptionRequestDTO;
import com.optistockplatrorm.dto.PurchaseOrderLineRequestDTO;
import com.optistockplatrorm.dto.PurchaseOrderLineRsponseDTO; // Added new DTO
import com.optistockplatrorm.entity.*;
import com.optistockplatrorm.entity.Enums.MovementType;
import com.optistockplatrorm.entity.Enums.PurchaseOrderStatus;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.mapper.MovementInventoryMapper;
import com.optistockplatrorm.mapper.PurchaseOrderMapper;
import com.optistockplatrorm.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock private PurchaseOrderMapper purchaseOrderMapper;
    @Mock private MovementInventoryMapper MovementInventoryMapper;
    @Mock private ProductRepository productRepository;
    @Mock private WareHouseRepository warehouseRepository;
    @Mock private SupplierRepository supplierRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private MovementInventoryRepository MovementInventoryRepository;
    @Mock private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private Supplier supplier;
    private Warehouse warehouse;
    private Product product;
    private Inventory inventory;
    private PurchaseOrder purchaseOrder;
    private PurchaseOrderRequestDTO purchaseOrderRequestDTO;
    private PurchaseOrderResponseDTO purchaseOrderResponseDTO;
    private PurchaseOrderReceptionRequestDTO receptionDTO;
    private MovementInventoryResponseDTO movementResponseDTO;

    private final LocalDateTime orderTime = LocalDateTime.now();
    private final LocalDateTime expectedTime = LocalDateTime.now().plusDays(5);

    @BeforeEach
    void setup() {
        // Entities Setup
        supplier = Supplier.builder().id(5L).supplierName("Supplier A").build();
        warehouse = Warehouse.builder().id(10L).name("Main WH").build();
        product = Product.builder().id(100L).name("P1").active(true).purchasePrice(50.0).build();

        inventory = Inventory.builder()
                .id(1000L).product(product).warehouse(warehouse).quantityOnHand(10).quantityReserved(0).build();

        PurchaseOrderLine line1 = PurchaseOrderLine.builder()
                .id(1L).product(product).quantity(50).unitPrice(50.0).build();

        purchaseOrder = PurchaseOrder.builder()
                .id(1L).supplier(supplier).warehouse(warehouse).orderStatus(PurchaseOrderStatus.CREATED)
                .orderLines(List.of(line1)).orderDate(orderTime).expectedDate(expectedTime).build();
        line1.setPurchaseOrder(purchaseOrder);

        // DTO Setup
        PurchaseOrderLineRequestDTO lineDTO = new PurchaseOrderLineRequestDTO(50, 100L);
        purchaseOrderRequestDTO = new PurchaseOrderRequestDTO(
                5L, 10L, expectedTime, List.of(lineDTO)
        );

        // ** التعديل ليتناسب مع الهيكل الجديد بـ 7 حقول **
        purchaseOrderResponseDTO = new PurchaseOrderResponseDTO(
                1L,
                5L,
                10L,
                PurchaseOrderStatus.CREATED.name(),
                expectedTime,
                orderTime,
                Collections.emptyList()
        );

        receptionDTO = new PurchaseOrderReceptionRequestDTO(100L, 20); // Receive 20 units of P1

        movementResponseDTO = new MovementInventoryResponseDTO(
                2000L, 20, 1000L, MovementType.INBOUND, LocalDateTime.now());
    }

    // --- 1. create Tests ---

    @Test
    void testCreatePurchaseOrderSuccess() {
        when(supplierRepository.findById(5L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(purchaseOrder);
        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(purchaseOrderResponseDTO);

        PurchaseOrderResponseDTO result = purchaseOrderService.create(purchaseOrderRequestDTO);

        assertNotNull(result);
        assertEquals(PurchaseOrderStatus.CREATED.name(), result.orderStatus());
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
    }

    @Test
    void testCreatePurchaseOrderSupplierNotFound() {
        when(supplierRepository.findById(5L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.create(purchaseOrderRequestDTO));
        assertEquals("Fournisseur introuvable avec l’identifiant : 5", ex.getMessage());
    }

    @Test
    void testCreatePurchaseOrderWarehouseNotFound() {
        when(supplierRepository.findById(5L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(10L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.create(purchaseOrderRequestDTO));
        assertEquals("Entrepôt introuvable avec l’identifiant : 10", ex.getMessage());
    }

    @Test
    void testCreatePurchaseOrderProductInactive() {
        product.setActive(false);
        when(supplierRepository.findById(5L)).thenReturn(Optional.of(supplier));
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.create(purchaseOrderRequestDTO));
        assertEquals("Produit introuvable avec l’identifiant : 100", ex.getMessage());
    }

    // --- 2. Get All/By ID Tests ---

    @Test
    void testGetAllPurchaseOrders() {
        when(purchaseOrderRepository.findAll()).thenReturn(List.of(purchaseOrder));
        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(purchaseOrderResponseDTO);

        List<PurchaseOrderResponseDTO> result = purchaseOrderService.getAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(purchaseOrderRepository).findAll();
    }

    @Test
    void testGetPurchaseOrderByIdSuccess() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(purchaseOrderResponseDTO);

        PurchaseOrderResponseDTO result = purchaseOrderService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void testGetPurchaseOrderByIdNotFound() {
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.getById(99L));
        assertEquals("Commande d’achat introuvable avec l’identifiant fourni.", ex.getMessage());
    }

    // --- 3. approvePurchaseOrder Tests ---

    @Test
    void testApprovePurchaseOrderSuccess() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(purchaseOrderRepository.save(purchaseOrder)).thenReturn(purchaseOrder);

        // ** DTO الموافق للحالة APPROVED **
        PurchaseOrderResponseDTO approvedDTO = new PurchaseOrderResponseDTO(
                1L, 5L, 10L, PurchaseOrderStatus.APPROVED.name(), expectedTime, orderTime, Collections.emptyList()
        );
        when(purchaseOrderMapper.toDto(purchaseOrder)).thenReturn(approvedDTO);

        purchaseOrderService.approvePurchaseOrder(1L);

        assertEquals(PurchaseOrderStatus.APPROVED, purchaseOrder.getOrderStatus());
        verify(purchaseOrderRepository).save(purchaseOrder);
    }

    @Test
    void testApprovePurchaseOrderNotFound() {
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.approvePurchaseOrder(99L));
        assertEquals("Commande d’achat introuvable avec l’identifiant fourni.", ex.getMessage());
    }

    // --- 4. receiveProduct Tests (Transactional Logic) ---

    // Case 4a: Partial Receipt (20 received / 50 ordered)
    @Test
    void testReceiveProductPartial() {
        // Setup initial QOH=10
        int initialQOH = inventory.getQuantityOnHand();

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(inventoryRepository.findByProductIdAndWarehouseId(100L, 10L)).thenReturn(Optional.of(inventory));

        // Mock calculation: 0 previously received + 20 now = 20 total
        when(MovementInventoryRepository.calculerQuantiteTotaleReçue(
                100L, 10L, 1L, MovementType.INBOUND)).thenReturn(20);

        // Mocks for saving and mapping
        when(MovementInventoryRepository.save(any(InventoryMovement.class))).thenReturn(new InventoryMovement());
        when(MovementInventoryMapper.toDTO(any(InventoryMovement.class))).thenReturn(movementResponseDTO);

        purchaseOrderService.receiveProduct(1L, receptionDTO); // Receive 20

        // Verification of Inventory Update
        assertEquals(initialQOH + 20, inventory.getQuantityOnHand()); // 10 + 20 = 30
        verify(inventoryRepository).save(inventory);

        // Verification of PO Status Update
        assertEquals(PurchaseOrderStatus.PARTIALLY_RECEIVED, purchaseOrder.getOrderStatus());
        verify(purchaseOrderRepository).save(purchaseOrder);

        // Verification of Movement Creation
        verify(MovementInventoryRepository).save(any(InventoryMovement.class));
    }

    // Case 4b: Full Receipt (50 ordered, total 50 received)
    @Test
    void testReceiveProductFull() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));
        when(inventoryRepository.findByProductIdAndWarehouseId(100L, 10L)).thenReturn(Optional.of(inventory));

        // Mock calculation: 30 previously received + 20 now = 50 total (50 ordered)
        when(MovementInventoryRepository.calculerQuantiteTotaleReçue(
                100L, 10L, 1L, MovementType.INBOUND)).thenReturn(50);

        // Mocks for saving and mapping
        when(MovementInventoryRepository.save(any(InventoryMovement.class))).thenReturn(new InventoryMovement());
        when(MovementInventoryMapper.toDTO(any(InventoryMovement.class))).thenReturn(movementResponseDTO);

        purchaseOrderService.receiveProduct(1L, receptionDTO); // Receive 20

        // Verification of PO Status Update
        assertEquals(PurchaseOrderStatus.RECEIVED, purchaseOrder.getOrderStatus());
        verify(purchaseOrderRepository).save(purchaseOrder);
    }

    // Case 4c: Product Not Found in Order Lines
    @Test
    void testReceiveProductLineNotFound() {
        PurchaseOrderReceptionRequestDTO invalidDTO = new PurchaseOrderReceptionRequestDTO(999L, 20); // Invalid Product ID

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.receiveProduct(1L, invalidDTO));
        assertEquals("Produit introuvable dans la commande.", ex.getMessage());
        verify(inventoryRepository, never()).findByProductIdAndWarehouseId(anyLong(), anyLong());
    }

    // Case 4d: Inventory Not Found
    @Test
    void testReceiveProductInventoryNotFound() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(purchaseOrder));

        // Product Line is found, but Inventory is missing
        when(inventoryRepository.findByProductIdAndWarehouseId(100L, 10L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.receiveProduct(1L, receptionDTO));
        assertEquals("Aucun inventaire trouvé pour ce produit dans l’entrepôt.", ex.getMessage());
        verify(MovementInventoryRepository, never()).save(any(InventoryMovement.class));
    }

    // Case 4e: Purchase Order Not Found
    @Test
    void testReceiveProductOrderNotFound() {
        when(purchaseOrderRepository.findById(99L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> purchaseOrderService.receiveProduct(99L, receptionDTO));
        assertEquals("Commande d’achat introuvable avec l’identifiant fourni.", ex.getMessage());
    }
}