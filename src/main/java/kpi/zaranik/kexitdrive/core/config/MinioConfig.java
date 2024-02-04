package kpi.zaranik.kexitdrive.core.config;

import io.minio.MinioClient;
import kpi.zaranik.kexitdrive.core.config.properties.MinioProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Autowired
    private MinioProperties minio;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minio.url())
            .credentials(minio.accessKey(), minio.secretKey())
            .build();
    }
}
