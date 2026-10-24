package ma.fsbm.lias.controller;

import java.util.List;
import ma.fsbm.lias.dto.notification.NotificationResponse;
import ma.fsbm.lias.security.UserPrincipal;
import ma.fsbm.lias.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        return notificationService.findByUser(principal.getId());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.countUnread(principal.getId()));
    }

    @PatchMapping("/{id}/lire")
    public NotificationResponse markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }

    @PatchMapping("/lire-tout")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
