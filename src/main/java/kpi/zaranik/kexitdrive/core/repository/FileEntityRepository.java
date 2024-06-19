package kpi.zaranik.kexitdrive.core.repository;

import java.util.List;
import java.util.stream.Stream;
import kpi.zaranik.kexitdrive.core.entity.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface FileEntityRepository extends MongoRepository<FileEntity, String> {

    List<FileEntity> findByContainingDirectoryIdIs(String directoryId);

    Stream<FileEntity> findByOwnerUserExternalIdAndContainingDirectoryIdIsNullOrderByCreatedDesc(String ownerUserExternalId);

    Stream<FileEntity> findByOwnerUserExternalIdAndContainingDirectoryIdOrderByCreatedDesc(String ownerUserExternalId, String containingDirectoryId);

    @Query("{ '_id': ?0 }")
    @Update("{$set: {'containingDirectoryId':  ?1}}")
    void setNewDirectoryId(String fileId, String newDirectoryId);

}
