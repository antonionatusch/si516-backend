package com.si516.saludconecta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class GridFsConfig {

    @Value("${app.gridfs.bucket:media_files}")
    private String bucket;

    @Bean
    public GridFsTemplate gridFsTemplate(MongoDatabaseFactory dbFactory,
                                         MongoConverter mongoConverter) {
        return new GridFsTemplate(dbFactory, mongoConverter, bucket);
    }
}
