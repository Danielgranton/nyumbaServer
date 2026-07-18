package nyumba_server.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // full conversation between two users
    @Query("""
        SELECT m FROM Message m
        WHERE (m.sender.id = :userId AND m.receiver.id = :otherId)
           OR (m.sender.id = :otherId AND m.receiver.id = :userId)
        ORDER BY m.createdAt ASC
    """)
    List<Message> findConversation(Long userId, Long otherId);

    // inbox — messages received by user
    List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    // unread messages
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);

    // sent messages
    List<Message> findBySenderIdOrderByCreatedAtDesc(Long senderId);
}