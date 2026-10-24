package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.demande.DecisionRequest;
import ma.fsbm.lias.dto.demande.DemandeAccepteeResponse;
import ma.fsbm.lias.dto.demande.DemandeAdhesionRequest;
import ma.fsbm.lias.dto.demande.DemandeResponse;
import ma.fsbm.lias.entity.enums.StatutDemande;
import ma.fsbm.lias.service.DemandeAdhesionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demandes-adhesion")
public class DemandeAdhesionController {

    private final DemandeAdhesionService demandeService;

    public DemandeAdhesionController(DemandeAdhesionService demandeService) {
        this.demandeService = demandeService;
    }

    /** Soumission publique (aucune authentification requise). */
    @PostMapping
    public ResponseEntity<DemandeResponse> soumettre(@Valid @RequestBody DemandeAdhesionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(demandeService.soumettre(request));
    }

    /** Consultation reservee au directeur / admin. Filtre optionnel par statut. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public List<DemandeResponse> list(@RequestParam(required = false) StatutDemande statut) {
        return demandeService.findAll(statut);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public DemandeResponse get(@PathVariable Long id) {
        return demandeService.findById(id);
    }

    @PostMapping("/{id}/accepter")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public DemandeAccepteeResponse accepter(@PathVariable Long id, @RequestBody(required = false) DecisionRequest decision) {
        return demandeService.accepter(id, decision);
    }

    @PostMapping("/{id}/refuser")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public DemandeResponse refuser(@PathVariable Long id, @RequestBody(required = false) DecisionRequest decision) {
        return demandeService.refuser(id, decision);
    }
}
