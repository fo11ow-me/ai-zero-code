package com.qiujie.aizerocode.ai;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiujie.aizerocode.ai.tools.*;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;
import com.qiujie.aizerocode.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
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
    private StreamingChatModel openAiStreamingChatModel;

    @Autowired
    private StreamingChatModel reasoningStreamingChatModel;

    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private ToolManager toolManager;

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
        return getAiCodegenService(appId, CodeGenTypeEnum.HTML);
    }

    public AiCodegenService getAiCodegenService(Long appId, CodeGenTypeEnum codeGenType) {
        return serviceCache.get(appId, k -> createAiCodegenService(appId, codeGenType));
    }


    private AiCodegenService createAiCodegenService(Long appId, CodeGenTypeEnum codeGenType) {
        log.info("为appId: {}的应用创建AI服务实例", appId);
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        chatHistoryService.loadChatHistory(appId, chatMemory, 20);
        switch (codeGenType) {
            case VUE_PROJECT -> {
                return AiServices.builder(AiCodegenService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .tools(toolManager.getTools())
                        .hallucinatedToolNameStrategy( // 处理工具调用幻觉问题
                                toolExecutionRequest -> ToolExecutionResultMessage.from(toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name())
                        )
                        .build();
            }
            case HTML, MULTI_FILE -> {
                return AiServices.builder(AiCodegenService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        .chatMemory(chatMemory)
                        .build();
            }
            default ->
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型: " + codeGenType.getValue());
        }
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
