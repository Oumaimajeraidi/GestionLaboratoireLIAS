package ma.fsbm.lias.dto.demande;

import ma.fsbm.lias.entity.enums.StatutMembre;

/**
 * Decision du directeur sur une demande.
 * - acceptation : {@code statut} = statut a attribuer au nouveau membre (defaut PERMANENT).
 * - refus : {@code commentaire} = motif communique au demandeur.
 */
public record DecisionRequest(
        StatutMembre statut,
        String commentaire) {
}
