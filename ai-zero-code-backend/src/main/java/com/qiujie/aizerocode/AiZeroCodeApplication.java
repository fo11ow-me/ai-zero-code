package com.qiujie.aizerocode;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.qiujie.aizerocode.mapper")
@EnableCaching // 开启缓存
public class AiZeroCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiZeroCodeApplication.class, args);
    }

}
