package com.vinatour.backend.Service;

import com.vinatour.backend.dto.request.CheckinRequestDTO;
import com.vinatour.backend.entity.Checkin;
import com.vinatour.backend.entity.Location;
import com.vinatour.backend.entity.User;
import com.vinatour.backend.repository.CheckinRepository;
import com.vinatour.backend.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final LocationRepository locationRepository;
    private final UserService userService; 
    private final NotificationService notificationService;

    private static final int EARTH_RADIUS_METERS = 6371000;
    private static final double ALLOWED_DISTANCE = 500.0;

    @Transactional
    public Checkin performCheckin(CheckinRequestDTO request) {
        User currentUser = userService.getCurrentUser();
        
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm!"));

        if (checkinRepository.existsByUserIdAndLocationId(currentUser.getId(), location.getId())) {
            throw new RuntimeException("Bạn đã check-in tại đây rồi!");
        }

        double distance = calculateDistance(
                request.getActualLatitude(), request.getActualLongitude(),
                location.getLatitude().doubleValue(),
                location.getLongitude().doubleValue()
        );

        if (distance > ALLOWED_DISTANCE) {
            throw new RuntimeException("Bạn đang cách xa địa điểm " + Math.round(distance) + "m. Vui lòng đến gần hơn!");
        }

        Checkin checkin = new Checkin();
        checkin.setUser(currentUser);
        checkin.setLocation(location);
        checkin.setActualLatitude(request.getActualLatitude());
        checkin.setActualLongitude(request.getActualLongitude());
        Checkin saved = checkinRepository.save(checkin);

        location.setCheckinCount((location.getCheckinCount() == null ? 0 : location.getCheckinCount()) + 1);
        locationRepository.save(location);

        notificationService.sendGlobalUpdate("/topic/locations/stats", 
            java.util.Map.of("locationId", location.getId(), "type", "CHECKIN", "count", location.getCheckinCount()));

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Checkin> getMyHistory() {
        User currentUser = userService.getCurrentUser();
        return checkinRepository.findByUserIdOrderByCheckinDateDesc(currentUser.getId());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}