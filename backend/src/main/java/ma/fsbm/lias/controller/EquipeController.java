package ma.fsbm.lias.controller;

import java.util.List;
import ma.fsbm.lias.dto.equipe.EquipeResponse;
import ma.fsbm.lias.repository.EquipeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consultation des equipes (publique). Utilisee notamment pour les listes deroulantes du front.
 */
@RestController
@RequestMapping("/api/equipes")
public class EquipeController {

    private final EquipeRepository equipeRepository;

    public EquipeController(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
    }

    @GetMapping
    public List<EquipeResponse> list() {
        return equipeRepository.findAll().stream()
                .map(e -> new EquipeResponse(e.getId(), e.getNom(), e.getDescription(), e.getThematique()))
                .toList();
    }
}
