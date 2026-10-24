package ma.fsbm.lias.entity.enums;

/**
 * Statut d'appartenance d'un membre au laboratoire LIAS.
 * Le statut est strictement separe du role (responsabilite).
 */
public enum StatutMembre {
    PERMANENT,  // Membre permanent du laboratoire
    ASSOCIE,    // Membre d'un autre laboratoire, collaborateur du LIAS
    DOCTORANT,  // Doctorant avec compte personnel
    RETRAITE,   // Ancien membre en fin de carriere -> compte gele
    ANCIEN      // Membre ayant quitte -> compte desactive, historique conserve
}
