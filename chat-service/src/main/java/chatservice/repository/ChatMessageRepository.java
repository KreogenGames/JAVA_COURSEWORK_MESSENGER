package chatservice.repository;

import chatservice.model.ChatMessage;
import chatservice.model.MessageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    long countBySenderIdAndRecipientIdAndMessageStatus(
            String senderId, String recipientId, MessageStatus messageStatus);

    List<ChatMessage> findByChatId(String chatId);
}
