package com.optistockplatrorm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Warehouse name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Warehouse code is required")
    @Column(unique = true)
    private String code;

    @Column(name = "is_active")
    private boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventory;

    @OneToMany(mappedBy = "warehouse" )
    private List<PurchaseOrder> purchaseOrders;

    @ManyToMany(mappedBy = "warehouses", fetch = FetchType.LAZY)
    private Set<WarehouseManager> managers = new HashSet<>();

    public void addManager(WarehouseManager manager) {
        this.managers.add(manager);
        manager.getWarehouses().add(this);
    }

    public void removeManager(WarehouseManager manager) {
        this.managers.remove(manager);
        manager.getWarehouses().remove(this);
    }
}