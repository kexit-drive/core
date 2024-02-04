package kpi.zaranik.kexitdrive.core.repository;

import kpi.zaranik.kexitdrive.core.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
