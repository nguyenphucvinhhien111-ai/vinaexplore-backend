package com.vinatour.backend.controller;

import org.springframework.web.bind.annotation.RestController;
import com.vinatour.backend.Service.ReviewService;
import com.vinatour.backend.dto.request.ReviewRequestDTO;
import com.vinatour.backend.dto.response.ReviewResponseDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@RestController
@RequestMapping("api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("user/{userId}")
    public List<ReviewResponseDTO> getByUser(@PathVariable Integer userId) {
        return reviewService.findReviewByUserId(userId);
    }
    
    @GetMapping("location/{locationId}")
    public List<ReviewResponseDTO> getByLocation(@PathVariable Integer locationId){
        return reviewService.findReviewByLocationId(locationId);
    }
    
    @PostMapping
    public ReviewResponseDTO create(@RequestBody ReviewRequestDTO requestDTO) {
        return reviewService.createReview(requestDTO);
    }

    @PutMapping("/{id}")
    public ReviewResponseDTO update(@PathVariable Integer id, @RequestBody ReviewRequestDTO requestDTO) {
        return reviewService.updateReview(id, requestDTO.getComment(), requestDTO.getRating());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        reviewService.deleteReview(id);
    }
}
