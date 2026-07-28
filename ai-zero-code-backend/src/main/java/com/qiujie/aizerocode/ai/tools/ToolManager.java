package com.qiujie.aizerocode.ai.tools;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ToolManager {


    private final Map<String, BaseTool> toolMap = new HashMap<>();

    @Getter
    @Autowired
    private BaseTool[] tools;

    @PostConstruct
    public void initTools() {
        for (BaseTool tool : tools) {
            toolMap.put(tool.getToolName(), tool);
            log.info("注册工具：{}", tool.getToolName());
        }
        log.info("工具注册完成，共{}个工具", toolMap.size());
    }


    /**
     * 获取工具
     *
     * @param toolName
     * @return
     */
    public BaseTool getTool(String toolName) {
        return toolMap.get(toolName);
    }

}
