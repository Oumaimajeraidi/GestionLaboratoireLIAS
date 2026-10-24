package ma.fsbm.lias.dto.convention;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import ma.fsbm.lias.entity.enums.StatutConvention;
import ma.fsbm.lias.entity.enums.TypeConvention;

public record ConventionRequest(
    @NotBlank String intitule,
    @NotNull TypeConvention type,
    StatutConvention statut,
    Long partenaireId,
    LocalDate dateDebut,
    LocalDate dateFin,
    String objectif
) {}
