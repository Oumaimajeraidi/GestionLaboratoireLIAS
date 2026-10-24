package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.publication.PublicationRequest;
import ma.fsbm.lias.dto.publication.PublicationResponse;
import ma.fsbm.lias.service.PublicationService;
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
@RequestMapping("/api/publications")
public class PublicationController {

    private final PublicationService publicationService;

    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    /** Consultation globale (publique). Filtres optionnels : annee, auteurId, equipeId. */
    @GetMapping
    public List<PublicationResponse> list(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Long auteurId,
            @RequestParam(required = false) Long equipeId) {
        return publicationService.findAll(annee, auteurId, equipeId);
    }

    @GetMapping("/{id}")
    public PublicationResponse get(@PathVariable Long id) {
        return publicationService.findById(id);
    }

    /** Ajout par membres, doctorants, directeur ou admin. */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'MEMBRE', 'DOCTORANT')")
    public ResponseEntity<PublicationResponse> create(@Valid @RequestBody PublicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(publicationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'MEMBRE', 'DOCTORANT')")
    public PublicationResponse update(@PathVariable Long id, @Valid @RequestBody PublicationRequest request) {
        return publicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
