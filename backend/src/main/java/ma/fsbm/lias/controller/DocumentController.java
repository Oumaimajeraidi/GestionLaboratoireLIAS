package ma.fsbm.lias.controller;

import java.util.List;
import ma.fsbm.lias.dto.document.DocumentDownload;
import ma.fsbm.lias.dto.document.DocumentResponse;
import ma.fsbm.lias.entity.enums.TypeDocument;
import ma.fsbm.lias.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<DocumentResponse> list(
            @RequestParam(required = false) Long evenementId,
            @RequestParam(required = false) TypeDocument type) {
        return documentService.findAll(evenementId, type);
    }

    /** Upload multipart (membres authentifies). */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR', 'MEMBRE', 'DOCTORANT')")
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "nom", required = false) String nom,
            @RequestParam(value = "typeDocument", required = false) TypeDocument typeDocument,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "evenementId", required = false) Long evenementId,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        DocumentResponse response = documentService.upload(file, nom, typeDocument, description, evenementId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<org.springframework.core.io.Resource> download(@PathVariable Long id) {
        DocumentDownload download = documentService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.filename() + "\"")
                .body(download.resource());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
