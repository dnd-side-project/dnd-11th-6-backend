package com.dnd.snappy.domain.auth.service;

import static org.assertj.core.api.Assertions.*;

import com.dnd.snappy.common.error.CommonErrorCode;
import com.dnd.snappy.common.error.exception.BusinessException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

class PathVariableExtractorTest {
    private PathVariableExtractor sut;

    @BeforeEach
    void setUp() {
        sut = new PathVariableExtractor();
    }

    @DisplayName("유효한 meetingId가 포함된 요청에서 meetingId를 추출한다.")
    @Test
    void extract_Meeting_Id() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", "1");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);

        // When
        Long result = sut.extractMeetingId(request);

        // Then
        assertThat(result).isEqualTo(1L);
    }

    @DisplayName("pathVariables가 null인 요청에서 예외를 던진다.")
    @Test
    void extract_Meeting_Id_null_PathVariables() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, null);

        //when & then
        assertThatThrownBy(() -> sut.extractMeetingId(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CommonErrorCode.BAD_REQUEST.getMessage());
    }

    @DisplayName("pathVariables에 meetingId가 없는 경우 예외를 던진다.")
    @Test
    void extract_Meeting_Id_nonMeetingId_in_pathVariables() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("memberId", "1");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);

        //when & then
        assertThatThrownBy(() -> sut.extractMeetingId(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CommonErrorCode.BAD_REQUEST.getMessage());
    }

    @DisplayName("meetingId가 숫자가 아닌 요청에서 예외를 던진다.")
    @Test
    void extractMeetingId_nonNumericMeetingId() {
        //given
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("meetingId", "notANumber");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);

        //when & then
        assertThatThrownBy(() -> sut.extractMeetingId(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CommonErrorCode.BAD_REQUEST.getMessage());
    }
}