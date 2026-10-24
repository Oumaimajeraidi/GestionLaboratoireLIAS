package ma.fsbm.lias.dto.gouvernance;

import java.time.LocalDate;

public record AffiliationRequest(
    Long membreId,
    LocalDate dateDebut,
    LocalDate dateFin,
    String motif
) {}
