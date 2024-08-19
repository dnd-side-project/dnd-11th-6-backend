package com.dnd.snappy.domain.mission.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.domain.mission.dto.response.RandomMissionDetailResponseDto;
import com.dnd.snappy.domain.mission.entity.RandomMission;
import com.dnd.snappy.domain.mission.repository.RandomMissionRepository;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RandomMissionServiceTest {

    @InjectMocks
    private RandomMissionService randomMissionService;

    @Mock
    private RandomMissionRepository randomMissionRepository;

    @DisplayName("모든 랜덤 미션을 조회한다.")
    @Test
    void findRandomMissions() {
        //given
        List<RandomMission> randomMissions = new ArrayList<>();
        for(int i=1; i<=3; i++) {
            RandomMission randomMission = RandomMission.builder()
                    .id(i)
                    .content("content" + i)
                    .build();
            randomMissions.add(randomMission);
        }
        given(randomMissionRepository.findAll()).willReturn(randomMissions);

        //when
        List<RandomMissionDetailResponseDto> result = randomMissionService.findRandomMissions();

        //then
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting("randomMissionId", "content")
                .containsExactly(
                        Tuple.tuple(1, "content1"),
                        Tuple.tuple(2, "content2"),
                        Tuple.tuple(3, "content3")
                );

    }
}