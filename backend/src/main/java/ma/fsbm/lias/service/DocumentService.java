package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.document.DocumentDownload;
import ma.fsbm.lias.dto.document.DocumentResponse;
import ma.fsbm.lias.entity.Document;
import ma.fsbm.lias.entity.Evenement;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.enums.TypeDocument;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.DocumentRepository;
import ma.fsbm.lias.repository.EvenementRepository;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.UtilisateurRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final EvenementRepository evenementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreRepository membreRepository;
    private final FileStorageService fileStorageService;

    public DocumentService(DocumentRepository documentRepository,
                           EvenementRepository evenementRepository,
                           UtilisateurRepository utilisateurRepository,
                           MembreRepository membreRepository,
                           FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.evenementRepository = evenementRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.membreRepository = membreRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> findAll(Long evenementId, TypeDocument type) {
        List<Document> documents;
        if (evenementId != null) {
            documents = documentRepository.findByEvenement_IdOrderByCreatedAtDesc(evenementId);
        } else if (type != null) {
            documents = documentRepository.findByTypeDocumentOrderByCreatedAtDesc(type);
        } else {
            documents = documentRepository.findAllByOrderByCreatedAtDesc();
        }
        return documents.stream().map(this::toResponse).toList();
    }

    public DocumentResponse upload(MultipartFile file, String nom, TypeDocument typeDocument,
                                   String description, Long evenementId, String emailUtilisateur) {
        String storedName = fileStorageService.store(file);

        Document document = new Document();
        document.setNom(nom != null && !nom.isBlank() ? nom : file.getOriginalFilename());
        document.setTypeDocument(typeDocument != null ? typeDocument : TypeDocument.AUTRE);
        document.setDescription(description);
        document.setFileName(storedName);
        document.setOriginalName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setTaille(file.getSize());

        if (evenementId != null) {
            Evenement evenement = evenementRepository.findById(evenementId)
                    .orElseThrow(() -> ResourceNotFoundException.of("Evenement", evenementId));
            document.setEvenement(evenement);
        }

        if (emailUtilisateur != null) {
            utilisateurRepository.findByEmail(emailUtilisateur)
                    .flatMap(u -> membreRepository.findByUtilisateurId(u.getId()))
                    .ifPresent(document::setUploadePar);
        }

        return toResponse(documentRepository.save(document));
    }

    @Transactional(readOnly = true)
    public DocumentDownload download(Long id) {
        Document document = getEntity(id);
        Resource resource = fileStorageService.load(document.getFileName());
        String contentType = document.getContentType() != null
                ? document.getContentType() : "application/octet-stream";
        String filename = document.getOriginalName() != null
                ? document.getOriginalName() : document.getFileName();
        return new DocumentDownload(resource, filename, contentType);
    }

    public void delete(Long id) {
        Document document = getEntity(id);
        fileStorageService.delete(document.getFileName());
        documentRepository.delete(document);
    }

    private Document getEntity(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Document", id));
    }

    private DocumentResponse toResponse(Document d) {
        Evenement evenement = d.getEvenement();
        Membre uploadePar = d.getUploadePar();
        return new DocumentResponse(
                d.getId(), d.getNom(), d.getTypeDocument(), d.getDescription(),
                d.getOriginalName(), d.getContentType(), d.getTaille(),
                evenement != null ? evenement.getId() : null,
                evenement != null ? evenement.getTitre() : null,
                uploadePar != null ? uploadePar.getPrenom() + " " + uploadePar.getNom() : null,
                d.getCreatedAt());
    }
}
