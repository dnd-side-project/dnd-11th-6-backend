package com.dnd.snappy.domain.meeting.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MeetingTest {

    @DisplayName("모임의 비밀번호가 맞는지 확인 가능하다.")
    @ParameterizedTest
    @CsvSource({
            "password, password, true",
            "password, wrongPassword, false"
    })
    void isCorrectPassword(String password, String inputPassword, boolean expected) {
        //given
        Meeting meeting = Meeting.builder().password(password).build();

        //when
        boolean result = meeting.isCorrectPassword(inputPassword);

        //then
        assertThat(result).isEqualTo(expected);
    }
}