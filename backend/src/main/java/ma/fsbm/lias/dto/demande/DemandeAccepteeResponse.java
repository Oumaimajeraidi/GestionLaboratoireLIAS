package ma.fsbm.lias.dto.demande;

/**
 * Resultat de l'acceptation : le compte est cree et un mot de passe temporaire
 * est genere (a communiquer au nouveau membre, faute de service d'email).
 */
public record DemandeAccepteeResponse(
        Long demandeId,
        Long membreId,
        Long utilisateurId,
        String email,
        String motDePasseTemporaire) {
}
