package com.vinatour.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.vinatour.backend.Service.UserService;
// Khai báo đúng đường dẫn package chứa CloudinaryService của bạn nhé:
import com.vinatour.backend.Service.CloudinaryService;
import com.vinatour.backend.Service.NotificationService;
import com.vinatour.backend.dto.response.UserResponseDTO;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService; 
    private final NotificationService notificationService; 

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getDanhSachUsers() {
        List<User> users = userService.getAllUsers();

        List<UserResponseDTO> responseDTOs = users.stream()
                .map(userMapper::toUserResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(userMapper.toUserResponseDTO(currentUser));
    }

    @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAvatar(@RequestPart("image") MultipartFile imageFile) {
        try {
            User currentUser = userService.getCurrentUser();
            String avatarUrl = cloudinaryService.uploadImage(imageFile);
            currentUser.setAvatarUrl(avatarUrl);
            userService.saveUser(currentUser);
            
            // Broadcast avatar update
            notificationService.sendGlobalUpdate("/topic/users/avatar", 
                Map.of("userId", currentUser.getId(), "avatarUrl", avatarUrl));
                
            return ResponseEntity.ok(userMapper.toUserResponseDTO(currentUser));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi upload ảnh avatar: " + e.getMessage());
        }
    }

    @PatchMapping("/profile/fullname")
    public ResponseEntity<UserResponseDTO> changeFullName(@RequestBody Map<String, String> body) {
        String fullName = body.get("fullName");
        User updatedUser = userService.changeFullName(fullName);
        return ResponseEntity.ok(userMapper.toUserResponseDTO(updatedUser));
    }

    @GetMapping("/exists/username")
    public ResponseEntity<Boolean> checkUsernameExists(
            @RequestParam String username) {

        boolean exists = userService.existsByUsername(username);

        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> checkEmailExists(
            @RequestParam String email) {

        boolean exists = userService.existsByEmail(email);

        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable int id) {
        User user = userService.findById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toUserResponseDTO(user));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<String> toggleUserActiveStatus(@PathVariable int id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Bạn không thể tự khóa tài khoản của chính mình!");
        }

        user.setActive(!user.getActive());
        userService.saveUser(user);

        if (!user.getActive()) {
            notificationService.sendUserUpdate(user.getUsername(), "/queue/status", "FORCE_LOGOUT");
        }
        
        String status = user.getActive() ? "mở khóa" : "vô hiệu hóa";
        return ResponseEntity.ok("Đã " + status + " tài khoản " + user.getFullName());
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<String> changeUserRole(@PathVariable int id, @RequestBody Map<String, String> body) {
        String newRole = body.get("role"); 
        if (newRole == null || (!newRole.equals("ROLE_USER") && !newRole.equals("ROLE_ADMIN"))) {
            return ResponseEntity.badRequest().body("Role không hợp lệ!");
        }

        User user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();

        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Bạn không thể tự đổi quyền của chính mình!");
        }

        user.setRole(newRole);
        userService.saveUser(user);

        notificationService.sendUserUpdate(user.getUsername(), "/queue/status", "FORCE_LOGOUT");

        return ResponseEntity.ok("Đã chuyển quyền tài khoản " + user.getFullName() + " sang " + newRole);
    }

    // 5. API Xóa User (Chỉ Admin)
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        User user = userService.findById(id);
        if (user == null) return ResponseEntity.notFound().build();

        User currentUser = userService.getCurrentUser();
        if (currentUser.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Bạn không thể tự xóa chính mình!");
        }

        notificationService.sendUserUpdate(user.getUsername(), "/queue/status", "FORCE_LOGOUT");
        userService.deleteById(id);

        return ResponseEntity.ok("Đã xóa vĩnh viễn tài khoản " + user.getFullName());
    }
}