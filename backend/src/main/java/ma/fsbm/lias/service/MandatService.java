package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.mandat.MandatRequest;
import ma.fsbm.lias.dto.mandat.MandatResponse;
import ma.fsbm.lias.entity.Mandat;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.MandatRepository;
import ma.fsbm.lias.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MandatService {

    private final MandatRepository mandatRepository;
    private final MembreRepository membreRepository;

    public MandatService(MandatRepository mandatRepository, MembreRepository membreRepository) {
        this.mandatRepository = mandatRepository;
        this.membreRepository = membreRepository;
    }

    @Transactional(readOnly = true)
    public List<MandatResponse> findAll() {
        return mandatRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MandatResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    public MandatResponse create(MandatRequest request) {
        Mandat mandat = new Mandat();
        applyRequest(mandat, request);
        if (request.actif()) {
            desactiverTousLesActifs();
        }
        return toResponse(mandatRepository.save(mandat));
    }

    public MandatResponse update(Long id, MandatRequest request) {
        Mandat mandat = getEntity(id);
        if (request.actif() && !mandat.isActif()) {
            desactiverTousLesActifs();
        }
        applyRequest(mandat, request);
        return toResponse(mandatRepository.save(mandat));
    }

    public void delete(Long id) {
        mandatRepository.delete(getEntity(id));
    }

    public MandatResponse setActif(Long id) {
        desactiverTousLesActifs();
        Mandat mandat = getEntity(id);
        mandat.setActif(true);
        return toResponse(mandatRepository.save(mandat));
    }

    private void desactiverTousLesActifs() {
        mandatRepository.findAll().stream()
                .filter(Mandat::isActif)
                .forEach(m -> { m.setActif(false); mandatRepository.save(m); });
    }

    private void applyRequest(Mandat mandat, MandatRequest request) {
        mandat.setIntitule(request.intitule());
        mandat.setDateDebut(request.dateDebut());
        mandat.setDateFin(request.dateFin());
        mandat.setActif(request.actif());

        if (request.directeurId() != null) {
            mandat.setDirecteur(membreRepository.findById(request.directeurId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Membre", request.directeurId())));
        } else {
            mandat.setDirecteur(null);
        }

        if (request.viceDirecteurId() != null) {
            mandat.setViceDirecteur(membreRepository.findById(request.viceDirecteurId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Membre", request.viceDirecteurId())));
        } else {
            mandat.setViceDirecteur(null);
        }
    }

    private Mandat getEntity(Long id) {
        return mandatRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Mandat", id));
    }

    private MandatResponse toResponse(Mandat m) {
        Membre dir = m.getDirecteur();
        Membre vdir = m.getViceDirecteur();
        return new MandatResponse(
                m.getId(),
                m.getIntitule(),
                m.getDateDebut(),
                m.getDateFin(),
                m.isActif(),
                dir != null ? dir.getId() : null,
                dir != null ? dir.getPrenom() + " " + dir.getNom() : null,
                vdir != null ? vdir.getId() : null,
                vdir != null ? vdir.getPrenom() + " " + vdir.getNom() : null,
                m.getCreatedAt());
    }
}
