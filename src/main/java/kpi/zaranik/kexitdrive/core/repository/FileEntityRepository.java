package kpi.zaranik.kexitdrive.core.repository;

import java.util.stream.Stream;
import kpi.zaranik.kexitdrive.core.entity.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileEntityRepository extends MongoRepository<FileEntity, String> {

    Stream<FileEntity> findByOwnerUserExternalIdOrderByCreatedDesc(String ownerUserExternalId);

}
