package com.dnd.snappy.controller.v1.mission;

import com.dnd.snappy.common.dto.ResponseDto;
import com.dnd.snappy.domain.mission.dto.response.RandomMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.service.RandomMissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/random-mission")
@RequiredArgsConstructor
public class RandomMissionController {

    private final RandomMissionService randomMissionService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<RandomMissionDetailResponseDto>>> findRandomMissions() {
        var data = randomMissionService.findRandomMissions();
        return ResponseDto.ok(data);
    }
}
