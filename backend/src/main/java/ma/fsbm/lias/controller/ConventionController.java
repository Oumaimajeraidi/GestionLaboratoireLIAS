package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.convention.ConventionRequest;
import ma.fsbm.lias.dto.convention.ConventionResponse;
import ma.fsbm.lias.dto.convention.PartenaireRequest;
import ma.fsbm.lias.dto.convention.PartenaireResponse;
import ma.fsbm.lias.service.ConventionService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conventions")
public class ConventionController {

    private final ConventionService conventionService;

    public ConventionController(ConventionService conventionService) {
        this.conventionService = conventionService;
    }

    // ---- Partenaires ----

    @GetMapping("/partenaires")
    public List<PartenaireResponse> listPartenaires() {
        return conventionService.findAllPartenaires();
    }

    @PostMapping("/partenaires")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<PartenaireResponse> createPartenaire(@Valid @RequestBody PartenaireRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conventionService.createPartenaire(request));
    }

    @PutMapping("/partenaires/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public PartenaireResponse updatePartenaire(@PathVariable Long id, @Valid @RequestBody PartenaireRequest request) {
        return conventionService.updatePartenaire(id, request);
    }

    @DeleteMapping("/partenaires/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePartenaire(@PathVariable Long id) {
        conventionService.deletePartenaire(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Conventions ----

    @GetMapping
    public List<ConventionResponse> list() {
        return conventionService.findAllConventions();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<ConventionResponse> create(@Valid @RequestBody ConventionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conventionService.createConvention(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ConventionResponse update(@PathVariable Long id, @Valid @RequestBody ConventionRequest request) {
        return conventionService.updateConvention(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        conventionService.deleteConvention(id);
        return ResponseEntity.noContent().build();
    }
}
