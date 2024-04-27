//package kpi.zaranik.kexitdrive.core.controller;
//
//import io.minio.DownloadObjectArgs;
//import io.minio.GetPresignedObjectUrlArgs;
//import io.minio.MinioClient;
//import io.minio.http.Method;
//import java.util.concurrent.TimeUnit;
//import kpi.zaranik.kexitdrive.core.config.properties.MinioProperties;
//import kpi.zaranik.kexitdrive.core.service.file.MinioService;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequiredArgsConstructor
//public class TestController {
//
//    @Autowired
//    MinioService service;
//
//    @Autowired
//    MinioClient client;
//
//    @Autowired
//    MinioProperties properties;
//
//    @PostMapping
//    public String uploadToMinio(@RequestBody MultipartFile file) {
//        return service.uploadMultipartFile(file);
//    }
//
//    @GetMapping
//    @SneakyThrows
//    public String getObject(@RequestParam String objectName) {
//        return client.getPresignedObjectUrl(
//                GetPresignedObjectUrlArgs.builder()
//                    .method(Method.GET)
//                    .bucket(properties.bucketName())
//                    .object(objectName)
//                    .expiry(1, TimeUnit.MINUTES)
//                    .build()
//        );
//    }
//
//    @GetMapping("download")
//    @SneakyThrows
//    public void downloadFile(@RequestParam String objectName) {
//        DownloadObjectArgs args = DownloadObjectArgs.builder()
//            .bucket(properties.bucketName())
//            .filename(objectName)
//            .build();
//        client.downloadObject(args);
//    }
//
//}
