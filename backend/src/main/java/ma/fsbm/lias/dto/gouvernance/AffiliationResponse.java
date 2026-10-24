package ma.fsbm.lias.dto.gouvernance;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AffiliationResponse(
    Long id,
    Long membreId,
    String membreNom,
    LocalDate dateDebut,
    LocalDate dateFin,
    String motif,
    LocalDateTime createdAt
) {}
