package ma.fsbm.lias.dto.mandat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MandatResponse(
    Long id,
    String intitule,
    LocalDate dateDebut,
    LocalDate dateFin,
    boolean actif,
    Long directeurId,
    String directeurNom,
    Long viceDirecteurId,
    String viceDirecteurNom,
    LocalDateTime createdAt
) {}
