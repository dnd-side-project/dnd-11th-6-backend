package com.dnd.snappy.support;

import static org.springframework.restdocs.snippet.Attributes.key;

import com.dnd.snappy.config.RestDocsConfiguration;
import com.dnd.snappy.infrastructure.uploader.ImageUploader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


@ActiveProfiles("test")
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class})
@Import(RestDocsConfiguration.class)
public abstract class RestDocsSupport extends AbstractContainerBase {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    protected MockMvc mockMvc;

    @MockBean
    protected ImageUploader imageUploader;

    // TODO: 컨트롤러 단위 or 통합 테스트
    @BeforeEach
    void setUp(
            final WebApplicationContext context,
            final RestDocumentationContextProvider provider
    ) {
        this.mockMvc =  MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(MockMvcResultHandlers.print())
                .alwaysDo(restDocs)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        cleanCache();
    }

    private void cleanCache() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }


    protected Attributes.Attribute getDateTimeFormat() {
        return key("format").value("yyyy-MM-dd HH:mm");
    }

}