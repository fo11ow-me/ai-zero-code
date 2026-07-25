package com.qiujie.aizerocode.core.handler;

import com.qiujie.aizerocode.model.entity.User;
import com.qiujie.aizerocode.model.enums.ChatHistoryMessageTypeEnum;
import com.qiujie.aizerocode.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 简单文本流处理器
 * 处理 HTML 和 MULTI_FILE 类型的流式响应
 */
@Component
@Slf4j
public class SimpleTextStreamHandler {

    @Autowired
    private ChatHistoryService chatHistoryService;

    /**
     * 处理 HTML 和 MULTI_FILE 类型的流式响应
     *
     * @param appId
     * @param lginUser
     * @param flux
     * @return
     */
    public   Flux<String> handle(Long appId, User lginUser, Flux<String> flux) {
        StringBuilder sb = new StringBuilder();
        return flux.doOnNext(sb::append)
                .doOnComplete(() -> {
                    // 保存ai消息
                    chatHistoryService.addChatMessage(appId, lginUser.getId(), sb.toString(), ChatHistoryMessageTypeEnum.AI.getValue());
                })
                .doOnError(error -> {
                    chatHistoryService.addChatMessage(appId, lginUser.getId(), "AI回复失败：" + error.getMessage(), ChatHistoryMessageTypeEnum.AI.getValue());
                });
    }
}
