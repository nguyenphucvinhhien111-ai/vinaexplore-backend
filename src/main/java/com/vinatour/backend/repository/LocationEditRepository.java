package com.vinatour.backend.repository;

import com.vinatour.backend.entity.LocationEdit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationEditRepository extends JpaRepository<LocationEdit, Integer> {
    
    @EntityGraph(attributePaths = {"user", "location"})
    List<LocationEdit> findByStatusOrderByCreatedAtDesc(String status);
}