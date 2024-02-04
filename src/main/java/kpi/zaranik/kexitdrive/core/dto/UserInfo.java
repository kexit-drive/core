package kpi.zaranik.kexitdrive.core.dto;

import lombok.Builder;

@Builder
public record UserInfo(
    String externalId,
    String name,
    String firstName,
    String lastName,
    String pictureLink
) {

}
