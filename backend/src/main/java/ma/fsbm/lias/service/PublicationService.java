package ma.fsbm.lias.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import ma.fsbm.lias.dto.membre.MembreSummary;
import ma.fsbm.lias.dto.publication.PublicationRequest;
import ma.fsbm.lias.dto.publication.PublicationResponse;
import ma.fsbm.lias.entity.Equipe;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.Publication;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.EquipeRepository;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.PublicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final MembreRepository membreRepository;
    private final EquipeRepository equipeRepository;

    public PublicationService(PublicationRepository publicationRepository,
                              MembreRepository membreRepository,
                              EquipeRepository equipeRepository) {
        this.publicationRepository = publicationRepository;
        this.membreRepository = membreRepository;
        this.equipeRepository = equipeRepository;
    }

    @Transactional(readOnly = true)
    public List<PublicationResponse> findAll(Integer annee, Long auteurId, Long equipeId) {
        List<Publication> publications;
        if (annee != null) {
            publications = publicationRepository.findByAnneeOrderByIdDesc(annee);
        } else if (auteurId != null) {
            publications = publicationRepository.findByAuteurs_IdOrderByAnneeDesc(auteurId);
        } else if (equipeId != null) {
            publications = publicationRepository.findByEquipe_IdOrderByAnneeDesc(equipeId);
        } else {
            publications = publicationRepository.findAllByOrderByAnneeDescIdDesc();
        }
        return publications.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PublicationResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    public PublicationResponse create(PublicationRequest request) {
        Publication publication = new Publication();
        applyRequest(publication, request);
        return toResponse(publicationRepository.save(publication));
    }

    public PublicationResponse update(Long id, PublicationRequest request) {
        Publication publication = getEntity(id);
        applyRequest(publication, request);
        return toResponse(publicationRepository.save(publication));
    }

    public void delete(Long id) {
        publicationRepository.delete(getEntity(id));
    }

    private Publication getEntity(Long id) {
        return publicationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Publication", id));
    }

    private void applyRequest(Publication publication, PublicationRequest request) {
        publication.setTitre(request.titre());
        publication.setType(request.type());
        publication.setAnnee(request.annee());
        publication.setVenue(request.venue());
        publication.setDoi(request.doi());
        publication.setUrl(request.url());
        publication.setResume(request.resume());

        if (request.auteurIds() != null && !request.auteurIds().isEmpty()) {
            List<Membre> auteurs = membreRepository.findAllById(request.auteurIds());
            publication.setAuteurs(new HashSet<>(auteurs));
        } else {
            publication.setAuteurs(new HashSet<>());
        }

        if (request.equipeId() != null) {
            Equipe equipe = equipeRepository.findById(request.equipeId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Equipe", request.equipeId()));
            publication.setEquipe(equipe);
        } else {
            publication.setEquipe(null);
        }
    }

    private PublicationResponse toResponse(Publication p) {
        List<MembreSummary> auteurs = p.getAuteurs().stream()
                .map(m -> new MembreSummary(m.getId(), m.getPrenom() + " " + m.getNom()))
                .sorted(Comparator.comparing(MembreSummary::nomComplet))
                .toList();
        Equipe equipe = p.getEquipe();
        return new PublicationResponse(
                p.getId(), p.getTitre(), p.getType(), p.getAnnee(), p.getVenue(),
                p.getDoi(), p.getUrl(), p.getResume(), auteurs,
                equipe != null ? equipe.getId() : null,
                equipe != null ? equipe.getNom() : null);
    }
}
