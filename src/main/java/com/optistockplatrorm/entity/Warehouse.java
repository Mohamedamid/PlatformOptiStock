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

    @NotBlank(message = "Le nom de l'entrepôt est requis")
    private String name;

    @NotBlank(message = "L'adresse est requise")
    private String address;

    @NotBlank(message = "Le code de l'entrepôt est requis")
    @Column(unique = true)
    private String code;

    @Column(name = "is_active")
    private boolean active = true;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    @ManyToMany(mappedBy = "warehouses", fetch = FetchType.LAZY)
    private Set<WarehouseManager> managers = new HashSet<>();

    public void assignManager(WarehouseManager manager) {
        this.managers.add(manager);
        manager.getWarehouses().add(this);
    }

    public void unassignManager(WarehouseManager manager) {
        this.managers.remove(manager);
        manager.getWarehouses().remove(this);
    }
}