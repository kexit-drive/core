package kpi.zaranik.kexitdrive.core.mapper;

import kpi.zaranik.kexitdrive.core.dto.file.FileResponse;
import kpi.zaranik.kexitdrive.core.dto.file.FileResponse.FileMetadata;
import kpi.zaranik.kexitdrive.core.dto.file.FileResponse.FileResponseBuilder;
import kpi.zaranik.kexitdrive.core.entity.FileEntity;
import org.springframework.stereotype.Service;

@Service
public class FileEntityMapper {

    public FileResponse mapToResponse(FileEntity entity) {
        FileResponseBuilder builder = FileResponse.builder();

        builder
            .id(entity.id())
            .ownerUserExternalId(entity.ownerUserExternalId())
            .filename(entity.filename())
            .created(entity.created())
            .isDirectory(entity.isDirectory())
            .containingDirectoryId(entity.containingDirectoryId());

        if (!entity.isDirectory()) {
            builder.metadata(new FileMetadata(
                entity.metadata().gridFsFileId(),
                entity.metadata().contentType(),
                entity.metadata().isImported()
            ));
        }

        return builder.build();
    }

}
