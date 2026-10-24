package ma.fsbm.lias.dto.demande;

import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.StatutDemande;

public record DemandeResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String etablissementOrigine,
        String motivation,
        String cvUrl,
        StatutDemande statut,
        String commentaireDecision,
        LocalDateTime dateDecision,
        LocalDateTime createdAt) {
}
