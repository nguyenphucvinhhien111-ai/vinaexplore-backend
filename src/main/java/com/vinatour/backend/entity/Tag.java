package com.vinatour.backend.entity;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="name", unique = true, nullable = false, length = 50)
    private String name;
    
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<Location> locations = new HashSet<>();
}
