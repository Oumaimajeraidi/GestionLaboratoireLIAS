package ma.fsbm.lias.dto.publication;

import java.util.List;
import ma.fsbm.lias.dto.membre.MembreSummary;
import ma.fsbm.lias.entity.enums.TypePublication;

public record PublicationResponse(
        Long id,
        String titre,
        TypePublication type,
        Integer annee,
        String venue,
        String doi,
        String url,
        String resume,
        List<MembreSummary> auteurs,
        Long equipeId,
        String equipeNom) {
}
