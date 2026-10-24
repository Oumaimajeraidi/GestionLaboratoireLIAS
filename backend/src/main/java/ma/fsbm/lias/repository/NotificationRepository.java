package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireIdOrderByCreatedAtDesc(Long destinataireId);
    long countByDestinataireIdAndLuFalse(Long destinataireId);
}
