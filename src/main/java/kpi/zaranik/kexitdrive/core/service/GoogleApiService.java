package kpi.zaranik.kexitdrive.core.service;

import kpi.zaranik.kexitdrive.core.dto.DriveFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GoogleApiService {

    @Qualifier("fileImportingWebClient")
    private final WebClient webClient;

    public DriveFile getDriveFileInfo(OAuth2AuthorizedClient authorizedClient, String fileId) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        return webClient.get()
            .uri("drive/v3/files/{fileId}", fileId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(DriveFile.class)
            .block();
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

    public Resource exportFileResource(OAuth2AuthorizedClient authorizedClient, String fileId, String exportMimeType) {
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("drive/v3/files/{fileId}/export")
                .queryParam("mimeType", exportMimeType)
                .build(fileId))
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(Resource.class)
            .block();
    }

}
