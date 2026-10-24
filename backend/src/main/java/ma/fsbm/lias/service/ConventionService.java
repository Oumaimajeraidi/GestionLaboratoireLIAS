package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.convention.ConventionRequest;
import ma.fsbm.lias.dto.convention.ConventionResponse;
import ma.fsbm.lias.dto.convention.PartenaireRequest;
import ma.fsbm.lias.dto.convention.PartenaireResponse;
import ma.fsbm.lias.entity.Convention;
import ma.fsbm.lias.entity.Partenaire;
import ma.fsbm.lias.entity.enums.StatutConvention;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.ConventionRepository;
import ma.fsbm.lias.repository.PartenaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConventionService {

    private final ConventionRepository conventionRepository;
    private final PartenaireRepository partenaireRepository;

    public ConventionService(ConventionRepository conventionRepository,
                             PartenaireRepository partenaireRepository) {
        this.conventionRepository = conventionRepository;
        this.partenaireRepository = partenaireRepository;
    }

    // ---- Partenaires ----

    @Transactional(readOnly = true)
    public List<PartenaireResponse> findAllPartenaires() {
        return partenaireRepository.findAll().stream().map(this::toPartenaireResponse).toList();
    }

    public PartenaireResponse createPartenaire(PartenaireRequest request) {
        Partenaire p = new Partenaire();
        applyPartenaire(p, request);
        return toPartenaireResponse(partenaireRepository.save(p));
    }

    public PartenaireResponse updatePartenaire(Long id, PartenaireRequest request) {
        Partenaire p = partenaireRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Partenaire", id));
        applyPartenaire(p, request);
        return toPartenaireResponse(partenaireRepository.save(p));
    }

    public void deletePartenaire(Long id) {
        partenaireRepository.delete(partenaireRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Partenaire", id)));
    }

    private void applyPartenaire(Partenaire p, PartenaireRequest request) {
        p.setNom(request.nom());
        p.setPays(request.pays());
        p.setTypeInstitution(request.typeInstitution());
        p.setContactNom(request.contactNom());
        p.setContactEmail(request.contactEmail());
    }

    private PartenaireResponse toPartenaireResponse(Partenaire p) {
        return new PartenaireResponse(p.getId(), p.getNom(), p.getPays(), p.getTypeInstitution(),
                p.getContactNom(), p.getContactEmail(), p.getCreatedAt());
    }

    // ---- Conventions ----

    @Transactional(readOnly = true)
    public List<ConventionResponse> findAllConventions() {
        return conventionRepository.findAll().stream().map(this::toConventionResponse).toList();
    }

    public ConventionResponse createConvention(ConventionRequest request) {
        Convention c = new Convention();
        applyConvention(c, request);
        return toConventionResponse(conventionRepository.save(c));
    }

    public ConventionResponse updateConvention(Long id, ConventionRequest request) {
        Convention c = conventionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Convention", id));
        applyConvention(c, request);
        return toConventionResponse(conventionRepository.save(c));
    }

    public void deleteConvention(Long id) {
        conventionRepository.delete(conventionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Convention", id)));
    }

    private void applyConvention(Convention c, ConventionRequest request) {
        c.setIntitule(request.intitule());
        c.setType(request.type());
        c.setStatut(request.statut() != null ? request.statut() : StatutConvention.EN_NEGOCIATION);
        c.setDateDebut(request.dateDebut());
        c.setDateFin(request.dateFin());
        c.setObjectif(request.objectif());
        if (request.partenaireId() != null) {
            c.setPartenaire(partenaireRepository.findById(request.partenaireId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Partenaire", request.partenaireId())));
        } else {
            c.setPartenaire(null);
        }
    }

    private ConventionResponse toConventionResponse(Convention c) {
        Partenaire p = c.getPartenaire();
        return new ConventionResponse(c.getId(), c.getIntitule(), c.getType(), c.getStatut(),
                p != null ? p.getId() : null,
                p != null ? p.getNom() : null,
                p != null ? p.getPays() : null,
                c.getDateDebut(), c.getDateFin(), c.getObjectif(),
                c.getFichierNom(), c.getCreatedAt());
    }
}
