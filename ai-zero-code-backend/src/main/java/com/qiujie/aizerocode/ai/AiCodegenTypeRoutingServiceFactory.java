package com.qiujie.aizerocode.ai;


import com.qiujie.aizerocode.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AiCodegenTypeRoutingServiceFactory {


    public AiCodegenTypeRoutingService createAiCodegenTypeRoutingService() {
        ChatModel routingChatModelPrototype = SpringContextUtil.getBean("routingChatModelPrototype", ChatModel.class);
        return AiServices.builder(AiCodegenTypeRoutingService.class)
                .chatModel(routingChatModelPrototype)
                .build();
    }

    @Bean
    public AiCodegenTypeRoutingService aiCodegenTypeRoutingService() {
        return createAiCodegenTypeRoutingService();
    }
}
