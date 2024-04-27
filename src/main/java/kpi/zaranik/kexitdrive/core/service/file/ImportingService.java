package kpi.zaranik.kexitdrive.core.service.file;

import java.util.concurrent.TimeUnit;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportingMessage;
import kpi.zaranik.kexitdrive.core.misc.Constants;
import kpi.zaranik.kexitdrive.core.service.GoogleApiService;
import kpi.zaranik.kexitdrive.core.service.auth.AuthorizedClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportingService {

    private final AuthorizedClientFactory authorizedClientFactory;
    private final GoogleApiService googleApiService;

    @RabbitListener(queues = Constants.IMPORT_FILES_QUEUE)
    public void importFilesRabbitListener(ImportingMessage message) {
        try {
            OAuth2AuthorizedClient authorizedClient = authorizedClientFactory.getClientByExternalUserId(message.userExternalId());
            Resource resource = googleApiService.getFileResource(authorizedClient, message.fileId());

            // save resource somehow

            log.info("Successfully imported file with id " + message.fileId() + " for user " + message.userExternalId());
        } catch (Exception e) {
            log.warn("Failed to import file with id " + message.fileId() + " for user " + message.userExternalId(), e);
        }
    }

//    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 15, initialDelay = 1)
//    public void scheduler() {
//        String userExternalId = "107010593884037449481";
//        String fileId = "1JS3dDecLG9_nTnjBdwYVBF15kzm2LkS6";
//        try {
//            OAuth2AuthorizedClient authorizedClient = authorizedClientFactory.getClientByExternalUserId(userExternalId);
//            log.info("accessToken = " + authorizedClient.getAccessToken().getTokenValue());
//            log.info("refreshToken = " + authorizedClient.getRefreshToken().getTokenValue());
//            Resource resource = googleApiService.getFileResource(authorizedClient, fileId);
//            log.info("Successfully imported file size: " + resource.getContentAsByteArray().length);
//        } catch (Exception e) {
//            log.error("Some error occurred: ", e);
//        }
//    }

}
