package ma.fsbm.lias.dto.document;

import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.TypeDocument;

public record DocumentResponse(
        Long id,
        String nom,
        TypeDocument typeDocument,
        String description,
        String originalName,
        String contentType,
        long taille,
        Long evenementId,
        String evenementTitre,
        String uploadePar,
        LocalDateTime createdAt) {
}
