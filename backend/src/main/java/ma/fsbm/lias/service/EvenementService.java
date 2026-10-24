package ma.fsbm.lias.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import ma.fsbm.lias.dto.evenement.EvenementRequest;
import ma.fsbm.lias.dto.evenement.EvenementResponse;
import ma.fsbm.lias.dto.membre.MembreSummary;
import ma.fsbm.lias.entity.Evenement;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.enums.TypeEvenement;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.EvenementRepository;
import ma.fsbm.lias.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final MembreRepository membreRepository;

    public EvenementService(EvenementRepository evenementRepository, MembreRepository membreRepository) {
        this.evenementRepository = evenementRepository;
        this.membreRepository = membreRepository;
    }

    @Transactional(readOnly = true)
    public List<EvenementResponse> findAll(TypeEvenement type) {
        List<Evenement> evenements = (type != null)
                ? evenementRepository.findByTypeOrderByDateDebutDesc(type)
                : evenementRepository.findAllByOrderByDateDebutDesc();
        return evenements.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EvenementResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    public EvenementResponse create(EvenementRequest request) {
        Evenement evenement = new Evenement();
        applyRequest(evenement, request);
        return toResponse(evenementRepository.save(evenement));
    }

    public EvenementResponse update(Long id, EvenementRequest request) {
        Evenement evenement = getEntity(id);
        applyRequest(evenement, request);
        return toResponse(evenementRepository.save(evenement));
    }

    public void delete(Long id) {
        evenementRepository.delete(getEntity(id));
    }

    private Evenement getEntity(Long id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Evenement", id));
    }

    private void applyRequest(Evenement evenement, EvenementRequest request) {
        evenement.setTitre(request.titre());
        evenement.setType(request.type());
        evenement.setDescription(request.description());
        evenement.setDateDebut(request.dateDebut());
        evenement.setDateFin(request.dateFin());
        evenement.setLieu(request.lieu());
        evenement.setEdition(request.edition());

        if (request.organisateurIds() != null && !request.organisateurIds().isEmpty()) {
            List<Membre> organisateurs = membreRepository.findAllById(request.organisateurIds());
            evenement.setOrganisateurs(new HashSet<>(organisateurs));
        } else {
            evenement.setOrganisateurs(new HashSet<>());
        }
    }

    private EvenementResponse toResponse(Evenement e) {
        List<MembreSummary> organisateurs = e.getOrganisateurs().stream()
                .map(m -> new MembreSummary(m.getId(), m.getPrenom() + " " + m.getNom()))
                .sorted(Comparator.comparing(MembreSummary::nomComplet))
                .toList();
        return new EvenementResponse(
                e.getId(), e.getTitre(), e.getType(), e.getDescription(),
                e.getDateDebut(), e.getDateFin(), e.getLieu(), e.getEdition(), organisateurs);
    }
}
