package ma.fsbm.lias.dto.convention;

import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.StatutConvention;
import ma.fsbm.lias.entity.enums.TypeConvention;

public record ConventionResponse(
    Long id,
    String intitule,
    TypeConvention type,
    StatutConvention statut,
    Long partenaireId,
    String partenaireNom,
    String partenairePays,
    LocalDate dateDebut,
    LocalDate dateFin,
    String objectif,
    String fichierNom,
    LocalDateTime createdAt
) {}
