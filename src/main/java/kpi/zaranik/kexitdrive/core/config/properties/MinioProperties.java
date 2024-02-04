package kpi.zaranik.kexitdrive.core.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
    @NotBlank String url,
    @NotBlank String accessKey,
    @NotBlank String secretKey,
    @NotBlank String bucketName
) {

}
