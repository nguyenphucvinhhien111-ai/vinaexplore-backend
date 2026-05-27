package com.vinatour.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinatour.backend.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @EntityGraph(attributePaths = { "user", "location" })
    List<Review> findByLocationId(Integer id);

    @EntityGraph(attributePaths = { "user", "location" })
    List<Review> findByUserId(Integer id);

    boolean existsByUserIdAndLocationId(Integer userId, Integer locationId);
}
