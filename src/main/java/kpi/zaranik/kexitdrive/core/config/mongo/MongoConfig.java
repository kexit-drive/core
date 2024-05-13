package kpi.zaranik.kexitdrive.core.config.mongo;

import kpi.zaranik.kexitdrive.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
public class MongoConfig {

    private final UserService userService;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return userService::getCurrentUserExternalId;
    }

//    @Bean
//    @Lazy
//    public MappingMongoConverter mongoMappingConverter(MongoTemplate mongoTemplate) {
//        MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();
//        converter.setMapKeyDotReplacement("#");
//        return converter;
//    }
//
//    @Bean
//    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory mongoDatabaseFactory, MappingMongoConverter mongoMappingConverter)  {
//        return new GridFsTemplate(mongoDatabaseFactory, mongoMappingConverter);
//    }

}
