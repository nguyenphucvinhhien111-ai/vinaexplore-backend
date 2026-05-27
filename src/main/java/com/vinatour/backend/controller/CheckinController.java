package com.vinatour.backend.controller;

import com.vinatour.backend.dto.request.CheckinRequestDTO;
import com.vinatour.backend.dto.response.CheckinResponseDTO;
import com.vinatour.backend.entity.Checkin;
import com.vinatour.backend.mapper.CheckinMapper;
import com.vinatour.backend.Service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;
    private final CheckinMapper checkinMapper;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CheckinRequestDTO request) {
        try {
            Checkin saved = checkinService.performCheckin(request);
            return ResponseEntity.ok(checkinMapper.toResponseDTO(saved));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<CheckinResponseDTO>> getHistory() {
        List<CheckinResponseDTO> history = checkinService.getMyHistory().stream()
                .map(checkinMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }
}