package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.reunion.ReunionRequest;
import ma.fsbm.lias.dto.reunion.ReunionResponse;
import ma.fsbm.lias.entity.Equipe;
import ma.fsbm.lias.entity.Reunion;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.EquipeRepository;
import ma.fsbm.lias.repository.ReunionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ReunionService {

    private final ReunionRepository reunionRepository;
    private final EquipeRepository equipeRepository;
    private final FileStorageService fileStorageService;

    public ReunionService(ReunionRepository reunionRepository,
                          EquipeRepository equipeRepository,
                          FileStorageService fileStorageService) {
        this.reunionRepository = reunionRepository;
        this.equipeRepository = equipeRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<ReunionResponse> findAll() {
        return reunionRepository.findByOrderByDateReunionDesc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ReunionResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    public ReunionResponse create(ReunionRequest request) {
        Reunion reunion = new Reunion();
        applyRequest(reunion, request);
        return toResponse(reunionRepository.save(reunion));
    }

    public ReunionResponse update(Long id, ReunionRequest request) {
        Reunion reunion = getEntity(id);
        applyRequest(reunion, request);
        return toResponse(reunionRepository.save(reunion));
    }

    public void delete(Long id) {
        reunionRepository.delete(getEntity(id));
    }

    public ReunionResponse uploadPv(Long id, MultipartFile file) {
        Reunion reunion = getEntity(id);
        String storedName = fileStorageService.store(file);
        reunion.setPvFichierPath(storedName);
        reunion.setPvFichierNom(file.getOriginalFilename());
        return toResponse(reunionRepository.save(reunion));
    }

    private void applyRequest(Reunion reunion, ReunionRequest request) {
        reunion.setTitre(request.titre());
        reunion.setType(request.type());
        reunion.setDateReunion(request.dateReunion());
        reunion.setLieu(request.lieu());
        reunion.setOrdreJour(request.ordreJour());
        if (request.equipeId() != null) {
            Equipe equipe = equipeRepository.findById(request.equipeId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Equipe", request.equipeId()));
            reunion.setEquipe(equipe);
        } else {
            reunion.setEquipe(null);
        }
    }

    private Reunion getEntity(Long id) {
        return reunionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Reunion", id));
    }

    private ReunionResponse toResponse(Reunion r) {
        Equipe eq = r.getEquipe();
        return new ReunionResponse(r.getId(), r.getTitre(), r.getType(), r.getDateReunion(),
                r.getLieu(), r.getOrdreJour(),
                eq != null ? eq.getId() : null,
                eq != null ? eq.getNom() : null,
                r.getPvFichierNom(),
                r.getPvFichierPath() != null,
                r.getCreatedAt());
    }
}
