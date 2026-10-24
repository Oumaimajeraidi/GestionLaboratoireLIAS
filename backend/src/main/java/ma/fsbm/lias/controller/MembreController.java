package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.membre.MembreRequest;
import ma.fsbm.lias.dto.membre.MembreResponse;
import ma.fsbm.lias.service.MembreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membres")
public class MembreController {

    private final MembreService membreService;

    public MembreController(MembreService membreService) {
        this.membreService = membreService;
    }

    @GetMapping
    public List<MembreResponse> list(@RequestParam(name = "q", required = false) String query) {
        if (query != null && !query.isBlank()) {
            return membreService.search(query);
        }
        return membreService.findAll();
    }

    @GetMapping("/{id}")
    public MembreResponse get(@PathVariable Long id) {
        return membreService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<MembreResponse> create(@Valid @RequestBody MembreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membreService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MembreResponse update(@PathVariable Long id, @Valid @RequestBody MembreRequest request) {
        return membreService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membreService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MembreResponse desactiver(@PathVariable Long id) {
        return membreService.desactiver(id);
    }

    @PatchMapping("/{id}/retraite")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MembreResponse mettreEnRetraite(@PathVariable Long id) {
        return membreService.mettreEnRetraite(id);
    }

    @PatchMapping("/{id}/reactiver")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MembreResponse reactiver(@PathVariable Long id) {
        return membreService.reactiver(id);
    }
}
