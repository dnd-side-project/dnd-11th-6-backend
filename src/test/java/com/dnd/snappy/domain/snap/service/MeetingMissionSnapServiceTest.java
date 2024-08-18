package com.dnd.snappy.domain.snap.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.dnd.snappy.common.error.exception.NotFoundException;
import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.mission.entity.Mission;
import com.dnd.snappy.domain.mission.exception.MissionErrorCode;
import com.dnd.snappy.domain.mission.repository.MissionRepository;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.entity.MeetingMissionSnap;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class MeetingMissionSnapServiceTest {

    @InjectMocks
    private MeetingMissionSnapService sut;

    @Mock
    private SnapSetupManager snapSetupManager;

    @Mock
    private SnapRepository snapRepository;

    @Mock
    private MissionRepository missionRepository;

    @DisplayName("random mission snap을 생성한다.")
    @Test
    void create() {
        //given
        Meeting meeting = Meeting.builder().id(1L).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).build();
        Participant participant = Participant.builder().id(2L).shootCount(0).build();
        MockMultipartFile image = new MockMultipartFile("test", "test.png", "image/png", new byte[]{});
        SnapSetupDto snapSetupDto = new SnapSetupDto(meeting, participant, image.getOriginalFilename());
        given(snapSetupManager.setup(1L, 2L, image)).willReturn(snapSetupDto);
        Mission mission = Mission.builder().id(3L).content("test").build();
        given(missionRepository.findByIdAndMeetingId(3L, 1L)).willReturn(Optional.of(mission));

        //when
        CreateSnapResponseDto result = sut.create(1L, 2L, 3L, image, LocalDateTime.now());

        //then
        assertThat(result).isNotNull();
        verify(snapRepository, times(1)).save(any(MeetingMissionSnap.class));
        assertThat(result.snapUrl()).isEqualTo(snapSetupDto.snapUrl());
    }

    @DisplayName("random mission에 해당하는 id값이 없다면 예외가 발생한다.")
    @Test
    void create_not_found_random_mission() {
        //given
        Meeting meeting = Meeting.builder().id(1L).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).build();
        Participant participant = Participant.builder().id(2L).shootCount(0).build();
        MockMultipartFile image = new MockMultipartFile("test", "test.png", "image/png", new byte[]{});
        SnapSetupDto snapSetupDto = new SnapSetupDto(meeting, participant, image.getOriginalFilename());
        given(snapSetupManager.setup(1L, 2L, image)).willReturn(snapSetupDto);
        given(missionRepository.findByIdAndMeetingId(3L, 1L)).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> sut.create(1L, 2L, 3L, image, LocalDateTime.now()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(MissionErrorCode.NOT_FOUND_MEETING_MISSION.getMessage());
    }
}