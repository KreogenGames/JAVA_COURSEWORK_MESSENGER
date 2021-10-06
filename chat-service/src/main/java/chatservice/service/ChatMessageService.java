package chatservice.service;

import chatservice.exception.ResourceNotFoundException;
import chatservice.model.ChatMessage;
import chatservice.model.MessageStatus;
import chatservice.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private MongoOperations mongoOperations;

    public ChatMessage save(ChatMessage chatMessage){
        chatMessage.setMessageStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    public long countNewMessages(String senderId, String recipientId){
        return chatMessageRepository.countBySenderIdAndRecipientIdAndMessageStatus(
                senderId, recipientId, MessageStatus.RECEIVED);
    }

    public void updateStatuses(String senderId, String recipientId, MessageStatus messageStatus){
        Query query = new Query(
                Criteria
                        .where("senderId").is(senderId)
                        .and("recipientId").is(recipientId));

        Update update = Update.update("messageStatus", messageStatus);
        mongoOperations.updateMulti(query, update, ChatMessage.class);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId){
        var chatId = chatRoomService.getChatId(senderId, recipientId, false);
        var messages = chatId.map(cId -> chatMessageRepository
                .findByChatId(cId)).orElse(new ArrayList<>());
        if(messages.size()>0){
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }
        return messages;
    }

    public ChatMessage findById(String id){
        return chatMessageRepository.findById(id)
                .map(chatMessage -> {
                    chatMessage.setMessageStatus(MessageStatus.DELIVERED);
                    return chatMessageRepository.save(chatMessage);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("Can't find message (" + id + ")"));
    }
}
