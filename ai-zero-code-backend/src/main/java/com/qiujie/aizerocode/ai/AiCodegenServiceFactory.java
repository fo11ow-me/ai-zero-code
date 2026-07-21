package com.qiujie.aizerocode.ai;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiujie.aizerocode.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AICodegenService工厂类
 */
@Configuration
@Slf4j
public class AiCodegenServiceFactory {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private StreamingChatModel streamingChatModel;

    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Autowired
    private ChatHistoryService chatHistoryService;


    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<Long, AiCodegenService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，appId: {}, 原因: {}", key, cause);
            })
            .build();


    public AiCodegenService getAiCodegenService(Long appId) {
        return serviceCache.get(appId, this::createAiCodegenService);
    }


    private AiCodegenService createAiCodegenService(Long appId) {
        log.info("为appId: {}的应用创建AI服务实例", appId);
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        chatHistoryService.loadChatHistory(appId, chatMemory, 20);
        return AiServices.builder(AiCodegenService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }


    /**
     * 创建AICodegenService实例
     *
     * @return
     */
    @Bean
    public AiCodegenService aiCodegenService() {
        return getAiCodegenService(0L);
    }
}
