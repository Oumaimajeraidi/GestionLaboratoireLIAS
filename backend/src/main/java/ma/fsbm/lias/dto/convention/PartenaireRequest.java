package ma.fsbm.lias.dto.convention;

import jakarta.validation.constraints.NotBlank;

public record PartenaireRequest(
    @NotBlank String nom,
    String pays,
    String typeInstitution,
    String contactNom,
    String contactEmail
) {}
