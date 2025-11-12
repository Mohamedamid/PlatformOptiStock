package com.optistockplatrorm.entity;

import com.optistockplatrorm.entity.Enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name="shipment")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name="shipment_status")
    private ShipmentStatus shipmentStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sales_order_id")
    private  SalesOrder salesOrder;

    @Column(name="planned_date")
    private LocalDateTime plannedDate;
    @Column(name="shipped_date")
    private LocalDateTime shippedDate;
    @Column(name="delivery_date")
    private LocalDateTime deliveryDate;

    @ManyToOne
    @JoinColumn(name="carrier_id")
    private Carrier carrier;
}