package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.*;
import com.optistockplatrorm.entity.*;
import com.optistockplatrorm.entity.Enums.*;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.mapper.SalesOrderLineMapper;
import com.optistockplatrorm.mapper.SalesOrderMapper;
import com.optistockplatrorm.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private SalesOrderRepository salesOrderRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private WareHouseRepository wareHouseRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private ShipmentRepository shipmentRepository;
    @Mock private MovementInventoryRepository movementInventoryRepository;
    @Mock private InventoryService inventoryService;
    @Mock private SalesOrderMapper salesOrderMapper;
    @Mock private SalesOrderLineMapper salesOrderLineMapper;

    @InjectMocks
    private OrderService orderService;

    private Client client;
    private Product product1;
    private Product product2;
    private Warehouse warehouseMain;
    private Warehouse warehouseSecondary;
    private Inventory inventoryMain;
    private Inventory inventorySecondary;
    private SalesOrder order;
    private SalesOrderLine line1;
    private OrderRequestDTO orderRequestDTO;

    @BeforeEach
    void setup() {
        // 1. Entities Setup
        client = new Client();
        client.setId(1L);

        product1 = Product.builder().id(10L).name("P1").active(true).sellingPrice(100.0).build();
        product2 = Product.builder().id(20L).name("P2").active(true).sellingPrice(50.0).build();

        warehouseMain = Warehouse.builder().id(100L).name("W1").build();
        warehouseSecondary = Warehouse.builder().id(200L).name("W2").build();

        // Initial QOH for main: 40, Secondary: 20
        inventoryMain = Inventory.builder().id(1000L).product(product1).warehouse(warehouseMain).quantityOnHand(40).quantityReserved(0).build();
        inventorySecondary = Inventory.builder().id(2000L).product(product1).warehouse(warehouseSecondary).quantityOnHand(20).quantityReserved(0).build();

        line1 = SalesOrderLine.builder()
                .product(product1)
                .quantityRequested(50)
                .price(product1.getSellingPrice())
                .status(OrderLineStatus.NOT_RESERVED)
                .build();

        // SalesOrder
        order = SalesOrder.builder()
                .id(1L)
                .client(client)
                .orderStatus(OrderStatus.CREATED)
                .orderLines(new ArrayList<>(List.of(line1)))
                .build();
        line1.setSalesOrder(order);


        // 2. DTO Setup
        SalesOrderLineRequestDTO lineDTO1 = new SalesOrderLineRequestDTO(10L, 50);
        orderRequestDTO = new OrderRequestDTO(1L, List.of(lineDTO1));
    }

    // --- 1. create Tests ---

    @Test
    void testCreateOrderSuccess() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product1));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(order);

        SalesOrderLineResponseDTO lineResponseDTO = new SalesOrderLineResponseDTO(
                10L, 10L, "P1",50, 0, 0, OrderLineStatus.NOT_RESERVED, 100.0
        );
        when(salesOrderLineMapper.toDTOs(any())).thenReturn(List.of(lineResponseDTO));


        OrderResponseDTO result = orderService.create(orderRequestDTO);

        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.orderStatus());
        assertEquals(1, result.orderLines().size());
        verify(salesOrderRepository).save(any(SalesOrder.class));
    }

    @Test
    void testCreateOrderClientNotFound() {
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.create(orderRequestDTO));
        assertEquals("Client introuvable.", ex.getMessage());
    }

    @Test
    void testCreateOrderProductNotFound() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.create(orderRequestDTO));
        assertEquals("Produit introuvable ou inactif (ID: 10).", ex.getMessage());
    }

    @Test
    void testCreateOrderProductInactive() {
        product1.setActive(false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product1));

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.create(orderRequestDTO));
        assertEquals("Produit introuvable ou inactif (ID: 10).", ex.getMessage());
    }

    // --- 2. reserveOrder Tests ---

    @Test
    void testReserveOrderSuccessFullyReserved() {
        // Mocks for consolidate (will return W1 = 100L)
        WarehouseInventoryInfo info = new WarehouseInventoryInfo(100L, 1000L, 60); // 60 available > 50 requested
        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(info));
        when(wareHouseRepository.findById(100L)).thenReturn(Optional.of(warehouseMain));

        when(inventoryRepository.findById(1000L)).thenReturn(Optional.of(inventoryMain));

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Mock reservation: 50 reserved
        doAnswer(invocation -> {
            SalesOrderLine line = invocation.getArgument(0);
            line.setQuantityReserved(50);
            line.setQuantityBackorder(0);
            line.setStatus(OrderLineStatus.RESERVED);
            return null;
        }).when(inventoryService).reserverQuantite(any(SalesOrderLine.class), eq(100L));

        when(salesOrderMapper.toDTO(any(SalesOrder.class))).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.RESERVED, Collections.emptyList(), ""));

        OrderResponseDTO result = orderService.reserveOrder(1L);

        assertNotNull(result);
        assertTrue(result.message().contains("P1 : 50/50 réservés, 0 en reliquat"));
        verify(inventoryService).reserverQuantite(any(SalesOrderLine.class), eq(100L));
        assertEquals(OrderStatus.RESERVED, order.getOrderStatus());
    }

    @Test
    void testReserveOrderPartiallyReserved() {
        line1.setQuantityRequested(60); // طلب 60
        order.setOrderLines(List.of(line1));

        // Mocks for consolidate: W1: 40 (main), W2: 20 (source) -> Transfer required
        WarehouseInventoryInfo infoMain = new WarehouseInventoryInfo(100L, 1000L, 40);
        WarehouseInventoryInfo infoSec = new WarehouseInventoryInfo(200L, 2000L, 20);
        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(infoMain, infoSec));
        when(wareHouseRepository.findById(100L)).thenReturn(Optional.of(warehouseMain));

        when(inventoryRepository.findById(1000L)).thenReturn(Optional.of(inventoryMain));
        when(inventoryRepository.findById(2000L)).thenReturn(Optional.of(inventorySecondary));

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Mock partial reservation: 40 reserved, 20 backorder
        doAnswer(invocation -> {
            SalesOrderLine line = invocation.getArgument(0);
            line.setQuantityReserved(40);
            line.setQuantityBackorder(20);
            line.setStatus(OrderLineStatus.PARTIALLY_RESERVED);
            return null;
        }).when(inventoryService).reserverQuantite(any(SalesOrderLine.class), eq(100L));

        when(salesOrderMapper.toDTO(any(SalesOrder.class))).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.RESERVED, Collections.emptyList(), ""));

        OrderResponseDTO result = orderService.reserveOrder(1L);

        assertNotNull(result);
        assertTrue(result.message().contains("P1 : 40/60 réservés, 20 en reliquat"));
        assertEquals(OrderStatus.RESERVED, order.getOrderStatus());
    }

    @Test
    void testReserveOrderNotReservedDueToNoStock() {
        line1.setQuantityRequested(50);

        // Mocks for consolidate: totalAvailable < requested (40 < 50)
        WarehouseInventoryInfo info = new WarehouseInventoryInfo(100L, 1000L, 40);
        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(info));

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        when(salesOrderMapper.toDTO(any(SalesOrder.class))).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.CREATED, Collections.emptyList(), ""));

        OrderResponseDTO result = orderService.reserveOrder(1L);

        assertNotNull(result);
        assertTrue(result.message().contains("P1 : 0/50 réservés, 50 en reliquat"));
        assertEquals(OrderStatus.CREATED, order.getOrderStatus());
        verify(inventoryService, never()).reserverQuantite(any(), anyLong());
    }

    @Test
    void testReserveOrderNotFound() {
        when(salesOrderRepository.findById(anyLong())).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.reserveOrder(99L));
        assertEquals("Commande introuvable.", ex.getMessage());
    }

    // ********** TEST REQUIRED FOR 100% COVERAGE **********
    @Test
    void testReserveLinesSetsWarehouseOnlyOnce() {
        // Setup Order without Warehouse initially
        order.setWarehouse(null);

        SalesOrderLine line2 = SalesOrderLine.builder()
                .product(product2) // ID 20L
                .quantityRequested(10)
                .price(product2.getSellingPrice())
                .status(OrderLineStatus.NOT_RESERVED)
                .salesOrder(order)
                .build();
        order.setOrderLines(Arrays.asList(line1, line2));

        // Mock consolidate
        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(new WarehouseInventoryInfo(100L, 1000L, 60)));
        when(wareHouseRepository.findWareHouse(20L)).thenReturn(List.of(new WarehouseInventoryInfo(200L, 2000L, 20)));

        // Mock Inventory and Warehouse lookup
        when(inventoryRepository.findById(anyLong())).thenReturn(Optional.of(Inventory.builder().id(1L).build()));
        when(wareHouseRepository.findById(100L)).thenReturn(Optional.of(warehouseMain));

        // ******* الإصلاح هنا: تغيير doNothing() إلى doAnswer() *******
        doAnswer(invocation -> {
            SalesOrderLine line = invocation.getArgument(0);
            line.setQuantityReserved(line.getQuantityRequested()); // حجز كامل
            line.setQuantityBackorder(0);
            line.setStatus(OrderLineStatus.RESERVED);
            return null;
        }).when(inventoryService).reserverQuantite(any(), anyLong());

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(salesOrderMapper.toDTO(any(SalesOrder.class))).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.RESERVED, Collections.emptyList(), ""));

        orderService.reserveOrder(1L);

        // Verify the Order warehouse was set only once (to W1)
        assertEquals(warehouseMain, order.getWarehouse());

        // Verify total calls to findById(warehouseId) is 1.
        verify(wareHouseRepository, times(1)).findById(anyLong());
    }


    // --- 3. confirmOrder Tests ---

    @Test
    void testConfirmOrderSuccess() {
        order.setOrderStatus(OrderStatus.CREATED);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(order);
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(new Shipment());
        when(salesOrderMapper.toDTO(order)).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.RESERVED, Collections.emptyList(), ""));

        OrderResponseDTO result = orderService.confirmOrder(1L);

        assertEquals(OrderStatus.RESERVED, order.getOrderStatus());
        assertNotNull(order.getConfirmedAt());
        verify(shipmentRepository).save(any(Shipment.class));
    }

    @Test
    void testConfirmOrderInvalidStatus() {
        order.setOrderStatus(OrderStatus.SHIPPED);
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> orderService.confirmOrder(1L));
        assertEquals("Impossible de confirmer la commande à ce stade.", ex.getMessage());
    }

    // --- 4. cancel Tests ---

    @Test
    void testCancelOrderCreatedSuccess() {
        order.setOrderStatus(OrderStatus.CREATED);
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(salesOrderRepository.save(order)).thenReturn(order);
        when(salesOrderMapper.toDTO(order)).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.CANCELED, Collections.emptyList(), ""));

        orderService.cancel(1L);

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        verify(inventoryRepository, never()).findByProductIdAndWarehouseId(anyLong(), anyLong());
    }

    @Test
    void testCancelOrderReservedSuccessAndInventoryReleased() {
        // Setup reserved state
        order.setOrderStatus(OrderStatus.RESERVED);
        order.setWarehouse(warehouseMain);
        line1.setQuantityReserved(20);

        inventoryMain.setQuantityReserved(20); // Initial reserved Qty in inventory

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepository.findByProductIdAndWarehouseId(10L, 100L)).thenReturn(Optional.of(inventoryMain));
        when(salesOrderRepository.save(order)).thenReturn(order);
        when(salesOrderMapper.toDTO(order)).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.CANCELED, Collections.emptyList(), ""));

        orderService.cancel(1L);

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        assertEquals(0, inventoryMain.getQuantityReserved()); // 20 - 20 = 0
        verify(inventoryRepository).save(inventoryMain);
    }

    @Test
    void testCancelOrderReservedSuccessWithoutWarehouse() {
        // Setup reserved state
        order.setOrderStatus(OrderStatus.RESERVED);
        order.setWarehouse(null); // Warehouse is null
        line1.setQuantityReserved(20);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(salesOrderRepository.save(order)).thenReturn(order);
        when(salesOrderMapper.toDTO(order)).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.CANCELED, Collections.emptyList(), ""));

        orderService.cancel(1L);

        assertEquals(OrderStatus.CANCELED, order.getOrderStatus());
        verify(inventoryRepository, never()).findByProductIdAndWarehouseId(anyLong(), anyLong());
    }

    @Test
    void testCancelOrderAlreadyShipped() {
        order.setOrderStatus(OrderStatus.SHIPPED);
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.cancel(1L));
        assertEquals("Impossible d'annuler une commande déjà expédiée.", ex.getMessage());
    }

    @Test
    void testCancelOrderReservedInventoryNotFound() {
        order.setOrderStatus(OrderStatus.RESERVED);
        order.setWarehouse(warehouseMain);
        line1.setQuantityReserved(20);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(inventoryRepository.findByProductIdAndWarehouseId(10L, 100L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.cancel(1L));
        assertEquals("Inventaire introuvable pour le produit.", ex.getMessage());
        verify(salesOrderRepository, never()).save(any(SalesOrder.class));
    }


    // --- 5. getOrder Tests ---

    @Test
    void testGetOrderSuccess() {
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(salesOrderMapper.toDTO(order)).thenReturn(new OrderResponseDTO(1L, 1L, OrderStatus.CREATED, Collections.emptyList(), ""));

        OrderResponseDTO result = orderService.getOrder(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void testGetOrderNotFound() {
        when(salesOrderRepository.findById(anyLong())).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.getOrder(99L));
        assertEquals("Commande introuvable.", ex.getMessage());
    }

    // --- 6. consolidateQuantityInOneWarehouse Tests (The Complex Logic) ---

    // Case 1: Total stock is insufficient (should return -1L)
    @Test
    void testConsolidateInsufficientStock() {
        int requested = 70; // Only 60 total available

        WarehouseInventoryInfo infoMain = new WarehouseInventoryInfo(100L, 1000L, 40);
        WarehouseInventoryInfo infoSec = new WarehouseInventoryInfo(200L, 2000L, 20);

        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(infoMain, infoSec));

        long result = orderService.consolidateQuantityInOneWarehouse(requested, 10L);

        assertEquals(-1L, result);
        verify(inventoryRepository, never()).findById(anyLong());
    }

    // Case 2: Stock in main warehouse is sufficient (no transfer needed)
    @Test
    void testConsolidateMainWarehouseSufficient() {
        int requested = 30;

        WarehouseInventoryInfo infoMain = new WarehouseInventoryInfo(100L, 1000L, 40); // 40 > 30
        WarehouseInventoryInfo infoSec = new WarehouseInventoryInfo(200L, 2000L, 20);

        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(infoMain, infoSec));
        when(inventoryRepository.findById(1000L)).thenReturn(Optional.of(inventoryMain));

        long result = orderService.consolidateQuantityInOneWarehouse(requested, 10L);

        assertEquals(100L, result);
        verify(movementInventoryRepository, never()).save(any(InventoryMovement.class));
        verify(inventoryRepository, times(1)).save(inventoryMain);
    }

    // Case 3: Consolidation/Transfer is required
    @Test
    void testConsolidateTransferRequiredSuccess() {
        int requested = 50; // Need 10 more than main (40)

        // W1: 40, W2: 20 -> Total 60. Max is W1. Need 10 from W2.
        WarehouseInventoryInfo infoMain = new WarehouseInventoryInfo(100L, 1000L, 40);
        WarehouseInventoryInfo infoSec = new WarehouseInventoryInfo(200L, 2000L, 20);

        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(infoMain, infoSec));
        when(inventoryRepository.findById(1000L)).thenReturn(Optional.of(inventoryMain));
        when(inventoryRepository.findById(2000L)).thenReturn(Optional.of(inventorySecondary));

        int initialMainQOH = inventoryMain.getQuantityOnHand(); // 40
        int initialSecQOH = inventorySecondary.getQuantityOnHand(); // 20

        long result = orderService.consolidateQuantityInOneWarehouse(requested, 10L);

        assertEquals(100L, result);

        assertEquals(initialMainQOH + 10, inventoryMain.getQuantityOnHand()); // 40 + 10 = 50
        assertEquals(initialSecQOH - 10, inventorySecondary.getQuantityOnHand()); // 20 - 10 = 10

        verify(movementInventoryRepository, times(2)).save(any(InventoryMovement.class));
        verify(inventoryRepository, times(1)).save(inventorySecondary);
        verify(inventoryRepository, times(1)).save(inventoryMain);
    }

    // Case 4: Consolidation with multiple sources required (Only one source in this setup, testing max transferable)
    @Test
    void testConsolidateTransferRequiredMaxTransferable() {
        int requested = 60; // Need 20 more than main (40). W2 has 20. Need all 20 from W2.

        WarehouseInventoryInfo infoMain = new WarehouseInventoryInfo(100L, 1000L, 40);
        WarehouseInventoryInfo infoSec = new WarehouseInventoryInfo(200L, 2000L, 20);

        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(infoMain, infoSec));
        when(inventoryRepository.findById(1000L)).thenReturn(Optional.of(inventoryMain));
        when(inventoryRepository.findById(2000L)).thenReturn(Optional.of(inventorySecondary));

        long result = orderService.consolidateQuantityInOneWarehouse(requested, 10L);

        assertEquals(100L, result);

        assertEquals(40 + 20, inventoryMain.getQuantityOnHand()); // 60
        assertEquals(20 - 20, inventorySecondary.getQuantityOnHand()); // 0

        verify(movementInventoryRepository, times(2)).save(any(InventoryMovement.class));
        verify(inventoryRepository).save(inventorySecondary);
        verify(inventoryRepository).save(inventoryMain);
    }

    // Case 5: Consolidation Fails (Inventory principal introuvable)
    @Test
    void testConsolidateMainInventoryNotFoundThrowsException() {
        int requested = 30;

        WarehouseInventoryInfo infoMain = new WarehouseInventoryInfo(100L, 1000L, 40);
        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(infoMain));
        when(inventoryRepository.findById(1000L)).thenReturn(Optional.empty());

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.consolidateQuantityInOneWarehouse(requested, 10L));
        assertEquals("Inventaire principal introuvable.", ex.getMessage());
    }

    // Case 6: Consolidation Fails (Inventaire source introuvable)
    @Test
    void testConsolidateSourceInventoryNotFoundThrowsException() {
        int requested = 50;

        WarehouseInventoryInfo infoMain = new WarehouseInventoryInfo(100L, 1000L, 40);
        WarehouseInventoryInfo infoSec = new WarehouseInventoryInfo(200L, 2000L, 20);

        when(wareHouseRepository.findWareHouse(10L)).thenReturn(List.of(infoMain, infoSec));
        when(inventoryRepository.findById(1000L)).thenReturn(Optional.of(inventoryMain));
        when(inventoryRepository.findById(2000L)).thenReturn(Optional.empty()); // Source not found

        GestionException ex = assertThrows(GestionException.class,
                () -> orderService.consolidateQuantityInOneWarehouse(requested, 10L));
        assertEquals("Inventaire source introuvable.", ex.getMessage());
    }

}