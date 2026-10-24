package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.materiel.BesoinRequest;
import ma.fsbm.lias.dto.materiel.BesoinResponse;
import ma.fsbm.lias.dto.materiel.MaterielRequest;
import ma.fsbm.lias.dto.materiel.MaterielResponse;
import ma.fsbm.lias.service.MaterielService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ma.fsbm.lias.security.UserPrincipal;

@RestController
@RequestMapping("/api/materiels")
public class MaterielController {

    private final MaterielService materielService;

    public MaterielController(MaterielService materielService) {
        this.materielService = materielService;
    }

    @GetMapping
    public List<MaterielResponse> list() {
        return materielService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<MaterielResponse> create(@Valid @RequestBody MaterielRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materielService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MaterielResponse update(@PathVariable Long id, @Valid @RequestBody MaterielRequest request) {
        return materielService.update(id, request);
    }

    @PatchMapping("/{id}/attribuer")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MaterielResponse attribuer(@PathVariable Long id, @RequestParam Long membreId) {
        return materielService.attribuer(id, membreId);
    }

    @PatchMapping("/{id}/retourner")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public MaterielResponse retourner(@PathVariable Long id) {
        return materielService.retourner(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        materielService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Besoins ----

    @GetMapping("/besoins")
    public List<BesoinResponse> listBesoins() {
        return materielService.findAllBesoins();
    }

    @PostMapping("/besoins")
    public ResponseEntity<BesoinResponse> createBesoin(@Valid @RequestBody BesoinRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(materielService.createBesoin(request));
    }

    @PatchMapping("/besoins/{id}/valider")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public BesoinResponse valider(@PathVariable Long id,
                                  @RequestParam Long valideurId,
                                  @RequestParam(required = false) String commentaire) {
        return materielService.validerBesoin(id, valideurId, commentaire);
    }

    @PatchMapping("/besoins/{id}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public BesoinResponse refuser(@PathVariable Long id,
                                  @RequestParam Long valideurId,
                                  @RequestParam(required = false) String commentaire) {
        return materielService.refuserBesoin(id, valideurId, commentaire);
    }
}
