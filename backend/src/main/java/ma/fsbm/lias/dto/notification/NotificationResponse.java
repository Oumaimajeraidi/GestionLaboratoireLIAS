package ma.fsbm.lias.dto.notification;

import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.TypeNotification;

public record NotificationResponse(
    Long id,
    String messageText,
    TypeNotification type,
    boolean lu,
    Long entityId,
    String entityType,
    LocalDateTime createdAt
) {}
