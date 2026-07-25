package com.qiujie.aizerocode.core.handler;

import com.qiujie.aizerocode.model.entity.User;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 流处理器执行器
 * 根据代码生成类型创建合适的流处理器：
 * 1. 传统的 Flux<String> 流（HTML、MULTI_FILE） -> SimpleTextStreamHandler
 * 2. TokenStream 格式的复杂流（VUE_PROJECT） -> JsonMessageStreamHandler
 */
@Component
@Slf4j
public class StreamHandlerExecutor {

    @Autowired
    private JsonMessageStreamHandler jsonMessageStreamHandler;

    @Autowired
    private SimpleTextStreamHandler simpleTextStreamHandler;


    public Flux<String> execute(Long appId, CodeGenTypeEnum type, User loginUser, Flux<String> flux) {
        return switch (type) {
            case HTML, MULTI_FILE -> simpleTextStreamHandler.handle(appId, loginUser, flux);
            case VUE_PROJECT -> jsonMessageStreamHandler.handle(appId, loginUser, flux);
        };
    }
}
