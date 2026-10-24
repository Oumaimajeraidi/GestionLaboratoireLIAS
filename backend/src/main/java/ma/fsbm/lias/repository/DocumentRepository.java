package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Document;
import ma.fsbm.lias.entity.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findAllByOrderByCreatedAtDesc();

    List<Document> findByEvenement_IdOrderByCreatedAtDesc(Long evenementId);

    List<Document> findByTypeDocumentOrderByCreatedAtDesc(TypeDocument typeDocument);
}
