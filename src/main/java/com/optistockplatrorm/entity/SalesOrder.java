package com.optistockplatrorm.entity;

import com.optistockplatrorm.entity.Enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name ="sales_order")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonIgnore
    private Client client;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonIgnore
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name="order_status")
    private OrderStatus orderStatus;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name="reserved_at")
    private LocalDateTime reservedAt;

    @Column(name="shipped_at")
    private LocalDateTime shippedAt;

    @Column(name="delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SalesOrderLine> orderLines;
}