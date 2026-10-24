package ma.fsbm.lias.dto.materiel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BesoinRequest(
    @NotBlank String intitule,
    String description,
    Integer quantiteDemandee,
    @NotNull Long demandeurId
) {}
