package kpi.zaranik.kexitdrive.core.service.file;

import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportingMessage;
import kpi.zaranik.kexitdrive.core.misc.Constants;
import kpi.zaranik.kexitdrive.core.service.GoogleApiService;
import kpi.zaranik.kexitdrive.core.service.auth.AuthorizedClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
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

            log.info("Successfully isImported file with id " + message.fileId() + " for user " + message.userExternalId());
        } catch (Exception e) {
            log.warn("Failed to import file with id " + message.fileId() + " for user " + message.userExternalId(), e);
        }
    }

}
