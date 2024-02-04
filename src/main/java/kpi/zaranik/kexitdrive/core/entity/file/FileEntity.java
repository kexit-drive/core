package kpi.zaranik.kexitdrive.core.entity.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
@AllArgsConstructor
public class FileEntity {

    @Id
    private String id;
    private String ownerExternalId;
    private String url;
    private boolean imported;
    private FileMetadata metadata;

    public static class FileMetadata {
        private String name;
        private String mimeType;
    }

}
