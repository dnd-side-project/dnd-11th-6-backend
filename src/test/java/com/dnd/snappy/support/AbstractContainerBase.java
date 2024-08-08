package com.dnd.snappy.support;

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.DockerImageName;

public abstract class AbstractContainerBase {

    private final static int REDIS_PORT = 6379;
    private static final DockerImageName REDIS_DOCKER_IMAGE = DockerImageName
            .parse("redis:6.0.2")
            .asCompatibleSubstituteFor("redis:6.0.2");

    static final RedisContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new RedisContainer(REDIS_DOCKER_IMAGE)
                .withExposedPorts(REDIS_PORT);

        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        if (REDIS_CONTAINER.isRunning()) {
            registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
            registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT)
                    .toString());
        }
    }
}
