package com.covoiturage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.covoiturage.dto.MessageDTO;
import com.covoiturage.dto.CreateMessageDTO;
import com.covoiturage.model.Message;
import com.covoiturage.model.User;
import com.covoiturage.model.Trip;
import com.covoiturage.repository.MessageRepository;
import com.covoiturage.repository.UserRepository;
import com.covoiturage.repository.TripRepository;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    /**
     * Send a new message
     */
    @Transactional
    public MessageDTO sendMessage(Long senderId, CreateMessageDTO createMessageDTO) {
        // Validate sender exists
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        
        // Validate receiver exists
        User receiver = userRepository.findById(createMessageDTO.getReceiverId())
            .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        // Validate trip if provided
        Trip trip = null;
        if (createMessageDTO.getTripId() != null) {
            trip = tripRepository.findById(createMessageDTO.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        }
        
        // Validate content
        if (createMessageDTO.getContent() == null || createMessageDTO.getContent().trim().isEmpty()) {
            throw new RuntimeException("Message content cannot be empty");
        }
        
        // Cannot send message to yourself
        if (senderId.equals(createMessageDTO.getReceiverId())) {
            throw new RuntimeException("You cannot send a message to yourself");
        }
        
        // Create message
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTrip(trip);
        message.setContent(createMessageDTO.getContent().trim());
        message.setIsRead(false);
        
        Message savedMessage = messageRepository.save(message);
        
        return new MessageDTO(savedMessage);
    }
    
    /**
     * Get message by ID
     */
    public MessageDTO getMessageById(Long messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        return new MessageDTO(message);
    }
    
    /**
     * Get conversation between two users
     */
    public List<MessageDTO> getConversationBetweenUsers(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findConversationBetweenUsers(userId1, userId2);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get messages sent by user
     */
    public List<MessageDTO> getSentMessages(Long senderId) {
        List<Message> messages = messageRepository.findBySenderId(senderId);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get messages received by user
     */
    public List<MessageDTO> getReceivedMessages(Long receiverId) {
        List<Message> messages = messageRepository.findByReceiverId(receiverId);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get unread messages for user
     */
    public List<MessageDTO> getUnreadMessages(Long receiverId) {
        List<Message> messages = messageRepository.findByReceiverIdAndIsReadFalse(receiverId);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all messages for user (sent and received)
     */
    public List<MessageDTO> getAllMessagesForUser(Long userId) {
        List<Message> messages = messageRepository.findAllMessagesForUser(userId);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get messages for a specific trip
     */
    public List<MessageDTO> getTripMessages(Long tripId) {
        List<Message> messages = messageRepository.findByTripId(tripId);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get trip conversation between two users
     */
    public List<MessageDTO> getTripConversationBetweenUsers(Long userId1, Long userId2, Long tripId) {
        List<Message> messages = messageRepository.findTripConversationBetweenUsers(userId1, userId2, tripId);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get recent conversations for user
     */
    public List<MessageDTO> getRecentConversations(Long userId) {
        List<Message> messages = messageRepository.findRecentConversations(userId);
        return messages.stream()
            .map(MessageDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Mark message as read
     */
    @Transactional
    public MessageDTO markMessageAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Only receiver can mark message as read
        if (!message.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("You can only mark your own messages as read");
        }
        
        message.setIsRead(true);
        Message savedMessage = messageRepository.save(message);
        
        return new MessageDTO(savedMessage);
    }
    
    /**
     * Mark multiple messages as read
     */
    @Transactional
    public void markMultipleMessagesAsRead(List<Long> messageIds, Long userId) {
        for (Long messageId : messageIds) {
            try {
                markMessageAsRead(messageId, userId);
            } catch (RuntimeException e) {
                // Skip messages that don't belong to the user
                continue;
            }
        }
    }
    
    /**
     * Get count of unread messages for user
     */
    public Long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }
    
    /**
     * Delete a message (only sender can delete their own message)
     */
    @Transactional
    public void deleteMessage(Long messageId, Long senderId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getSender().getId().equals(senderId)) {
            throw new RuntimeException("You can only delete your own messages");
        }
        
        messageRepository.delete(message);
    }
}
