package com.qiujie.aizerocode.ai;

import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;
import dev.langchain4j.service.SystemMessage;

public interface AiCodegenTypeRoutingService {


    @SystemMessage(fromResource = "prompt/codegen-routing-system-prompt.txt")
    CodeGenTypeEnum routeCodegenType(String systemMessage);
}
