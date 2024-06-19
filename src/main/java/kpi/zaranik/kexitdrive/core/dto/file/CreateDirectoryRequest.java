package kpi.zaranik.kexitdrive.core.dto.file;

import jakarta.validation.constraints.NotBlank;

public record CreateDirectoryRequest(
    @NotBlank
    String name,
    String containingDirectoryId
) {

}
