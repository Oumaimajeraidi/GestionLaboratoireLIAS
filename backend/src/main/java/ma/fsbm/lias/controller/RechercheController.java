package ma.fsbm.lias.controller;

import java.util.ArrayList;
import java.util.List;
import ma.fsbm.lias.dto.recherche.SearchResultItem;
import ma.fsbm.lias.repository.DocumentRepository;
import ma.fsbm.lias.repository.EvenementRepository;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.PublicationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recherche")
public class RechercheController {

    private final MembreRepository membreRepository;
    private final PublicationRepository publicationRepository;
    private final EvenementRepository evenementRepository;
    private final DocumentRepository documentRepository;

    public RechercheController(MembreRepository membreRepository,
                               PublicationRepository publicationRepository,
                               EvenementRepository evenementRepository,
                               DocumentRepository documentRepository) {
        this.membreRepository = membreRepository;
        this.publicationRepository = publicationRepository;
        this.evenementRepository = evenementRepository;
        this.documentRepository = documentRepository;
    }

    @GetMapping
    public List<SearchResultItem> search(@RequestParam String q) {
        List<SearchResultItem> results = new ArrayList<>();
        String query = q.toLowerCase();

        membreRepository.findAll().stream()
                .filter(m -> (m.getNom() + " " + m.getPrenom()).toLowerCase().contains(query)
                        || (m.getEmail() != null && m.getEmail().toLowerCase().contains(query)))
                .limit(5)
                .forEach(m -> results.add(new SearchResultItem(
                        "membre", m.getId(), m.getPrenom() + " " + m.getNom(),
                        m.getStatut() != null ? m.getStatut().name() : "",
                        "/membres/" + m.getId())));

        publicationRepository.findAll().stream()
                .filter(p -> p.getTitre() != null && p.getTitre().toLowerCase().contains(query))
                .limit(5)
                .forEach(p -> results.add(new SearchResultItem(
                        "publication", p.getId(), p.getTitre(),
                        p.getVenue() != null ? p.getVenue() : "",
                        "/publications")));

        evenementRepository.findAll().stream()
                .filter(e -> e.getTitre() != null && e.getTitre().toLowerCase().contains(query))
                .limit(5)
                .forEach(e -> results.add(new SearchResultItem(
                        "evenement", e.getId(), e.getTitre(),
                        e.getLieu() != null ? e.getLieu() : "",
                        "/evenements")));

        documentRepository.findAll().stream()
                .filter(d -> d.getNom() != null && d.getNom().toLowerCase().contains(query))
                .limit(5)
                .forEach(d -> results.add(new SearchResultItem(
                        "document", d.getId(), d.getNom(),
                        d.getTypeDocument() != null ? d.getTypeDocument().name() : "",
                        "/documents")));

        return results;
    }
}
