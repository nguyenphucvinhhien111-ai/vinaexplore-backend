package com.vinatour.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name="checkins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "actual_latitude", nullable = false)
    private Double actualLatitude;

    @Column(name = "actual_longitude", nullable = false)
    private Double actualLongitude;

    @CreationTimestamp
    @Column(name = "checkin_date", updatable = false)
    private LocalDateTime checkinDate;
}