package com.vinatour.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinatour.backend.entity.Favorite;
import com.vinatour.backend.entity.FavoriteId;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    @EntityGraph(attributePaths = { "location", "location.user", "location.tags" })
    List<Favorite> findByUserId(Integer userId);

    boolean existsByUser_IdAndLocation_Id(Integer userId, Integer locationId);

    void deleteByUser_IdAndLocation_Id(Integer userId, Integer locationId);

    long countByLocation_Id(Integer locationId);
}
