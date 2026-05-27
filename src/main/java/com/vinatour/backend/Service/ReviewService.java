package com.vinatour.backend.Service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vinatour.backend.constant.NotificationType;
import com.vinatour.backend.dto.request.ReviewRequestDTO;
import com.vinatour.backend.dto.response.ReviewResponseDTO;
import com.vinatour.backend.entity.Review;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.entity.Location;
import com.vinatour.backend.mapper.ReviewMapper;
import com.vinatour.backend.repository.LocationRepository;
import com.vinatour.backend.repository.ReviewRepository;
import com.vinatour.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final NotificationService notificationService;

    public List<ReviewResponseDTO> findReviewByLocationId(Integer locationId) {
        return reviewRepository.findByLocationId(locationId).stream()
                .map(reviewMapper::toResponseDTO).toList();
    }

    public List<ReviewResponseDTO> findReviewByUserId(Integer userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(reviewMapper::toResponseDTO).toList();
    }

    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO requestDTO) {
        if (reviewRepository.existsByUserIdAndLocationId(requestDTO.getUserId(), requestDTO.getLocationId())) {
            throw new RuntimeException("Bạn đã đánh giá địa điểm này rồi!");
        }
        Review review = reviewMapper.toEntity(requestDTO);

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
        review.setUser(user);

        Location location = locationRepository.findById(requestDTO.getLocationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Địa điểm"));
        review.setLocation(location);

        Review savedReview = reviewRepository.save(review);

        if (location.getUser() != null) {
            notificationService.createNotification(
                    location.getUser().getId(),
                    user.getId(),
                    (requestDTO.getComment() != null && !requestDTO.getComment().trim().isEmpty()) 
                        ? NotificationType.NEW_COMMENT 
                        : NotificationType.NEW_REVIEW,
                    location.getId(),
                    user.getFullName() != null ? user.getFullName() : user.getUsername(),
                    location.getName());
        }
        updateLocationRating(location);
        ReviewResponseDTO responseDTO = reviewMapper.toResponseDTO(savedReview);

        notificationService.sendGlobalUpdate("/topic/locations/reviews", responseDTO);

        return responseDTO;
    }

    private void updateLocationRating(Location location) {
        List<Review> allReviews = reviewRepository.findByLocationId(location.getId());

        double average = allReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        location.setRating(average);
        location.setReviewsCount(allReviews.size());
        locationRepository.save(location);

        notificationService.sendGlobalUpdate("/topic/locations/stats", 
            java.util.Map.of(
                "locationId", location.getId(), 
                "type", "REVIEW", 
                "rating", average, 
                "count", allReviews.size()
            ));
    }

    @Transactional
    public ReviewResponseDTO updateReview(Integer id, String comment, Integer rating) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá!"));
        
        review.setComment(comment);
        review.setRating(rating);
        Review savedReview = reviewRepository.save(review);
        
        updateLocationRating(review.getLocation());
        return reviewMapper.toResponseDTO(savedReview);
    }

    @Transactional
    public void deleteReview(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá!"));
        
        Location location = review.getLocation();
        reviewRepository.delete(review);
        updateLocationRating(location);
    }
}