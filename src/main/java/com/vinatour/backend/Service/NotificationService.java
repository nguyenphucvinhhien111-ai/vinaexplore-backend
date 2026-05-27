package com.vinatour.backend.Service;

import com.vinatour.backend.constant.NotificationType;
import com.vinatour.backend.dto.response.NotificationResponseDTO;
import com.vinatour.backend.entity.Notification;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.mapper.NotificationMapper;
import com.vinatour.backend.repository.NotificationRepository;
import com.vinatour.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate; // Thêm thư viện WebSocket
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    private final SimpMessagingTemplate messagingTemplate; 

    @Transactional
    public void createNotification(Integer receiverId, Integer senderId, NotificationType type, Integer referenceId, String... messageArgs) {
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver == null) return;

        User sender = null;
        if (senderId != null) {
            sender = userRepository.findById(senderId).orElse(null);
        }

        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setSender(sender);
        notification.setType(type.name()); 

        notification.setMessage(type.format((Object[]) messageArgs));
        notification.setReferenceId(referenceId);

        Notification savedNotification = notificationRepository.save(notification);

        NotificationResponseDTO responseDTO = notificationMapper.toResponseDTO(savedNotification);

        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(), 
                "/queue/notifications", 
                responseDTO 
        );
    }

    @Transactional
    public void notifyAdmins(Integer senderId, NotificationType type, Integer referenceId, String... messageArgs) {
        List<User> admins = userRepository.findByRole("ROLE_ADMIN");
        for (User admin : admins) {
            createNotification(admin.getId(), senderId, type, referenceId, messageArgs);
        }
    }

    public List<NotificationResponseDTO> getUserNotifications(Integer userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toResponseDTO)
                .toList();
    }

    public long countUnreadNotifications(Integer userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        List<Notification> unreadList = notificationRepository.findByUserIdAndIsReadFalse(userId);
        for (Notification notif : unreadList) {
            notif.setIsRead(true);
        }
        notificationRepository.saveAll(unreadList);
    }

    public void sendGlobalUpdate(String topic, Object data) {
        messagingTemplate.convertAndSend(topic, data);
    }

    public void sendUserUpdate(String username, String destination, Object data) {
        messagingTemplate.convertAndSendToUser(username, destination, data);
    }
}