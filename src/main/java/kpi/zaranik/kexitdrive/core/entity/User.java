package kpi.zaranik.kexitdrive.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "user")
@AllArgsConstructor
public class User {

    @Id
    private String id;
    private String externalId;
    private String firstName;
    private String lastName;

}
