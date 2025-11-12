package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.SupplierRequestDTO;
import com.optistockplatrorm.dto.SupplierResponseDTO;
import com.optistockplatrorm.entity.Supplier;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.mapper.SupplierMapper;
import com.optistockplatrorm.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    public Page<SupplierResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return supplierRepository.findAll(pageable).map(supplierMapper::toDto);
    }

    public SupplierResponseDTO getById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Impossible de trouver ce fournisseur."));
        return supplierMapper.toDto(supplier);
    }

    public SupplierResponseDTO create(SupplierRequestDTO dto) {
        if (supplierRepository.existsByNumber(dto.phoneNumber())) {
            throw new GestionException("Un fournisseur utilise déjà ce numéro de téléphone.");
        }
        Supplier supplier = supplierMapper.toEntity(dto);
        return supplierMapper.toDto(supplierRepository.save(supplier));
    }

    public SupplierResponseDTO update(Long id, SupplierRequestDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new GestionException("Aucun fournisseur trouvé avec cet identifiant."));
        supplierMapper.toDtoSupplier(dto, supplier);
        return supplierMapper.toDto(supplierRepository.save(supplier));
    }

    public SupplierResponseDTO delete(Long id) throws BadRequestException {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new GestionException("Aucun fournisseur trouvé pour la suppression."));
        supplierRepository.delete(supplier);
        return supplierMapper.toDto(supplier);
    }
}