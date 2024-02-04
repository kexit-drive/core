package kpi.zaranik.kexitdrive.core.service;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.InputStream;
import java.util.UUID;
import kpi.zaranik.kexitdrive.core.config.properties.MinioProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String uploadMultipartFile(MultipartFile file) {
        String fileName = generateFileName(file);
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioProperties.bucketName())
                    .object(fileName).stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return minioProperties.url() + "/" + minioProperties.bucketName() + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store image file.", e);
        }
    }

    private String generateFileName(@NotNull MultipartFile file) {
        return UUID.randomUUID() + "-" + file.getOriginalFilename().replace(" ", "_");
    }

    @SneakyThrows
    public String shareLink(String objectName) {
        GetObjectArgs args = GetObjectArgs.builder()
            .object(objectName)
            .bucket(minioProperties.bucketName())
            .build();
        GetObjectResponse response = minioClient.getObject(args);
        return response.object();
    }

}
