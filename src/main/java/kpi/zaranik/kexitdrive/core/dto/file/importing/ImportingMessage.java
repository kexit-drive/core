package kpi.zaranik.kexitdrive.core.dto.file.importing;

public record ImportingMessage(
    String userExternalId,
    String fileId,
    String directoryId
) {

}
