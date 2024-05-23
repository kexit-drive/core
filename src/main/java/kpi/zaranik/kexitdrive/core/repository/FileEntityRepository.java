package kpi.zaranik.kexitdrive.core.repository;

import java.util.stream.Stream;
import kpi.zaranik.kexitdrive.core.entity.file.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileEntityRepository extends MongoRepository<FileEntity, String> {

    Stream<FileEntity> findByOwnerUserExternalIdOrderByCreatedDesc(String ownerUserExternalId);

}
