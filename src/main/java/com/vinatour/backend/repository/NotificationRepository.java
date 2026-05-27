package com.vinatour.backend.repository;

import com.vinatour.backend.entity.Notification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @EntityGraph(attributePaths = { "sender" })
    List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

    long countByUserIdAndIsReadFalse(Integer userId);

    List<Notification> findByUserIdAndIsReadFalse(Integer userId);
}   