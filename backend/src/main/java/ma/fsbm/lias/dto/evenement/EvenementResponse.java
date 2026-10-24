package ma.fsbm.lias.dto.evenement;

import java.time.LocalDate;
import java.util.List;
import ma.fsbm.lias.dto.membre.MembreSummary;
import ma.fsbm.lias.entity.enums.TypeEvenement;

public record EvenementResponse(
        Long id,
        String titre,
        TypeEvenement type,
        String description,
        LocalDate dateDebut,
        LocalDate dateFin,
        String lieu,
        String edition,
        List<MembreSummary> organisateurs) {
}
