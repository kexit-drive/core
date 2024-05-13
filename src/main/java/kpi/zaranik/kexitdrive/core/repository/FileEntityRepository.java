package kpi.zaranik.kexitdrive.core.repository;

import kpi.zaranik.kexitdrive.core.entity.file.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileEntityRepository extends MongoRepository<FileEntity, String> {

}
