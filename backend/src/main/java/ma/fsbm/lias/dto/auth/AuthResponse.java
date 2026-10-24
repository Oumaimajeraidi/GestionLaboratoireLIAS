package ma.fsbm.lias.dto.auth;

import java.util.Set;

/**
 * Reponse renvoyee apres une authentification reussie.
 */
public record AuthResponse(
        String token,
        String type,
        Long utilisateurId,
        String email,
        Set<String> roles,
        Long membreId,
        String nomComplet) {

    public static AuthResponse bearer(String token, Long utilisateurId, String email,
                                      Set<String> roles, Long membreId, String nomComplet) {
        return new AuthResponse(token, "Bearer", utilisateurId, email, roles, membreId, nomComplet);
    }
}
