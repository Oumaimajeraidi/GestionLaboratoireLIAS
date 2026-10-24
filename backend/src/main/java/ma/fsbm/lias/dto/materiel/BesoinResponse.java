package ma.fsbm.lias.dto.materiel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.StatutBesoin;

public record BesoinResponse(
    Long id,
    String intitule,
    String description,
    Integer quantiteDemandee,
    StatutBesoin statut,
    String commentaireDecision,
    LocalDate dateValidation,
    Long demandeurId,
    String demandeurNom,
    Long valideurId,
    String valideurNom,
    LocalDateTime createdAt
) {}
