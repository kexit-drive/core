package kpi.zaranik.kexitdrive.core.dto;

import org.springframework.http.MediaType;

public record DriveFile(
    String id,
    String name,
    String kind,
    MediaType mimeType
) {

}
