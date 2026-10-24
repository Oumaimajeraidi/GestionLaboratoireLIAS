package ma.fsbm.lias.dto.convention;

import java.time.LocalDateTime;

public record PartenaireResponse(
    Long id,
    String nom,
    String pays,
    String typeInstitution,
    String contactNom,
    String contactEmail,
    LocalDateTime createdAt
) {}
