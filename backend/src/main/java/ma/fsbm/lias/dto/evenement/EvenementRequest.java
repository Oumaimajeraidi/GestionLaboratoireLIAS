package ma.fsbm.lias.dto.evenement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import ma.fsbm.lias.entity.enums.TypeEvenement;

public record EvenementRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String titre,

        @NotNull(message = "Le type est obligatoire")
        TypeEvenement type,

        String description,
        LocalDate dateDebut,
        LocalDate dateFin,
        String lieu,
        String edition,
        Set<Long> organisateurIds) {
}
