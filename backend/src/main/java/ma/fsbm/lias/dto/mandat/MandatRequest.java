package ma.fsbm.lias.dto.mandat;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record MandatRequest(
    @NotBlank String intitule,
    LocalDate dateDebut,
    LocalDate dateFin,
    boolean actif,
    Long directeurId,
    Long viceDirecteurId
) {}
