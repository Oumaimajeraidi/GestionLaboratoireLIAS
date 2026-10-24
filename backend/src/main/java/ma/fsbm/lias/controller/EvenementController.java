package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.evenement.EvenementRequest;
import ma.fsbm.lias.dto.evenement.EvenementResponse;
import ma.fsbm.lias.entity.enums.TypeEvenement;
import ma.fsbm.lias.service.EvenementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final EvenementService evenementService;

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    /** Consultation publique (visiteurs). Filtre optionnel par type. */
    @GetMapping
    public List<EvenementResponse> list(@RequestParam(required = false) TypeEvenement type) {
        return evenementService.findAll(type);
    }

    @GetMapping("/{id}")
    public EvenementResponse get(@PathVariable Long id) {
        return evenementService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'MEMBRE')")
    public ResponseEntity<EvenementResponse> create(@Valid @RequestBody EvenementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evenementService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'MEMBRE')")
    public EvenementResponse update(@PathVariable Long id, @Valid @RequestBody EvenementRequest request) {
        return evenementService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        evenementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
