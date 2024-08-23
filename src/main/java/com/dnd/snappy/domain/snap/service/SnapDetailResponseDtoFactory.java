package com.dnd.snappy.domain.snap.service;


import com.dnd.snappy.domain.snap.dto.response.MissionDetailResponseDto;
import com.dnd.snappy.domain.snap.dto.response.ParticipantDetailResponseDto;
import com.dnd.snappy.domain.snap.dto.response.SnapDetailResponseDto;
import com.dnd.snappy.domain.snap.entity.MeetingMissionSnap;
import com.dnd.snappy.domain.snap.entity.RandomMissionSnap;
import com.dnd.snappy.domain.snap.entity.Snap;

public class SnapDetailResponseDtoFactory {

    public static SnapDetailResponseDto createSnapDetailResponseDto(Snap snap) {
        if (snap instanceof RandomMissionSnap) {
            return createRandomMissionSnapDetail((RandomMissionSnap) snap);
        } else if (snap instanceof MeetingMissionSnap) {
            return createMeetingMissionSnapDetail((MeetingMissionSnap) snap);
        } else {
            return createSimpleSnapDetail(snap);
        }
    }

    private static SnapDetailResponseDto createRandomMissionSnapDetail(RandomMissionSnap snap) {
        return new SnapDetailResponseDto(
                snap.getId(),
                snap.getSnapUrl(),
                snap.getShootDate(),
                "RANDOM_MISSION",
                new ParticipantDetailResponseDto(
                        snap.getPhotographerId(),
                        snap.getPhotographerNickname()
                ),
                new MissionDetailResponseDto(
                        snap.getMissionId(),
                        snap.getMissionContent()
                )
        );
    }

    private static SnapDetailResponseDto createMeetingMissionSnapDetail(MeetingMissionSnap snap) {
        return new SnapDetailResponseDto(
                snap.getId(),
                snap.getSnapUrl(),
                snap.getShootDate(),
                "MEETING_MISSION",
                new ParticipantDetailResponseDto(
                        snap.getPhotographerId(),
                        snap.getPhotographerNickname()
                ),
                new MissionDetailResponseDto(
                        snap.getMissionId(),
                        snap.getMissionContent()
                )
        );
    }

    private static SnapDetailResponseDto createSimpleSnapDetail(Snap snap) {
        return new SnapDetailResponseDto(
                snap.getId(),
                snap.getSnapUrl(),
                snap.getShootDate(),
                "SIMPLE",
                new ParticipantDetailResponseDto(
                        snap.getPhotographerId(),
                        snap.getPhotographerNickname()
                ),
                null
        );
    }
}

