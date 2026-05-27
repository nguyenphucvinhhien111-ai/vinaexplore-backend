package com.vinatour.backend.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinatour.backend.dto.response.LocationResponseDTO;
import com.vinatour.backend.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
        @EntityGraph(attributePaths = { "user", "tags" })
        List<Location> findByStatus(String status);

        @EntityGraph(attributePaths = { "user", "tags" })
        @Query("SELECT l FROM Location l")
        List<Location> findAllOptimized();

        @Query("SELECT DISTINCT l FROM Location l JOIN l.tags t WHERE t.name IN :tagNames and l.status = 'APPROVED'")
        List<Location> findByTagNames(@Param("tagNames") List<String> tagNames);

        @Query(value = "SELECT * FROM locations l WHERE l.status = 'APPROVED' AND " +
                        "(6371 * acos(cos(radians(:userLat)) * cos(radians(l.latitude)) * " +
                        "cos(radians(l.longitude) - radians(:userLng)) + " +
                        "sin(radians(:userLat)) * sin(radians(l.latitude)))) <= :radiusInKm " +
                        "ORDER BY (6371 * acos(cos(radians(:userLat)) * cos(radians(l.latitude)) * " +
                        "cos(radians(l.longitude) - radians(:userLng)) + " +
                        "sin(radians(:userLat)) * sin(radians(l.latitude)))) ASC", nativeQuery = true)
        List<Location> findNearby(
                        @Param("userLat") BigDecimal userLat,
                        @Param("userLng") BigDecimal userLng,
                        @Param("radiusInKm") double radiusInKm);

        List<Location> findByNameContainingIgnoreCase(String name);

        @EntityGraph(attributePaths = { "user", "tags" })
        List<Location> findByUserId(Integer userId);
}
