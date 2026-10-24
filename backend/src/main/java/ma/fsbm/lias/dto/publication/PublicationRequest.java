package ma.fsbm.lias.dto.publication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import ma.fsbm.lias.entity.enums.TypePublication;

public record PublicationRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String titre,

        @NotNull(message = "Le type est obligatoire")
        TypePublication type,

        Integer annee,
        String venue,
        String doi,
        String url,
        String resume,
        Set<Long> auteurIds,
        Long equipeId) {
}
