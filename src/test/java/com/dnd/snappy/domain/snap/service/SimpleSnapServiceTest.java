package com.dnd.snappy.domain.snap.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.dnd.snappy.domain.meeting.entity.Meeting;
import com.dnd.snappy.domain.participant.entity.Participant;
import com.dnd.snappy.domain.snap.dto.response.CreateSnapResponseDto;
import com.dnd.snappy.domain.snap.entity.SimpleSnap;
import com.dnd.snappy.domain.snap.repository.SnapRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class SimpleSnapServiceTest {

    @InjectMocks
    private SimpleSnapService sut;

    @Mock
    private SnapSetupManager snapSetupManager;

    @Mock
    private SnapRepository snapRepository;

    @DisplayName("simple snap을 생성한다.")
    @Test
    void create() {
        //given
        Meeting meeting = Meeting.builder().id(1L).startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusDays(1)).build();
        Participant participant = Participant.builder().id(2L).shootCount(0).build();
        MockMultipartFile image = new MockMultipartFile("test", "test.png", "image/png", new byte[]{});
        SnapSetupDto snapSetupDto = new SnapSetupDto(meeting, participant, image.getOriginalFilename());
        given(snapSetupManager.setup(1L, 2L, image)).willReturn(snapSetupDto);

        //when
        CreateSnapResponseDto result = sut.create(1L, 2L, image, LocalDateTime.now());

        //then
        assertThat(result).isNotNull();
        verify(snapRepository, times(1)).save(any(SimpleSnap.class));
        assertThat(result.snapUrl()).isEqualTo(snapSetupDto.snapUrl());
    }
}