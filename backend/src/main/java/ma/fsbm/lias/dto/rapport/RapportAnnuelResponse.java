package ma.fsbm.lias.dto.rapport;

import java.util.List;
import java.util.Map;

public record RapportAnnuelResponse(
    int annee,
    // Membres
    long totalMembres,
    long nouveauxMembres,
    Map<String, Long> membresByStatut,
    // Publications
    long totalPublications,
    long publicationsAnnee,
    Map<String, Long> publicationsByType,
    // Événements
    long totalEvenements,
    long evenementsAnnee,
    Map<String, Long> evenementsByType,
    // Documents
    long totalDocuments,
    long documentsAnnee,
    // Conventions
    long conventionsActives,
    // Réunions
    long reunionsAnnee,
    // Top 5 publications par auteur
    List<Map<String, Object>> topAuteurs
) {}
