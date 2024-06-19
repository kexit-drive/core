package kpi.zaranik.kexitdrive.core.service.file;

import kpi.zaranik.kexitdrive.core.config.properties.MimeTypesToExportMappingProperties;
import kpi.zaranik.kexitdrive.core.config.properties.MimeTypesToExportMappingProperties.ExportMapping;
import kpi.zaranik.kexitdrive.core.dto.DriveFile;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportingMessage;
import kpi.zaranik.kexitdrive.core.entity.FileEntity;
import kpi.zaranik.kexitdrive.core.misc.Constants;
import kpi.zaranik.kexitdrive.core.repository.FileEntityRepository;
import kpi.zaranik.kexitdrive.core.service.GoogleApiService;
import kpi.zaranik.kexitdrive.core.service.auth.AuthorizedClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportingService {

    private final AuthorizedClientFactory authorizedClientFactory;
    private final GoogleApiService googleApiService;
    private final TikaService tikaService;
    private final FileEntityRepository fileEntityRepository;
    private final GridFsTemplate gridFsTemplate;
    private final MimeTypesToExportMappingProperties mimeTypesToExportMapping;

    @Async
    @RabbitListener(queues = Constants.IMPORT_FILES_QUEUE)
    public void importFilesRabbitListener(ImportingMessage message) {
        try {
            OAuth2AuthorizedClient authorizedClient = authorizedClientFactory.getClientByExternalUserId(message.userExternalId());
            DriveFile driveFile = googleApiService.getDriveFileInfo(authorizedClient, message.fileId());

            Resource resource;
            String fileName;
            if (mimeTypesToExportMapping.mapping().containsKey(driveFile.mimeType())) {
                ExportMapping exportMapping = mimeTypesToExportMapping.mapping().get(driveFile.mimeType());
                resource = googleApiService.exportFileResource(authorizedClient, driveFile.id(), exportMapping.exportMimeType());
                fileName = driveFile.name() + "." + exportMapping.extension();
            } else {
                resource = googleApiService.getFileResource(authorizedClient, driveFile.id());
                fileName = driveFile.name();
            }
            String dataType = tikaService.getDataType(resource.getInputStream());
            String gridFsFileId = gridFsTemplate.store(resource.getInputStream(), fileName, dataType).toString();
            FileEntity importedFile = FileEntity.createImportedFile(fileName, gridFsFileId, dataType, message.directoryId(), message.userExternalId());

            fileEntityRepository.save(importedFile);

            log.info("Successfully isImported file with external id {} for user {}", message.fileId(), message.userExternalId());
        } catch (Exception e) {
            log.warn("Failed to import file with external id {} for user {}", message.fileId(), message.userExternalId(), e);
        }
    }

}
