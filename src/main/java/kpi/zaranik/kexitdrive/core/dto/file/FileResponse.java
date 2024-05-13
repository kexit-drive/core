package kpi.zaranik.kexitdrive.core.dto.file;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FileResponse(
    String id,
    String filename,
    String ownerUserExternalId,
    LocalDateTime created,
    boolean isDirectory,
    String containingDirectoryId,
    FileMetadata metadata
) {

    public record FileMetadata(
        String gridFsFileId,
        String contentType,
        boolean isImported
    ) {

    }

}
