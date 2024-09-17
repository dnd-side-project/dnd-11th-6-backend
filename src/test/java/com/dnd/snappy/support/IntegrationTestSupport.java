package com.dnd.snappy.support;

import com.dnd.snappy.infrastructure.image.ImageUploader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class IntegrationTestSupport extends AbstractContainerBase {

    @MockBean
    protected ImageUploader imageUploader;
}
