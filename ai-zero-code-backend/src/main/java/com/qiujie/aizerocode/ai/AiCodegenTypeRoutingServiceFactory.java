package com.qiujie.aizerocode.ai;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AiCodegenTypeRoutingServiceFactory {


    @Autowired
    private ChatModel chatModel;


    @Bean
    public AiCodegenTypeRoutingService aiCodegenTypeRoutingService() {
        return AiServices.builder(AiCodegenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }
}
