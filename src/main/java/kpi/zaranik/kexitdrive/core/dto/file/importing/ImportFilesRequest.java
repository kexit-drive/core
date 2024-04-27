package kpi.zaranik.kexitdrive.core.dto.file.importing;

import java.util.List;

public record ImportFilesRequest(
    List<String> fileIds
) {

}
