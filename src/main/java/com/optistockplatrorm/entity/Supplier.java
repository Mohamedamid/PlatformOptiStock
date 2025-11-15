package com.optistockplatrorm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="supplier_name")
    private String supplierName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String number;

    @OneToMany(mappedBy = "supplier")
    private List<PurchaseOrder> purchaseOrders;
}