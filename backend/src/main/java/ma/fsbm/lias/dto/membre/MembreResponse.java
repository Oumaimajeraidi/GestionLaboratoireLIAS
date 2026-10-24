package ma.fsbm.lias.dto.membre;

import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.StatutMembre;

/**
 * Representation d'un membre renvoyee par l'API.
 */
public record MembreResponse(
        Long id,
        String nom,
        String prenom,
        String email,
        String photoUrl,
        StatutMembre statut,
        String etablissementOrigine,
        String laboratoireOrigine,
        String biographie,
        String centresInteret,
        LocalDate dateEmbauche,
        LocalDate dateAffiliation,
        Long equipeId,
        String equipeNom,
        Long utilisateurId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
