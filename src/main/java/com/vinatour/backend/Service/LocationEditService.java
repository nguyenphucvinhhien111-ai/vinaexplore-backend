package com.vinatour.backend.Service;

import com.vinatour.backend.constant.NotificationType;
import com.vinatour.backend.dto.request.LocationEditRequestDTO;
import com.vinatour.backend.dto.response.LocationEditResponseDTO;
import com.vinatour.backend.entity.Location;
import com.vinatour.backend.entity.LocationEdit;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.mapper.LocationEditMapper;
import com.vinatour.backend.mapper.LocationMapper;
import com.vinatour.backend.repository.LocationEditRepository;
import com.vinatour.backend.repository.LocationRepository;
import com.vinatour.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationEditService {

    private final LocationEditRepository locationEditRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final LocationEditMapper locationEditMapper;
    private final LocationMapper locationMapper; // Thêm mapper để gửi real-time update
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;  

    @Transactional
    public LocationEditResponseDTO submitEdit(Integer userId, Integer locationId, LocationEditRequestDTO requestDTO, MultipartFile imageFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Địa điểm"));

        LocationEdit edit = locationEditMapper.toEntity(requestDTO);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                edit.setNewCoverImage(imageUrl); 
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload ảnh đề xuất lên Cloudinary: " + e.getMessage());
            }
        }

        edit.setUser(user);
        edit.setLocation(location);
        edit.setStatus("PENDING");

        LocationEdit savedEdit = locationEditRepository.save(edit);

        notificationService.notifyAdmins(
                user.getId(),
                com.vinatour.backend.constant.NotificationType.LOCATION_EDIT_PENDING,
                savedEdit.getId(),
                user.getFullName() != null ? user.getFullName() : user.getUsername(),
                location.getName()
        );

        notificationService.sendGlobalUpdate("/topic/admin/edits", locationEditMapper.toResponseDTO(savedEdit));

        return locationEditMapper.toResponseDTO(savedEdit);
    }

    public List<LocationEditResponseDTO> getPendingEdits() {
        return locationEditRepository.findByStatusOrderByCreatedAtDesc("PENDING").stream()
                .map(locationEditMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public String approveEdit(Integer editId) {
        LocationEdit edit = locationEditRepository.findById(editId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản đề xuất"));

        if (!"PENDING".equals(edit.getStatus())) {
            throw new RuntimeException("Đề xuất này đã được xử lý trước đó!");
        }

        Location location = edit.getLocation();

        if (edit.getNewName() != null)
            location.setName(edit.getNewName());
        if (edit.getNewDescription() != null)
            location.setDescription(edit.getNewDescription());
        if (edit.getNewAddress() != null)
            location.setAddress(edit.getNewAddress());
        if (edit.getNewLatitude() != null)
            location.setLatitude(edit.getNewLatitude());
        if (edit.getNewLongitude() != null)
            location.setLongitude(edit.getNewLongitude());
        if (edit.getNewCoverImage() != null)
            location.setCoverImage(edit.getNewCoverImage());

        Location updatedLocation = locationRepository.save(location);

        edit.setStatus("APPROVED");
        locationEditRepository.save(edit);

        notificationService.sendGlobalUpdate("/topic/locations", locationMapper.toResponseDTO(updatedLocation));

        return "Đã duyệt thành công, thông tin địa điểm đã được cập nhật!";
    }

    @Transactional
    public String rejectEdit(Integer editId) {
        LocationEdit edit = locationEditRepository.findById(editId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản đề xuất"));

        if (!"PENDING".equals(edit.getStatus())) {
            throw new RuntimeException("Đề xuất này đã được xử lý trước đó!");
        }

        edit.setStatus("REJECTED");

        locationEditRepository.save(edit);
        notificationService.createNotification(
        edit.getUser().getId(),
        null, 
        NotificationType.LOCATION_REJECTED,
        edit.getLocation().getId(),
        edit.getLocation().getName()
    );
        return "Đã từ chối đề xuất chỉnh sửa.";
    }
}