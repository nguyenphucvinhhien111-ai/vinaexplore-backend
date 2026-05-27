package com.vinatour.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinatour.backend.Service.FavoriteService;
import com.vinatour.backend.dto.response.LocationResponseDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/user/{userId}")
    public List<LocationResponseDTO> getFavorites(@PathVariable Integer userId) {
        return favoriteService.getFavoriteByUser(userId);
    }

    @GetMapping("/location/{locationId}/count")
    public ResponseEntity<Long> getFavoriteCountByLocation(@PathVariable Integer locationId) {
        long count = favoriteService.getFavoriteCountByLocation(locationId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("user/{userId}/location/{locationId}")
    public ResponseEntity<Long> toggleFavorite(
            @PathVariable Integer userId,
            @PathVariable Integer locationId) {

        long count = favoriteService.toggleFavorite(userId, locationId);
        return ResponseEntity.ok(count);
    }

}
