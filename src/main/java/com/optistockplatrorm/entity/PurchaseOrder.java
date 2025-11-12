package com.optistockplatrorm.entity;

import com.optistockplatrorm.entity.Enums.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="purchase_order")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="order_status")
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus orderStatus;

    @Column(name="order_date")
    private LocalDateTime orderDate;

    @Column(name="expected_date")
    private LocalDateTime expectedDate;

    @ManyToOne
    @JoinColumn(name="supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name="warehouse_id")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderLine> orderLines = new ArrayList<>();
}