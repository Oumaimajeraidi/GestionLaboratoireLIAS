package ma.fsbm.lias.entity.enums;

/**
 * Type de responsabilite (role metier) au sein de la gouvernance du laboratoire.
 * Un membre peut changer de role ; aucun changement ne supprime les donnees (historisation).
 */
public enum TypeResponsabilite {
    DIRECTEUR,
    VICE_DIRECTEUR,
    CHEF_EQUIPE,
    MEMBRE_EQUIPE
}
