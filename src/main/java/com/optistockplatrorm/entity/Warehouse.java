package com.optistockplatrorm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank(message = "Le nom de l'entrepôt est requis")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "L'adresse est requise")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "Le code de l'entrepôt est requis")
    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "is_active")
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    @ManyToMany(mappedBy = "warehouses", fetch = FetchType.LAZY)
    private Set<WarehouseManager> managers = new HashSet<>();

    @OneToMany(mappedBy = "warehouse" )
    private List<PurchaseOrder> purchaseOrders;

    public void assignManager(WarehouseManager manager) {
        this.managers.add(manager);
        manager.getWarehouses().add(this);
    }

    public void unassignManager(WarehouseManager manager) {
        this.managers.remove(manager);
        manager.getWarehouses().remove(this);
    }
}