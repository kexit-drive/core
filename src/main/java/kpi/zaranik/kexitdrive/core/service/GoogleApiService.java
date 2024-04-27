package kpi.zaranik.kexitdrive.core.service;

import java.nio.charset.StandardCharsets;
import kpi.zaranik.kexitdrive.core.client.GoogleApiClient;
import kpi.zaranik.kexitdrive.core.dto.DriveFile;
import kpi.zaranik.kexitdrive.core.dto.FileListDriveResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GoogleApiService {

    private final GoogleApiClient client;
    private final WebClient webClient;

    public GoogleApiService(GoogleApiClient client, @Qualifier("fileImportingWebClient") WebClient webClient) {
        this.client = client;
        this.webClient = webClient;
    }

    public FileListDriveResponse getListOfFiles() {
        return client.getListOfFiles();
    }

    public DriveFile getDriveFileInfo(String fileId) {
        return client.getDriveFileInfo(fileId);
    }

    public ResponseEntity<Resource> downloadFile(String fileId) {
        DriveFile fileInfo = getDriveFileInfo(fileId);

        Resource resource = client.downloadFile(fileId);

        return ResponseEntity.ok()
            .contentType(fileInfo.mimeType())
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileInfo.name(), StandardCharsets.UTF_8).build().toString())
            .body(resource);
    }

    public Resource getFileResource(OAuth2AuthorizedClient authorizedClient, String fileId) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        return webClient.get()
            .uri("drive/v3/files/{fileId}?alt=media", fileId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(Resource.class)
            .block();
    }

}
