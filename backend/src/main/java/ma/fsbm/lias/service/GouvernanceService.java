package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.gouvernance.AffectationRoleRequest;
import ma.fsbm.lias.dto.gouvernance.AffectationRoleResponse;
import ma.fsbm.lias.dto.gouvernance.AffiliationRequest;
import ma.fsbm.lias.dto.gouvernance.AffiliationResponse;
import ma.fsbm.lias.entity.AffectationRole;
import ma.fsbm.lias.entity.Affiliation;
import ma.fsbm.lias.entity.Equipe;
import ma.fsbm.lias.entity.Mandat;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.AffectationRoleRepository;
import ma.fsbm.lias.repository.AffiliationRepository;
import ma.fsbm.lias.repository.EquipeRepository;
import ma.fsbm.lias.repository.MandatRepository;
import ma.fsbm.lias.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GouvernanceService {

    private final AffiliationRepository affiliationRepository;
    private final AffectationRoleRepository affectationRoleRepository;
    private final MembreRepository membreRepository;
    private final EquipeRepository equipeRepository;
    private final MandatRepository mandatRepository;

    public GouvernanceService(AffiliationRepository affiliationRepository,
                              AffectationRoleRepository affectationRoleRepository,
                              MembreRepository membreRepository,
                              EquipeRepository equipeRepository,
                              MandatRepository mandatRepository) {
        this.affiliationRepository = affiliationRepository;
        this.affectationRoleRepository = affectationRoleRepository;
        this.membreRepository = membreRepository;
        this.equipeRepository = equipeRepository;
        this.mandatRepository = mandatRepository;
    }

    @Transactional(readOnly = true)
    public List<AffiliationResponse> findAllAffiliations() {
        return affiliationRepository.findAll().stream().map(this::toAffiliationResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AffiliationResponse> findAffiliationsByMembre(Long membreId) {
        return affiliationRepository.findAll().stream()
                .filter(a -> a.getMembre().getId().equals(membreId))
                .map(this::toAffiliationResponse).toList();
    }

    public AffiliationResponse createAffiliation(AffiliationRequest request) {
        Membre membre = membreRepository.findById(request.membreId())
                .orElseThrow(() -> ResourceNotFoundException.of("Membre", request.membreId()));
        Affiliation affiliation = new Affiliation();
        affiliation.setMembre(membre);
        affiliation.setDateDebut(request.dateDebut());
        affiliation.setDateFin(request.dateFin());
        affiliation.setMotif(request.motif());
        return toAffiliationResponse(affiliationRepository.save(affiliation));
    }

    @Transactional(readOnly = true)
    public List<AffectationRoleResponse> findAllAffectations() {
        return affectationRoleRepository.findAll().stream().map(this::toAffectationResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AffectationRoleResponse> findAffectationsByMembre(Long membreId) {
        return affectationRoleRepository.findAll().stream()
                .filter(a -> a.getMembre().getId().equals(membreId))
                .map(this::toAffectationResponse).toList();
    }

    public AffectationRoleResponse createAffectation(AffectationRoleRequest request) {
        Membre membre = membreRepository.findById(request.membreId())
                .orElseThrow(() -> ResourceNotFoundException.of("Membre", request.membreId()));
        AffectationRole affectation = new AffectationRole();
        affectation.setMembre(membre);
        affectation.setResponsabilite(request.responsabilite());
        affectation.setDateDebut(request.dateDebut());
        affectation.setDateFin(request.dateFin());

        if (request.equipeId() != null) {
            Equipe equipe = equipeRepository.findById(request.equipeId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Equipe", request.equipeId()));
            affectation.setEquipe(equipe);
        }
        if (request.mandatId() != null) {
            Mandat mandat = mandatRepository.findById(request.mandatId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Mandat", request.mandatId()));
            affectation.setMandat(mandat);
        }
        return toAffectationResponse(affectationRoleRepository.save(affectation));
    }

    private AffiliationResponse toAffiliationResponse(Affiliation a) {
        return new AffiliationResponse(
                a.getId(),
                a.getMembre().getId(),
                a.getMembre().getPrenom() + " " + a.getMembre().getNom(),
                a.getDateDebut(),
                a.getDateFin(),
                a.getMotif(),
                a.getCreatedAt());
    }

    private AffectationRoleResponse toAffectationResponse(AffectationRole a) {
        return new AffectationRoleResponse(
                a.getId(),
                a.getMembre().getId(),
                a.getMembre().getPrenom() + " " + a.getMembre().getNom(),
                a.getResponsabilite(),
                a.getEquipe() != null ? a.getEquipe().getId() : null,
                a.getEquipe() != null ? a.getEquipe().getNom() : null,
                a.getMandat() != null ? a.getMandat().getId() : null,
                a.getDateDebut(),
                a.getDateFin(),
                a.getCreatedAt());
    }
}
