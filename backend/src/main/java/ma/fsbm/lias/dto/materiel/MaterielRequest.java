package ma.fsbm.lias.dto.materiel;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import ma.fsbm.lias.entity.enums.EtatMateriel;

public record MaterielRequest(
    @NotBlank String nom,
    String description,
    String reference,
    Integer quantite,
    EtatMateriel etat,
    LocalDate dateArrivee,
    Long attribueAId
) {}
