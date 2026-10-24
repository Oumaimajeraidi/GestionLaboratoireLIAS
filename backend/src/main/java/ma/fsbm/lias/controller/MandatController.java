package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.mandat.MandatRequest;
import ma.fsbm.lias.dto.mandat.MandatResponse;
import ma.fsbm.lias.service.MandatService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mandats")
public class MandatController {

    private final MandatService mandatService;

    public MandatController(MandatService mandatService) {
        this.mandatService = mandatService;
    }

    @GetMapping
    public List<MandatResponse> list() {
        return mandatService.findAll();
    }

    @GetMapping("/{id}")
    public MandatResponse get(@PathVariable Long id) {
        return mandatService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<MandatResponse> create(@Valid @RequestBody MandatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mandatService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MandatResponse update(@PathVariable Long id, @Valid @RequestBody MandatRequest request) {
        return mandatService.update(id, request);
    }

    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MandatResponse setActif(@PathVariable Long id) {
        return mandatService.setActif(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mandatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
