package nyumba_server.messaging;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.messaging.dto.MessageRequest;
import nyumba_server.messaging.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // send a message (both landlord and tenant)
    @PostMapping
    public ResponseEntity<MessageResponse> send(
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.send(request, user));
    }

    // get conversation with a specific user
    @GetMapping("/conversation/{otherId}")
    public ResponseEntity<List<MessageResponse>> conversation(
            @PathVariable Long otherId,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getConversation(otherId, user));
    }

    // inbox
    @GetMapping("/inbox")
    public ResponseEntity<List<MessageResponse>> inbox(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getInbox(user));
    }

    // sent
    @GetMapping("/sent")
    public ResponseEntity<List<MessageResponse>> sent(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getSent(user));
    }

    // unread messages
    @GetMapping("/unread")
    public ResponseEntity<List<MessageResponse>> unread(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(messageService.getUnread(user));
    }
}