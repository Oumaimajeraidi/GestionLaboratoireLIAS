package ma.fsbm.lias.entity.enums;

/**
 * Roles de securite utilises par Spring Security pour l'autorisation.
 * A ne pas confondre avec {@link TypeResponsabilite} (responsabilite metier liee a un mandat).
 */
public enum RoleSecurite {
    ADMIN,      // Administrateur systeme (gestion technique)
    DIRECTEUR,  // Directeur du laboratoire en mandat actif
    MEMBRE,     // Membre permanent (acces complet)
    ASSOCIE,    // Membre associe (acces limite)
    DOCTORANT   // Doctorant (acces tres limite : publications)
}
