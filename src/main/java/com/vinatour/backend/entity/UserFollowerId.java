package com.vinatour.backend.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserFollowerId implements Serializable {
    private Integer follower;
    private Integer followed;
    
}
