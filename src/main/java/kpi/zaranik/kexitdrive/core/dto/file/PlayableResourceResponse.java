package kpi.zaranik.kexitdrive.core.dto.file;

import org.springframework.core.io.Resource;

public record PlayableResourceResponse(
    Resource resource,
    String contentType
) {

}
