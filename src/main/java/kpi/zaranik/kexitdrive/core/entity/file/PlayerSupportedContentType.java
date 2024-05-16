package kpi.zaranik.kexitdrive.core.entity.file;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum PlayerSupportedContentType {

    APPLICATION_PDF(
        Set.of(
            "application/pdf"
        )
    ),
    AUDIO(
        Set.of(
            "audio/mp3",
            "audio/mpeg",
            "audio/wav"
        )
    ),
    IMAGE(
        Set.of(
            "image/apng",
            "image/avif",
            "image/gif",
            "image/jpeg",
            "image/bmp",
            "image/png",
            "image/svg+xml",
            "image/webp"
        )
    ),
    VIDEO(
        Set.of(
            "video/mp4",
            "video/webm",
            "video/quicktime",
            "video/ogg"
        )
    ),
    UNSUPPORTED(Collections.emptySet());

    private final Set<String> mimeType;

    public static Optional<PlayerSupportedContentType> getForMimeType(String mimeType) {
        return Arrays.stream(PlayerSupportedContentType.values())
            .filter(supportedContentType -> supportedContentType.getMimeType().contains(mimeType))
            .findFirst();
    }

}
