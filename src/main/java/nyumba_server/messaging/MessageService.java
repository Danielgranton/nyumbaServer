package nyumba_server.messaging;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.auth.UserRepository;
import nyumba_server.messaging.dto.MessageRequest;
import nyumba_server.messaging.dto.MessageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageResponse send(MessageRequest request, User sender) {
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .body(request.getBody())
                .build();

        messageRepository.save(message);
        return toResponse(message);
    }

    // get full conversation between current user and another user
    public List<MessageResponse> getConversation(Long otherId, User user) {
        // mark all received messages in this conversation as read
        List<Message> conversation = messageRepository.findConversation(user.getId(), otherId);
        conversation.stream()
                .filter(m -> m.getReceiver().getId().equals(user.getId()) && !m.isRead())
                .forEach(m -> {
                    m.setRead(true);
                    messageRepository.save(m);
                });
        return conversation.stream().map(this::toResponse).toList();
    }

    // inbox
    public List<MessageResponse> getInbox(User user) {
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).toList();
    }

    // sent
    public List<MessageResponse> getSent(User user) {
        return messageRepository.findBySenderIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).toList();
    }

    // unread count + messages
    public List<MessageResponse> getUnread(User user) {
        return messageRepository.findByReceiverIdAndIsReadFalse(user.getId())
                .stream().map(this::toResponse).toList();
    }

    private MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getFullName())
                .body(message.getBody())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}