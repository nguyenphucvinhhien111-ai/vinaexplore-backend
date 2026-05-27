package com.vinatour.backend.controller;

import com.vinatour.backend.Service.LocationEditService;
import com.vinatour.backend.dto.request.LocationEditRequestDTO;
import com.vinatour.backend.dto.response.LocationEditResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/location-edits")
@RequiredArgsConstructor
public class LocationEditController {

    private final LocationEditService locationEditService;

    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/user/{userId}/location/{locationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LocationEditResponseDTO> submitEdit(
            @PathVariable Integer userId,
            @PathVariable Integer locationId,
            @RequestPart("data") LocationEditRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
            
        return ResponseEntity.ok(locationEditService.submitEdit(userId, locationId, requestDTO, imageFile));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<LocationEditResponseDTO>> getPendingEdits() {
        return ResponseEntity.ok(locationEditService.getPendingEdits());
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{editId}/approve")
    public ResponseEntity<String> approveEdit(@PathVariable Integer editId) {
        return ResponseEntity.ok(locationEditService.approveEdit(editId));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{editId}/reject")
    public ResponseEntity<String> rejectEdit(@PathVariable Integer editId) {
        return ResponseEntity.ok(locationEditService.rejectEdit(editId));
    }
}