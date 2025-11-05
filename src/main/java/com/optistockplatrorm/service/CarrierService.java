package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.CarrierRequestDTO;
import com.optistockplatrorm.dto.CarrierResponseDTO;
import com.optistockplatrorm.entity.Carrier;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.mapper.CarrierMapper;
import com.optistockplatrorm.repository.CarrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CarrierService {

    @Autowired
    private CarrierRepository carrierRepository;

    @Autowired
    private CarrierMapper carrierMapper;

    public Page<CarrierResponseDTO> getAllCarriers(int page,int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Carrier> carriers = carrierRepository.findAll(pageable);
        if (carriers.isEmpty()) {
            throw new GestionException("Aucun transporteur trouvé dans le système");
        }
        return carriers.map(carrierMapper::toDTO);
    }

    public CarrierResponseDTO getCarrierById(Long id) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new GestionException("Transporteur non trouvé avec l'ID : " + id));
        return carrierMapper.toDTO(carrier);
    }

    public CarrierResponseDTO createCarrier(CarrierRequestDTO dto) {
        if (carrierRepository.existsByCarrierName(dto.carrierName())) {
            throw new GestionException("Un transporteur avec ce nom existe déjà");
        }

        Carrier carrier = carrierMapper.toEntity(dto);
        Carrier saved = carrierRepository.save(carrier);
        return carrierMapper.toDTO(saved);
    }

    public CarrierResponseDTO updateCarrier(Long id, CarrierRequestDTO dto) {
        Carrier carrier = carrierRepository.findById(id)
                .orElseThrow(() -> new GestionException("Transporteur non trouvé avec l'ID : " + id));

        if (!carrier.getCarrierName().equals(dto.carrierName()) &&
                carrierRepository.existsByCarrierName(dto.carrierName())) {
            throw new GestionException("Un transporteur avec ce nom existe déjà");
        }

        carrier.setCarrierName(dto.carrierName());
        carrier.setPhoneNumber(dto.phoneNumber());

        Carrier updated = carrierRepository.save(carrier);
        return carrierMapper.toDTO(updated);
    }

    public void deleteCarrier(Long id) {
        if (!carrierRepository.existsById(id)) {
            throw new GestionException("Transporteur non trouvé avec l'ID : " + id);
        }
        carrierRepository.deleteById(id);
    }
}