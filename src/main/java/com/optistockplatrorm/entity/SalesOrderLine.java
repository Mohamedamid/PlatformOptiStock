package com.optistockplatrorm.entity;

import com.optistockplatrorm.entity.Enums.OrderLineStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity
@Table(name="sales_order_line")
public class SalesOrderLine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="quantity_requested")
    private Integer  quantityRequested;

    @Column(name="quantity_reserved")
    private Integer  quantityReserved;

    @Column(name="quantity_backorder")
    private Integer  quantityBackorder;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private OrderLineStatus status;

    private double price;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @ManyToOne
    @JoinColumn(name = "sales_order_id")
    @JsonIgnore
    private SalesOrder salesOrder;
}