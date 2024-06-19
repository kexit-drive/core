package kpi.zaranik.kexitdrive.core.repository;

import java.util.List;
import java.util.stream.Stream;
import kpi.zaranik.kexitdrive.core.entity.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileEntityRepository extends MongoRepository<FileEntity, String> {

    List<FileEntity> findByContainingDirectoryIdIs(String directoryId);

    Stream<FileEntity> findByOwnerUserExternalIdAndContainingDirectoryIdIsNullOrderByCreatedDesc(String ownerUserExternalId);

    Stream<FileEntity> findByOwnerUserExternalIdAndContainingDirectoryIdOrderByCreatedDesc(String ownerUserExternalId, String containingDirectoryId);

    boolean existsByOwnerUserExternalId(String ownerUserExternalId);

}
