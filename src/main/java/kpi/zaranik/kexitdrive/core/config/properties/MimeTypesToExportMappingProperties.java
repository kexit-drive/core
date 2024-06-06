package kpi.zaranik.kexitdrive.core.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("mime-types-to-export")
public record MimeTypesToExportMappingProperties(
    @NotNull Map<String, ExportMapping> mapping
) {
    public record ExportMapping(
        @NotBlank String extension,
        @NotBlank String exportMimeType
    ) {

    }
}
