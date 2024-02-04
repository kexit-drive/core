package kpi.zaranik.kexitdrive.core.service;

import java.nio.charset.StandardCharsets;
import kpi.zaranik.kexitdrive.core.client.GoogleApiClient;
import kpi.zaranik.kexitdrive.core.dto.DriveFile;
import kpi.zaranik.kexitdrive.core.dto.FileListDriveResponse;
import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

@Service
public class GoogleApiService {

    @Autowired
    private GoogleApiClient client;

//    public UserInfo getInfoAboutMe(OAuth2AuthorizedClient authorizedClient) {
//        return client.getUserInfo();
//    }

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

    public String getAccessToken(OAuth2AuthorizedClient authorizedClient) {
        return authorizedClient.getAccessToken().getTokenValue();
    }

}
