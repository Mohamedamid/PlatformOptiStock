package com.optistockplatrorm.service;

import com.optistockplatrorm.entity.Warehouse;
import com.optistockplatrorm.entity.Enums.Role;
import com.optistockplatrorm.entity.WarehouseManager;
import com.optistockplatrorm.dto.ManagerRequestDTO;
import com.optistockplatrorm.dto.ManagerResponseDTO;
import com.optistockplatrorm.repository.ManagerRepository;
import com.optistockplatrorm.repository.WareHouseRepository;
import com.optistockplatrorm.mapper.ManagerMapper;
import com.optistockplatrorm.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManagerService {

    @Autowired
    private ManagerRepository ManagerRepository;

    @Autowired
    private WareHouseRepository warehouseRepository;

    @Autowired
    private ManagerMapper managerMapper;

    public ManagerResponseDTO create(ManagerRequestDTO dto) {
        Set<Warehouse> warehouses = new HashSet<>(warehouseRepository.findAllById(dto.IdWarehouse()));

        Set<Long> foundIds = warehouses.stream()
                .map(Warehouse::getId).collect(Collectors.toSet());
        Set<Long> missingIds = dto.IdWarehouse().stream()
                .filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
        if (!missingIds.isEmpty()) {
            throw new RuntimeException("Impossible de créer le gestionnaire : les entrepôts suivants sont introuvables -> " + missingIds);
        }

        WarehouseManager manager = WarehouseManager.builder()
                .firstName(dto.firstName()).lastName(dto.lastName()).email(dto.email()).password(PasswordUtil.hash(dto.password()))
                .role(Role.WAREHOUSE_MANAGER).createdAt(LocalDateTime.now()).active(dto.active()).build();
        for (Warehouse w : warehouses) {
            manager.addWarehouse(w);
        }

        WarehouseManager saved = ManagerRepository.save(manager);
        return managerMapper.toDTO(saved);
    }

    public ManagerResponseDTO getManagerById(long id) {
        WarehouseManager manager = ManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gestionnaire introuvable avec l'identifiant : " + id));
        return managerMapper.toDTO(manager);
    }

    public ManagerResponseDTO updateManager(long id, ManagerRequestDTO dto) {
        WarehouseManager manager = ManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Impossible de mettre à jour : gestionnaire introuvable avec l'identifiant : " + id));

        manager.setFirstName(dto.firstName());
        manager.setLastName(dto.lastName());
        manager.setEmail(dto.email());
        manager.setActive(dto.active());

        if (dto.IdWarehouse() != null) {
            Set<Warehouse> newWarehouses = new HashSet<>(warehouseRepository.findAllById(dto.IdWarehouse()));
            Set<Long> foundIds = newWarehouses.stream().map(Warehouse::getId).collect(Collectors.toSet());
            Set<Long> missing = dto.IdWarehouse().stream().filter(i -> !foundIds.contains(i)).collect(Collectors.toSet());
            if (!missing.isEmpty()) {
                throw new RuntimeException("Impossible de mettre à jour : certains entrepôts sont introuvables -> " + missing);
            }

            Set<Warehouse> current = new HashSet<>(manager.getWarehouses());
            for (Warehouse w : current) {
                manager.removeWarehouse(w);
            }
            for (Warehouse w : newWarehouses) {
                manager.addWarehouse(w);
            }
        }

        WarehouseManager updated = ManagerRepository.save(manager);
        return managerMapper.toDTO(updated);
    }

    public boolean deleteManager(long id) {
        if (!ManagerRepository.existsById(id)) {
            return false;
        }
        ManagerRepository.deleteById(id);
        return true;
    }
}
