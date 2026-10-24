package ma.fsbm.lias.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ma.fsbm.lias.dto.rapport.RapportAnnuelResponse;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.Publication;
import ma.fsbm.lias.entity.enums.StatutConvention;
import ma.fsbm.lias.repository.ConventionRepository;
import ma.fsbm.lias.repository.DocumentRepository;
import ma.fsbm.lias.repository.EvenementRepository;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.PublicationRepository;
import ma.fsbm.lias.repository.ReunionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RapportAnnuelService {

    private final MembreRepository membreRepository;
    private final PublicationRepository publicationRepository;
    private final EvenementRepository evenementRepository;
    private final DocumentRepository documentRepository;
    private final ConventionRepository conventionRepository;
    private final ReunionRepository reunionRepository;

    public RapportAnnuelService(MembreRepository membreRepository,
                                PublicationRepository publicationRepository,
                                EvenementRepository evenementRepository,
                                DocumentRepository documentRepository,
                                ConventionRepository conventionRepository,
                                ReunionRepository reunionRepository) {
        this.membreRepository = membreRepository;
        this.publicationRepository = publicationRepository;
        this.evenementRepository = evenementRepository;
        this.documentRepository = documentRepository;
        this.conventionRepository = conventionRepository;
        this.reunionRepository = reunionRepository;
    }

    public RapportAnnuelResponse generer(int annee) {
        LocalDate debut = LocalDate.of(annee, 1, 1);
        LocalDate fin = LocalDate.of(annee, 12, 31);

        List<Membre> tousLesMembres = membreRepository.findAll();

        // Membres stats
        long totalMembres = tousLesMembres.size();
        long nouveauxMembres = tousLesMembres.stream()
                .filter(m -> m.getDateAffiliation() != null
                        && !m.getDateAffiliation().isBefore(debut)
                        && !m.getDateAffiliation().isAfter(fin))
                .count();
        Map<String, Long> membresByStatut = tousLesMembres.stream()
                .collect(Collectors.groupingBy(m -> m.getStatut().name(), Collectors.counting()));

        // Publications stats
        List<Publication> toutesPublications = publicationRepository.findAll();
        long totalPublications = toutesPublications.size();
        long publicationsAnnee = toutesPublications.stream()
                .filter(p -> p.getAnnee() != null && p.getAnnee() == annee).count();
        Map<String, Long> pubsByType = toutesPublications.stream()
                .filter(p -> p.getAnnee() != null && p.getAnnee() == annee)
                .collect(Collectors.groupingBy(p -> p.getType().name(), Collectors.counting()));

        // Événements stats
        long totalEvenements = evenementRepository.count();
        long evenementsAnnee = evenementRepository.findAll().stream()
                .filter(e -> e.getDateDebut() != null
                        && !e.getDateDebut().isBefore(debut)
                        && !e.getDateDebut().isAfter(fin))
                .count();
        Map<String, Long> evByType = evenementRepository.findAll().stream()
                .filter(e -> e.getDateDebut() != null
                        && !e.getDateDebut().isBefore(debut)
                        && !e.getDateDebut().isAfter(fin))
                .collect(Collectors.groupingBy(e -> e.getType().name(), Collectors.counting()));

        // Documents
        long totalDocuments = documentRepository.count();
        long documentsAnnee = documentRepository.findAll().stream()
                .filter(d -> d.getCreatedAt() != null
                        && d.getCreatedAt().getYear() == annee)
                .count();

        // Conventions actives
        long conventionsActives = conventionRepository.findByStatut(StatutConvention.ACTIVE).size();

        // Réunions
        long reunionsAnnee = reunionRepository.findAll().stream()
                .filter(r -> r.getDateReunion() != null
                        && !r.getDateReunion().isBefore(debut)
                        && !r.getDateReunion().isAfter(fin))
                .count();

        // Top auteurs par publications de l'année
        Map<String, Long> auteurCount = new HashMap<>();
        toutesPublications.stream()
                .filter(p -> p.getAnnee() != null && p.getAnnee() == annee)
                .forEach(p -> p.getAuteurs().forEach(a -> {
                    String name = a.getPrenom() + " " + a.getNom();
                    auteurCount.merge(name, 1L, Long::sum);
                }));
        List<Map<String, Object>> topAuteurs = new ArrayList<>();
        auteurCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("nom", e.getKey());
                    entry.put("publications", e.getValue());
                    topAuteurs.add(entry);
                });

        return new RapportAnnuelResponse(
                annee, totalMembres, nouveauxMembres, membresByStatut,
                totalPublications, publicationsAnnee, pubsByType,
                totalEvenements, evenementsAnnee, evByType,
                totalDocuments, documentsAnnee,
                conventionsActives, reunionsAnnee, topAuteurs);
    }
}
