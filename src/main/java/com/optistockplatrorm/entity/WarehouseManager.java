package com.optistockplatrorm.entity;

import jakarta.persistence.Entity;
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
            joinColumns = @JoinColumn(name = "manager_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "warehouse_id", referencedColumnName = "id")
    )
    private Set<Warehouse> warehouses = new HashSet<>();

    public void addWarehouse(Warehouse warehouse) {
        if (this.warehouses == null) {
            this.warehouses = new HashSet<>();
        }
        this.warehouses.add(warehouse);
        warehouse.getManagers().add(this);
    }

    public void removeWarehouse(Warehouse warehouse) {
        this.warehouses.remove(warehouse);
        warehouse.getManagers().remove(this);
    }

    @EqualsAndHashCode.Include
    private Long getIdentity() {
        return this.getId();
    }
}