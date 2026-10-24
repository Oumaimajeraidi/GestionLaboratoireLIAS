package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import java.util.List;
import ma.fsbm.lias.dto.message.MessageRequest;
import ma.fsbm.lias.dto.message.MessageResponse;
import ma.fsbm.lias.security.UserPrincipal;
import ma.fsbm.lias.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/recus")
    public List<MessageResponse> received(@AuthenticationPrincipal UserPrincipal principal) {
        return messageService.findReceived(principal.getId());
    }

    @GetMapping("/envoyes")
    public List<MessageResponse> sent(@AuthenticationPrincipal UserPrincipal principal) {
        return messageService.findSent(principal.getId());
    }

    @GetMapping("/non-lus")
    public ResponseEntity<Long> countUnread(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(messageService.countUnread(principal.getId()));
    }

    @PostMapping
    public ResponseEntity<MessageResponse> send(@AuthenticationPrincipal UserPrincipal principal,
                                                @Valid @RequestBody MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.send(principal.getId(), request));
    }

    @PatchMapping("/{id}/lire")
    public MessageResponse markAsRead(@PathVariable Long id) {
        return messageService.markAsRead(id);
    }
}
