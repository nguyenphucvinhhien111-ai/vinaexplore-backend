package com.vinatour.backend.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vinatour.backend.dto.request.LocationRequestDTO;
import com.vinatour.backend.dto.response.LocationResponseDTO;
import com.vinatour.backend.entity.Location;
import com.vinatour.backend.entity.Tag;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.mapper.LocationMapper;
import com.vinatour.backend.repository.LocationRepository;
import com.vinatour.backend.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final TagRepository tagRepository;
    private final LocationMapper locationMapper;
    private final CloudinaryService cloudinaryService;
    private final UserService userService; 
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getAllLocations() {
        return locationRepository.findAllOptimized().stream()
                .map(locationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getLocationsByStatus(String status) {
        return locationRepository.findByStatus(status).stream()
                .map(locationMapper::toResponseDTO).toList();
    }

    public LocationResponseDTO createLocation(LocationRequestDTO requestDTO, MultipartFile imageFile) {
        Location location = locationMapper.toEntity(requestDTO);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                location.setCoverImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh lên Cloudinary: " + e.getMessage());
            }
        }

        User creator = userService.getCurrentUser();
        location.setUser(creator);

        if (requestDTO.getTagIds() != null && !requestDTO.getTagIds().isEmpty()) {
            List<Tag> foundTags = tagRepository.findAllById(requestDTO.getTagIds());
            location.setTags(new HashSet<>(foundTags));
        }

        location.setStatus("PENDING");

        Location savedLocation = locationRepository.save(location);

        notificationService.notifyAdmins(
                creator.getId(),
                com.vinatour.backend.constant.NotificationType.NEW_LOCATION_PENDING,
                savedLocation.getId(),
                creator.getFullName() != null ? creator.getFullName() : creator.getUsername(),
                savedLocation.getName()
        );

        return locationMapper.toResponseDTO(savedLocation);
    }

    public LocationResponseDTO approveLocation(Integer id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm!"));

        location.setStatus("APPROVED");
        Location updatedLocation = locationRepository.save(location);
        LocationResponseDTO responseDTO = locationMapper.toResponseDTO(updatedLocation);

        if (location.getUser() != null) {
            notificationService.createNotification(
                    location.getUser().getId(),
                    null,
                    com.vinatour.backend.constant.NotificationType.LOCATION_APPROVED,
                    location.getId(),
                    "Hệ thống",
                    location.getName());
        }

        notificationService.sendGlobalUpdate("/topic/locations", responseDTO);

        return responseDTO;
    }

    public LocationResponseDTO rejectLocation(Integer id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm!"));

        location.setStatus("REJECTED");
        Location updatedLocation = locationRepository.save(location);

        if (location.getUser() != null) {
            notificationService.createNotification(
                    location.getUser().getId(),
                    null,
                    com.vinatour.backend.constant.NotificationType.LOCATION_REJECTED,
                    location.getId(),
                    "Hệ thống",
                    location.getName());
        }

        return locationMapper.toResponseDTO(updatedLocation);
    }

    public List<LocationResponseDTO> getNearbyLocations(BigDecimal lat, BigDecimal lng, double radius) {
        return locationRepository.findNearby(lat, lng, radius).stream()
                .map(locationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LocationResponseDTO> searchLocationsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of(); 
        }

        return locationRepository.findByNameContainingIgnoreCase(name).stream()
                .map(locationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LocationResponseDTO> convertToResponseDTOList(List<Location> locations) {
        if (locations == null || locations.isEmpty()) {
            return List.of();
        }

        return locations.stream()
                .map(locationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public LocationResponseDTO getLocationById(Integer id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm!"));
        return locationMapper.toResponseDTO(location);
    }

    public List<LocationResponseDTO> getLocationsByUserId(Integer userId) {
        return locationRepository.findByUserId(userId).stream()
                .map(locationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLocation(Integer id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm!"));
        locationRepository.delete(location);
    }

    @Transactional
    public LocationResponseDTO updateLocation(Integer id, LocationRequestDTO requestDTO, MultipartFile imageFile) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm!"));

        location.setName(requestDTO.getName());
        location.setAddress(requestDTO.getAddress());
        location.setDescription(requestDTO.getDescription());
        location.setLatitude(requestDTO.getLatitude());
        location.setLongitude(requestDTO.getLongitude());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                location.setCoverImage(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh lên Cloudinary: " + e.getMessage());
            }
        }

        if (requestDTO.getTagIds() != null) {
            List<Tag> foundTags = tagRepository.findAllById(requestDTO.getTagIds());
            location.setTags(new HashSet<>(foundTags));
        }

        Location savedLocation = locationRepository.save(location);
        LocationResponseDTO responseDTO = locationMapper.toResponseDTO(savedLocation);

        if (location.getUser() != null) {
            notificationService.createNotification(
                    location.getUser().getId(),
                    null,
                    com.vinatour.backend.constant.NotificationType.LOCATION_UPDATED_BY_ADMIN,
                    location.getId(),
                    "Quản trị viên",
                    location.getName());
        }

        notificationService.sendGlobalUpdate("/topic/locations", responseDTO);

        return responseDTO;
    }
}