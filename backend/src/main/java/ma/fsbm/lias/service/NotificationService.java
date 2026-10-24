package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.notification.NotificationResponse;
import ma.fsbm.lias.entity.Notification;
import ma.fsbm.lias.entity.Utilisateur;
import ma.fsbm.lias.entity.enums.TypeNotification;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.NotificationRepository;
import ma.fsbm.lias.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public void notify(Long destinataireId, String message, TypeNotification type,
                       Long entityId, String entityType) {
        utilisateurRepository.findById(destinataireId).ifPresent(user -> {
            Notification notif = new Notification();
            notif.setDestinataire(user);
            notif.setMessageText(message);
            notif.setType(type);
            notif.setEntityId(entityId);
            notif.setEntityType(entityType);
            notificationRepository.save(notif);
        });
    }

    public void notifyAll(String message, TypeNotification type, Long entityId, String entityType) {
        utilisateurRepository.findAll().forEach(user -> {
            Notification notif = new Notification();
            notif.setDestinataire(user);
            notif.setMessageText(message);
            notif.setType(type);
            notif.setEntityId(entityId);
            notif.setEntityType(entityType);
            notificationRepository.save(notif);
        });
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findByUser(Long userId) {
        return notificationRepository.findByDestinataireIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public long countUnread(Long userId) {
        return notificationRepository.countByDestinataireIdAndLuFalse(userId);
    }

    public NotificationResponse markAsRead(Long id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", id));
        notif.setLu(true);
        return toResponse(notificationRepository.save(notif));
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.findByDestinataireIdOrderByCreatedAtDesc(userId).stream()
                .filter(n -> !n.isLu())
                .forEach(n -> { n.setLu(true); notificationRepository.save(n); });
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getMessageText(), n.getType(),
                n.isLu(), n.getEntityId(), n.getEntityType(), n.getCreatedAt());
    }
}
