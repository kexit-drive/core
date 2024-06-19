package kpi.zaranik.kexitdrive.core.dto.file.move;

import jakarta.validation.constraints.NotBlank;

public record MoveRequest(
    @NotBlank
    String fileId,
    @NotBlank
    String destinationDirectoryId
) {

}
