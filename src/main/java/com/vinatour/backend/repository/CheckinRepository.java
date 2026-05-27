package com.vinatour.backend.repository;

import com.vinatour.backend.entity.Checkin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CheckinRepository extends JpaRepository<Checkin, Integer> {

    @Query("SELECT COUNT(DISTINCT c.location.id) FROM Checkin c WHERE c.user.id = :userId")
    int countDistinctLocationsByUserId(Integer userId);

    boolean existsByUserIdAndLocationId(Integer userId, Integer locationId);

    List<Checkin> findByUserIdOrderByCheckinDateDesc(Integer userId);
}