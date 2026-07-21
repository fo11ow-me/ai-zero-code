package com.qiujie.aizerocode;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude =  {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.qiujie.aizerocode.mapper")
public class AiZeroCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiZeroCodeApplication.class, args);
    }

}
