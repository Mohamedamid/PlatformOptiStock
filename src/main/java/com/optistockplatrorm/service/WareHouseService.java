package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.WarehouseRequestDTO;
import com.optistockplatrorm.dto.WarehouseResponseDTO;
import com.optistockplatrorm.entity.Warehouse;
import com.optistockplatrorm.mapper.WarehouseMapper;
import com.optistockplatrorm.repository.WareHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WareHouseService {

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Autowired
    private WarehouseMapper wareHouseMapper;

    public Page<WarehouseResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Warehouse> warehousesPage = wareHouseRepository.findAll(pageable);
        return warehousesPage.map(wareHouseMapper::toDTO);
    }

    public WarehouseResponseDTO getWarehouseById(long id){
        return wareHouseMapper.toDTO(wareHouseRepository.findById(id).orElse(null));
    }

    public WarehouseResponseDTO create(WarehouseRequestDTO dto) {
        Warehouse warehouse = wareHouseMapper.toEntity(dto);
        return wareHouseMapper.toDTO(wareHouseRepository.save(warehouse));
    }

    public WarehouseResponseDTO updateWarehouse(long id , WarehouseRequestDTO dto) {
        Warehouse warehouse =   wareHouseRepository.findById(id).orElse(null);
        warehouse.setCode(dto.code());
        warehouse.setName(dto.name());
        warehouse.setAddress(dto.address());
        warehouse.setActive(dto.active());
        return wareHouseMapper.toDTO(wareHouseRepository.save(warehouse));
    }

    public boolean deleteWarehouse(long id) {
        if(!wareHouseRepository.existsById(id)) return false;
        wareHouseRepository.deleteById(id);
        return  true;
    }
}
