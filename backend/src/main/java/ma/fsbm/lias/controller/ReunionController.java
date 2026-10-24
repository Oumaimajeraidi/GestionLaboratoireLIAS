package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.reunion.ReunionRequest;
import ma.fsbm.lias.dto.reunion.ReunionResponse;
import ma.fsbm.lias.service.ReunionService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/reunions")
public class ReunionController {

    private final ReunionService reunionService;

    public ReunionController(ReunionService reunionService) {
        this.reunionService = reunionService;
    }

    @GetMapping
    public List<ReunionResponse> list() {
        return reunionService.findAll();
    }

    @GetMapping("/{id}")
    public ReunionResponse get(@PathVariable Long id) {
        return reunionService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<ReunionResponse> create(@Valid @RequestBody ReunionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reunionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ReunionResponse update(@PathVariable Long id, @Valid @RequestBody ReunionRequest request) {
        return reunionService.update(id, request);
    }

    @PostMapping("/{id}/pv")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ReunionResponse uploadPv(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        return reunionService.uploadPv(id, file);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reunionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
