package ma.fsbm.lias.dto.membre;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import ma.fsbm.lias.entity.enums.StatutMembre;

/**
 * Donnees d'entree pour la creation / mise a jour d'un profil membre.
 */
public record MembreRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prenom est obligatoire")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email,

        @NotNull(message = "Le statut est obligatoire")
        StatutMembre statut,

        String photoUrl,
        String etablissementOrigine,
        String laboratoireOrigine,
        String biographie,
        String centresInteret,
        LocalDate dateNaissance,
        LocalDate dateEmbauche,
        LocalDate dateAffiliation,
        Long equipeId) {
}
