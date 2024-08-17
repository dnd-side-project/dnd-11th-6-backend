package com.dnd.snappy.domain.mission.service;

import com.dnd.snappy.domain.mission.dto.response.RandomMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.repository.RandomMissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RandomMissionService {

    private final RandomMissionRepository randomMissionRepository;

    public List<RandomMissionDetailResponseDto> findRandomMissions() {
        return randomMissionRepository.findAll().stream()
                .map(randomMission -> new RandomMissionDetailResponseDto(
                        randomMission.getId(),
                        randomMission.getContent())
                )
                .toList();
    }

}
