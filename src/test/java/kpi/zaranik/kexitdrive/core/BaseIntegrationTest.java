package kpi.zaranik.kexitdrive.core;

import com.mongodb.client.MongoClient;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.DockerHealthcheckWaitStrategy;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@AutoConfigureWebTestClient
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest implements AutoCloseable {

    private static final Set<String> SYSTEM_DBS = Set.of("admin", "config", "local");
    protected static final String MONGO_CONTAINER_VERSION = "mongo:6.0.3";
    protected static final String RABBITMQ_CONTAINER_VERSION = "rabbitmq:3.9.7-management";

    static MongoDBContainer mongoContainer;
    static RabbitMQContainer rabbitMQContainer;

    @BeforeAll
    public static void init() {
        mongoContainer = new MongoDBContainer(DockerImageName.parse(MONGO_CONTAINER_VERSION));
        rabbitMQContainer = new RabbitMQContainer(DockerImageName.parse(RABBITMQ_CONTAINER_VERSION));

        mongoContainer.start();
        rabbitMQContainer.start();

        mongoContainer.setWaitStrategy(new DockerHealthcheckWaitStrategy());
        rabbitMQContainer.setWaitStrategy(new DockerHealthcheckWaitStrategy());
    }

    @Autowired
    private MongoClient mongoClient;

    @AfterEach
    public void cleanUp() {
        cleanUpMongo();
    }

    private void cleanUpMongo() {
        List<String> dbNames = StreamSupport.stream(mongoClient.listDatabaseNames().spliterator(), false)
            .filter(n -> !SYSTEM_DBS.contains(n))
            .toList();
        log.info("CLEAN UP DB's after test = {}", dbNames);
        dbNames.forEach(dn -> mongoClient.getDatabase(dn).drop());
    }

    @DynamicPropertySource
    static void mongoDbPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @DynamicPropertySource
    static void rabbitmqPropertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @DynamicPropertySource
    static void springCloudPropertySource(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.enabled", () -> false);
        registry.add("spring.cloud.config.enabled", () -> false);
    }

    @DynamicPropertySource
    static void commonSpringPropertySource(DynamicPropertyRegistry registry) {
        registry.add("logging.level.org.springframework.data.mongodb.core.MongoTemplate", () -> "DEBUG");
        registry.add("log4j.category.org.springframework.data.mongodb", () -> "DEBUG");
    }

    @Override
    public void close() {
        mongoContainer.close();
        rabbitMQContainer.close();
    }

}
