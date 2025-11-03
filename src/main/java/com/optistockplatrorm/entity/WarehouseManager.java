package com.optistockplatrorm.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "warehouse_manager")
public class WarehouseManager extends User {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "manager_warehouse",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id")
    )
    private Set<Warehouse> warehouses = new HashSet<>();

    @EqualsAndHashCode.Include
    private Long identity() {
        return getId();
    }

    public void assignWarehouse(Warehouse warehouse) {
        if (warehouse == null) return;

        warehouses.add(warehouse);
        warehouse.getManagers().add(this);
    }

    public void unassignWarehouse(Warehouse warehouse) {
        if (warehouse == null) return;

        warehouses.remove(warehouse);
        warehouse.getManagers().remove(this);
    }
}
