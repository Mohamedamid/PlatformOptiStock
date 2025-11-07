package com.optistockplatrorm.service;

import com.optistockplatrorm.entity.Carrier;
import com.optistockplatrorm.entity.Shipment;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.repository.CarrierRepository;
import com.optistockplatrorm.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private CarrierRepository carrierRepository;

    public void assignCarrier(Long shipmentId, Long carrierId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new GestionException("Aucun envoi trouvé"));

        Carrier carrier = carrierRepository.findById(carrierId)
                .orElseThrow(() -> new GestionException("Aucun transporteur trouvé"));

        shipment.setCarrier(carrier);

        shipmentRepository.save(shipment);
    }
}
