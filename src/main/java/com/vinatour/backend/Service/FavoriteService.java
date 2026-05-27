package com.vinatour.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vinatour.backend.dto.response.LocationResponseDTO;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.entity.Favorite;
import com.vinatour.backend.entity.Location;
import com.vinatour.backend.mapper.LocationMapper;
import com.vinatour.backend.repository.FavoriteRepository;
import com.vinatour.backend.repository.LocationRepository;
import com.vinatour.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final NotificationService notificationService;

    public List<LocationResponseDTO> getFavoriteByUser(Integer userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favorite -> locationMapper.toResponseDTO(favorite.getLocation())).toList();
    }

    public long getFavoriteCountByLocation(Integer locationId) {
        return favoriteRepository.countByLocation_Id(locationId);
    }

    @Transactional
    public long toggleFavorite(Integer userId, Integer locationId) {
        if (favoriteRepository.existsByUser_IdAndLocation_Id(userId, locationId)) {
            favoriteRepository.deleteByUser_IdAndLocation_Id(userId, locationId);
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Location"));

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setLocation(location);
            favoriteRepository.save(favorite);

            if (location.getUser() != null && !location.getUser().getId().equals(user.getId())) {
                notificationService.createNotification(
                        location.getUser().getId(),
                        user.getId(),
                        com.vinatour.backend.constant.NotificationType.NEW_FAVORITE,
                        location.getId(),
                        user.getFullName() != null ? user.getFullName() : user.getUsername(),
                        location.getName());
            }
        }

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Location"));
        long count = favoriteRepository.countByLocation_Id(locationId);
        location.setFavoriteCount((int) count);
        locationRepository.save(location);

        notificationService.sendGlobalUpdate("/topic/locations/stats", 
            java.util.Map.of("locationId", locationId, "type", "FAVORITE", "count", count));

        return count;
    }
}
