package ma.fsbm.lias.dto.demande;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Donnees soumises publiquement pour une demande d'adhesion.
 */
public record DemandeAdhesionRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prenom est obligatoire")
        String prenom,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email,

        String etablissementOrigine,
        String motivation,
        String cvUrl) {
}
