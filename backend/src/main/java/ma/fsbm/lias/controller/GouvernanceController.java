package ma.fsbm.lias.controller;

import java.util.List;
import ma.fsbm.lias.dto.gouvernance.AffectationRoleRequest;
import ma.fsbm.lias.dto.gouvernance.AffectationRoleResponse;
import ma.fsbm.lias.dto.gouvernance.AffiliationRequest;
import ma.fsbm.lias.dto.gouvernance.AffiliationResponse;
import ma.fsbm.lias.service.GouvernanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gouvernance")
public class GouvernanceController {

    private final GouvernanceService gouvernanceService;

    public GouvernanceController(GouvernanceService gouvernanceService) {
        this.gouvernanceService = gouvernanceService;
    }

    @GetMapping("/affiliations")
    public List<AffiliationResponse> listAffiliations() {
        return gouvernanceService.findAllAffiliations();
    }

    @GetMapping("/affiliations/membre/{membreId}")
    public List<AffiliationResponse> affiliationsByMembre(@PathVariable Long membreId) {
        return gouvernanceService.findAffiliationsByMembre(membreId);
    }

    @PostMapping("/affiliations")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<AffiliationResponse> createAffiliation(@RequestBody AffiliationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gouvernanceService.createAffiliation(request));
    }

    @GetMapping("/roles")
    public List<AffectationRoleResponse> listRoles() {
        return gouvernanceService.findAllAffectations();
    }

    @GetMapping("/roles/membre/{membreId}")
    public List<AffectationRoleResponse> rolesByMembre(@PathVariable Long membreId) {
        return gouvernanceService.findAffectationsByMembre(membreId);
    }

    @PostMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<AffectationRoleResponse> createRole(@RequestBody AffectationRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gouvernanceService.createAffectation(request));
    }
}
