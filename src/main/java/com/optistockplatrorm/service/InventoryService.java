package com.optistockplatrorm.service;

import com.optistockplatrorm.dto.*;
import com.optistockplatrorm.entity.*;
import com.optistockplatrorm.entity.Enums.MovementType;
import com.optistockplatrorm.entity.Enums.OrderLineStatus;
import com.optistockplatrorm.exception.GestionException;
import com.optistockplatrorm.mapper.*;
import com.optistockplatrorm.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WareHouseRepository entrepotRepository;
    private final MovementInventoryRepository mouvementRepository;
    private final MovementInventoryMapper mouvementMapper;
    private final WareHouseRepository wareHouseRepository;

    public InventoryResponseDTO createInventory(InventoryRequestDTO dto) {

        Inventory inventaire = inventoryMapper.toEntity(dto);

        Product produit = productRepository.findById(dto.productId())
                .orElseThrow(() -> new GestionException("Produit introuvable avec l’identifiant " + dto.productId()));

        Warehouse entrepot = entrepotRepository.findById(dto.warehouseId())
                .orElseThrow(() -> new GestionException("Entrepôt introuvable avec l’identifiant " + dto.warehouseId()));

        inventoryRepository.findByProductIdAndWarehouseId(dto.productId(), dto.warehouseId())
                .ifPresent(inv -> {
                    throw new GestionException("Un inventaire existe déjà pour ce produit dans cet entrepôt.");
                });

        inventaire.setProduct(produit);
        inventaire.setWarehouse(entrepot);
        inventaire.setQuantityOnHand(0);
        inventaire.setQuantityReserved(0);

        return inventoryMapper.toDto(inventoryRepository.save(inventaire));
    }

    public InventoryResponseDTO getInventoryById(long id) {
        Inventory inventaire = inventoryRepository.findById(id)
                .orElseThrow(() -> new GestionException("Inventaire introuvable avec l’identifiant " + id));

        return inventoryMapper.toDto(inventaire);
    }

    public Page<InventoryResponseDTO> getAllInventory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryRepository.findAll(pageable).map(inventoryMapper::toDto);
    }

    @Transactional
    public void updateInventory(Long id, InventoryRequestDTO dto) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new GestionException("Inventaire introuvable avec l’identifiant " + id));

        // On met uniquement à jour ce qui est autorisé
        if (dto.productId() != null) {
            Product product = productRepository.findById(dto.productId())
                    .orElseThrow(() -> new GestionException("Produit introuvable avec l’identifiant " + dto.productId()));
            inventory.setProduct(product);
        }

        if (dto.warehouseId() != null) {
            Warehouse warehouse = wareHouseRepository.findById(dto.warehouseId())
                    .orElseThrow(() -> new GestionException("Entrepôt introuvable avec l’identifiant " + dto.warehouseId()));
            inventory.setWarehouse(warehouse);
        }

        inventoryRepository.save(inventory);
    }

    /**
     * Supprime un inventaire existant.
     */
    @Transactional
    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new GestionException("Inventaire introuvable avec l’identifiant " + id));

        inventoryRepository.delete(inventory);
    }

    @Transactional
    public MovementInventoryResponseDTO enregistrerEntree(MovementInventoryRequestDTO dto) {
        if (dto.quantity() <= 0) {
            throw new GestionException("La quantité d’entrée doit être supérieure à zéro.");
        }

        Inventory inventaire = inventoryRepository.findById(dto.inventoryId())
                .orElseThrow(() -> new GestionException("Inventaire introuvable."));

        inventaire.setQuantityOnHand(inventaire.getQuantityOnHand() + dto.quantity());
        inventoryRepository.save(inventaire);

        InventoryMovement mouvement = InventoryMovement.builder()
                .inventory(inventaire).movementType(MovementType.INBOUND).quantity(dto.quantity())
                .createdAt(LocalDateTime.now()).build();
        return mouvementMapper.toDTO(mouvementRepository.save(mouvement));
    }

    @Transactional
    public MovementInventoryResponseDTO enregistrerSortie(MovementInventoryRequestDTO dto) {
        Inventory inventaire = inventoryRepository.findById(dto.inventoryId())
                .orElseThrow(() -> new GestionException("Inventaire introuvable."));

        if (dto.quantity() <= 0) {
            throw new GestionException("La quantité de sortie doit être supérieure à zéro.");
        }

        if (inventaire.getQuantityOnHand() < dto.quantity()) {
            throw new GestionException("La quantité demandée dépasse la quantité disponible en stock.");
        }

        inventaire.setQuantityOnHand(inventaire.getQuantityOnHand() - dto.quantity());
        inventoryRepository.save(inventaire);

        InventoryMovement mouvement = InventoryMovement.builder()
                .inventory(inventaire).movementType(MovementType.OUTBOUND).quantity(dto.quantity())
                .createdAt(LocalDateTime.now()).build();
        return mouvementMapper.toDTO(mouvementRepository.save(mouvement));
    }

    @Transactional
    public MovementInventoryResponseDTO enregistrerAjustement(MovementInventoryRequestDTO dto) {
        Inventory inventaire = inventoryRepository.findById(dto.inventoryId())
                .orElseThrow(() -> new GestionException("Inventaire introuvable."));

        int quantiteDisponible = inventaire.getQuantityOnHand();
        int quantiteReservee = inventaire.getQuantityReserved();
        int ajustement = dto.quantity();

        if (ajustement < 0 && (quantiteDisponible + ajustement) < quantiteReservee) {
            throw new GestionException("Ajustement refusé : la quantité disponible ne peut pas être inférieure à la quantité réservée.");
        }

        inventaire.setQuantityOnHand(quantiteDisponible + ajustement);
        inventoryRepository.save(inventaire);

        InventoryMovement mouvement = InventoryMovement.builder()
                .inventory(inventaire).movementType(MovementType.ADJUSTMENT).quantity(ajustement)
                .createdAt(LocalDateTime.now()).build();

        mouvementRepository.save(mouvement);
        return mouvementMapper.toDTO(mouvement);
    }

    public void reserverQuantite(SalesOrderLine ligne, long entrepotId) {
        Inventory inventaire = inventoryRepository
                .findByProductIdAndWarehouseId(ligne.getProduct().getId(), entrepotId)
                .orElseThrow(() -> new GestionException(
                        "Aucun inventaire trouvé pour le produit : " + ligne.getProduct().getName()
                ));

        int disponible = inventaire.getQuantityOnHand() - inventaire.getQuantityReserved();
        int quantiteAReserver = Math.min(ligne.getQuantityRequested(), disponible);

        inventaire.setQuantityReserved(inventaire.getQuantityReserved() + quantiteAReserver);
        ligne.setQuantityReserved(quantiteAReserver);
        ligne.setQuantityBackorder(ligne.getQuantityRequested() - quantiteAReserver);

        if (quantiteAReserver == ligne.getQuantityRequested()) {
            ligne.setStatus(OrderLineStatus.RESERVED);
        } else if (quantiteAReserver > 0) {
            ligne.setStatus(OrderLineStatus.PARTIALLY_RESERVED);
        } else {
            ligne.setStatus(OrderLineStatus.NOT_RESERVED);
        }

        inventoryRepository.save(inventaire);
    }

    public void libererInventaire(SalesOrder commande) {
        commande.getOrderLines().forEach(ligne -> {
            Inventory inventaire = inventoryRepository
                    .findByProductIdAndWarehouseId(ligne.getProduct().getId(), commande.getWarehouse().getId())
                    .orElseThrow(() -> new GestionException(
                            "Aucun inventaire trouvé pour le produit : " + ligne.getProduct().getName()
                    ));

            int quantiteReservee = ligne.getQuantityReserved() != null ? ligne.getQuantityReserved() : 0;
            inventaire.setQuantityReserved(Math.max(0, inventaire.getQuantityReserved() - quantiteReservee));

            inventoryRepository.save(inventaire);
        });
    }
}