package com.vinatour.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vinatour.backend.constant.NotificationType;
import com.vinatour.backend.dto.response.UserResponseDTO;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.entity.UserFollower;
import com.vinatour.backend.mapper.UserMapper;
import com.vinatour.backend.repository.UserFollowerRepository;
import com.vinatour.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFollowerService {
    private final UserFollowerRepository userFollowerRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    public List<UserResponseDTO> getfollowing(Integer userId) {
        return userFollowerRepository.findByFollower_Id(userId).stream()
                .map(userFollower -> userMapper.toUserResponseDTO(userFollower.getFollowed()))
                .toList();
    }

    public List<UserResponseDTO> getfollowers(Integer userId) {
        return userFollowerRepository.findByFollowed_Id(userId).stream()
                .map(userFollower -> userMapper.toUserResponseDTO(userFollower.getFollower()))
                .toList();
    }

    @Transactional
    public String toggleFollow(Integer followerId, Integer followedId) {
        if (followerId.equals(followedId)) {
            throw new RuntimeException("Bạn không thể theo dõi chính mình");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Người theo dõi không tồn tại"));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("Người được theo dõi không tồn tại"));

        if (userFollowerRepository.existsByFollower_IdAndFollowed_Id(followerId, followedId)) {
            userFollowerRepository.deleteByFollower_IdAndFollowed_Id(followerId, followedId);

            notificationService.sendGlobalUpdate("/topic/users/stats", 
                java.util.Map.of("userId", followedId, "action", "REFRESH_FOLLOWERS"));
                
            return "Đã hủy theo dõi thành công";
        } else {
            UserFollower userFollower = new UserFollower(follower, followed, java.time.LocalDateTime.now());
            userFollowerRepository.save(userFollower);
            
            notificationService.createNotification(
                    followedId,
                    followerId,
                    NotificationType.NEW_FOLLOWER,
                    followerId,
                    follower.getUsername());

            notificationService.sendGlobalUpdate("/topic/users/stats", 
                java.util.Map.of("userId", followedId, "action", "REFRESH_FOLLOWERS"));
            
            return "Đã theo dõi thành công";
        }
    }
}
