package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /** Messages reçus par un utilisateur (destinataire direct ou via équipe). */
    @Query("SELECT m FROM Message m WHERE m.destinataire.id = :userId " +
           "OR (m.equipe IS NOT NULL AND m.equipe.id IN " +
           "  (SELECT mb.equipeActuelle.id FROM Membre mb WHERE mb.utilisateur.id = :userId))")
    List<Message> findReceivedByUser(@Param("userId") Long userId);

    List<Message> findByExpediteurIdOrderByCreatedAtDesc(Long expediteurId);

    long countByDestinataireIdAndLuFalse(Long destinataireId);
}
