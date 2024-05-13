package kpi.zaranik.kexitdrive.core.entity.file;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document("file_entity")
public record FileEntity(
    @Id
    String id,
    @Indexed
    String filename,
    @Indexed
    @CreatedBy
    String ownerUserExternalId,
    @CreatedDate
    LocalDateTime created,
    boolean isDirectory,
    String containingDirectoryId,
    FileMetadata metadata // is absent for directories
) {

    public record FileMetadata(
        String gridFsFileId,
        String contentType,
        boolean isImported
    ) {

    }

    public FileEntity(String filename, boolean isDirectory, String containingDirectoryId, FileMetadata fileMetadata) {
        this(null, filename, null, null, isDirectory, containingDirectoryId, fileMetadata);
    }

    public static FileEntity createUploadedFile(String filename, String gridFsFileId, String dataType, String containingDirectoryId) {
        FileMetadata fileMetadata = new FileMetadata(gridFsFileId, dataType, false);
        return new FileEntity(filename, false, containingDirectoryId, fileMetadata);
    }

    public static FileEntity createImportedFile(String filename, String gridFsFileId, String dataType, String containingDirectoryId) {
        FileMetadata fileMetadata = new FileMetadata(gridFsFileId, dataType, true);
        return new FileEntity(filename, false, containingDirectoryId, fileMetadata);
    }

    public static FileEntity createDirectory(String directoryName, String containingDirectoryId) {
        return new FileEntity(directoryName, true, containingDirectoryId, null);
    }

}
