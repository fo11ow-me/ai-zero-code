package com.qiujie.aizerocode.langgraph4j.ai;

import com.qiujie.aizerocode.langgraph4j.tools.ImageSearchTool;
import com.qiujie.aizerocode.langgraph4j.tools.LogoGeneratorTool;
import com.qiujie.aizerocode.langgraph4j.tools.MermaidDiagramTool;
import com.qiujie.aizerocode.langgraph4j.tools.UndrawIllustrationTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ImageCollectionServiceFactory {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ImageSearchTool imageSearchTool;

    @Autowired
    private UndrawIllustrationTool undrawIllustrationTool;

    @Autowired
    private MermaidDiagramTool mermaidDiagramTool;

    @Autowired
    private LogoGeneratorTool logoGeneratorTool;

    /**
     * 创建图片收集 AI 服务
     */
    @Bean
    public ImageCollectionService imageCollectionService() {
        return AiServices.builder(ImageCollectionService.class)
                .chatModel(chatModel)
                .tools(imageSearchTool,
                        undrawIllustrationTool,
                        mermaidDiagramTool,
                        logoGeneratorTool
                )
                .build();
    }
}
