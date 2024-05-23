package kpi.zaranik.kexitdrive.core.service.file;

import java.util.List;
import kpi.zaranik.kexitdrive.core.dto.DriveFile;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportingMessage;
import kpi.zaranik.kexitdrive.core.entity.file.FileEntity;
import kpi.zaranik.kexitdrive.core.misc.Constants;
import kpi.zaranik.kexitdrive.core.repository.FileEntityRepository;
import kpi.zaranik.kexitdrive.core.service.GoogleApiService;
import kpi.zaranik.kexitdrive.core.service.auth.AuthorizedClientFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
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

    private final List<String> mimeTypesToExport = List.of(
        "application/vnd.google-apps.document",
        "application/vnd.google-apps.spreadsheet",
        "application/vnd.google-apps.presentation",
        "application/vnd.google-apps.drawing",
        "application/vnd.google-apps.script"
    );

    @RabbitListener(queues = Constants.IMPORT_FILES_QUEUE)
    public void importFilesRabbitListener(ImportingMessage message) {
        try {
            OAuth2AuthorizedClient authorizedClient = authorizedClientFactory.getClientByExternalUserId(message.userExternalId());

            DriveFile driveFile = googleApiService.getDriveFileInfo(authorizedClient, message.fileId());

            Resource resource;
            String fileName;
            if (mimeTypesToExport.contains(driveFile.mimeType())) {
                ExportDocumentMetadata metadata = getExportDocumentMetadata(driveFile);
                resource = googleApiService.exportFileResource(authorizedClient, driveFile.id(), metadata.exportMimeType());
                fileName = metadata.fileName();
            } else {
                resource = googleApiService.getFileResource(authorizedClient, driveFile.id());
                fileName = driveFile.name();
            }
            String dataType = tikaService.getDataType(resource.getInputStream());
            String gridFsFileId = gridFsTemplate.store(resource.getInputStream(), fileName, dataType).toString();
            FileEntity importedFile = FileEntity.createImportedFile(fileName, gridFsFileId, dataType, null, message.userExternalId());

            fileEntityRepository.save(importedFile);

            log.info("Successfully isImported file with external id {} for user {}", message.fileId(), message.userExternalId());
        } catch (Exception e) {
            log.warn("Failed to import file with external id {} for user {}", message.fileId(), message.userExternalId(), e);
        }
    }

    record ExportDocumentMetadata(String fileName, String exportMimeType) {
    }

    private ExportDocumentMetadata getExportDocumentMetadata(DriveFile driveFile) {
        return switch (driveFile.mimeType()) {
            case "application/vnd.google-apps.document" -> new ExportDocumentMetadata(driveFile.name() + ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "application/vnd.google-apps.spreadsheet" -> new ExportDocumentMetadata(driveFile.name() + ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "application/vnd.google-apps.presentation" -> new ExportDocumentMetadata(driveFile.name() + ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            case "application/vnd.google-apps.drawing" -> new ExportDocumentMetadata(driveFile.name() + ".png", "image/png");
            case "application/vnd.google-apps.script" -> new ExportDocumentMetadata(driveFile.name() + ".json", "application/vnd.google-apps.script+json");
            default -> throw new IllegalStateException("Unexpected export google drive file mime type for file " + driveFile.id() + ": " + driveFile.mimeType());
        };
    }

}
