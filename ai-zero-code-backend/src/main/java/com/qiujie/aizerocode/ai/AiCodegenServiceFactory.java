package com.qiujie.aizerocode.ai;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AICodegenService工厂类
 */
@Configuration
public class AiCodegenServiceFactory {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private StreamingChatModel streamingChatModel;


    /**
     * 创建AICodegenService实例
     *
     * @return
     */
    @Bean
    public AiCodegenService create() {
        return AiServices.builder(AiCodegenService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
