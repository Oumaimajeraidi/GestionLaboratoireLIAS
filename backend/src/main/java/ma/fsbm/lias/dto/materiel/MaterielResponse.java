package ma.fsbm.lias.dto.materiel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.EtatMateriel;

public record MaterielResponse(
    Long id,
    String nom,
    String description,
    String reference,
    Integer quantite,
    EtatMateriel etat,
    LocalDate dateArrivee,
    Long attribueAId,
    String attribueANom,
    LocalDateTime createdAt
) {}
