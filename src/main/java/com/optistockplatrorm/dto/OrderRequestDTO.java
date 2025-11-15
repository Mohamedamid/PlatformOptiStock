package com.optistockplatrorm.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequestDTO(

        @NotNull(message = "L’identifiant du client doit être renseigné.")
        Long clientId,

        @NotNull(message = "Les lignes de commande doivent être renseignées.")
        List<SalesOrderLineRequestDTO> lines

) {}