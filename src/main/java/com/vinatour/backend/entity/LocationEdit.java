package com.vinatour.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_edits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationEdit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "new_name")
    private String newName;

    @Column(name = "new_description", columnDefinition = "TEXT")
    private String newDescription;

    @Column(name = "new_address")
    private String newAddress;

    @Column(name = "new_latitude")
    private BigDecimal newLatitude;

    @Column(name = "new_longitude")
    private BigDecimal newLongitude;

    @Column(name = "new_cover_image")
    private String newCoverImage;

    @Column(name = "status", length = 20)
    private String status = "PENDING"; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}