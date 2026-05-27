package com.vinatour.backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinatour.backend.Service.LocationService;
import com.vinatour.backend.Service.SmartSearchService;
import com.vinatour.backend.dto.request.LocationRequestDTO;
import com.vinatour.backend.dto.response.LocationResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    private final SmartSearchService smartSearchService;

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> getById(@PathVariable Integer id) {
        try {
            LocationResponseDTO location = locationService.getLocationById(id);
            return ResponseEntity.ok(location);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public List<LocationResponseDTO> getAll() {
        return locationService.getAllLocations();
    }

    @GetMapping("/status/{status}") // Thêm dấu / trước status cho chuẩn RESTful
    public List<LocationResponseDTO> getByStatus(@PathVariable String status) {
        return locationService.getLocationsByStatus(status);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LocationResponseDTO create(
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LocationRequestDTO requestDTO = objectMapper.readValue(dataJson, LocationRequestDTO.class);
        return locationService.createLocation(requestDTO, imageFile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<LocationResponseDTO> approveLocation(@PathVariable Integer id) {
        try {
            LocationResponseDTO response = locationService.approveLocation(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<LocationResponseDTO> rejectLocation(@PathVariable Integer id) {
        try {
            LocationResponseDTO response = locationService.rejectLocation(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/smart-search")
    public ResponseEntity<?> smartSearch(@RequestParam("prompt") String prompt) {
        try {
            // Nên convert list Entity sang ResponseDTO giống như các hàm khác
            List<LocationResponseDTO> results = locationService.convertToResponseDTOList(
                    smartSearchService.searchLocationsByPrompt(prompt));
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tìm kiếm AI: " + e.getMessage());
        }
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<LocationResponseDTO>> getNearbyLocations(
            @RequestParam("lat") BigDecimal lat,
            @RequestParam("lng") BigDecimal lng,
            @RequestParam(value = "radius", defaultValue = "10.0") double radius) {

        List<LocationResponseDTO> results = locationService.getNearbyLocations(lat, lng, radius);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationResponseDTO>> searchByName(@RequestParam("name") String name) {
        try {
            List<LocationResponseDTO> results = locationService.searchLocationsByName(name);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LocationResponseDTO>> getByUserId(@PathVariable Integer userId) {
        try {
            List<LocationResponseDTO> results = locationService.getLocationsByUserId(userId);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LocationResponseDTO> update(
            @PathVariable Integer id,
            @RequestParam("data") String dataJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        LocationRequestDTO requestDTO = objectMapper.readValue(dataJson, LocationRequestDTO.class);
        return ResponseEntity.ok(locationService.updateLocation(id, requestDTO, imageFile));
    }
}