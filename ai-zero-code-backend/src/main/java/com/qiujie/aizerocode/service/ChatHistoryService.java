package com.qiujie.aizerocode.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.qiujie.aizerocode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.qiujie.aizerocode.model.entity.ChatHistory;
import com.qiujie.aizerocode.model.entity.User;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author qiujie
 */
public interface ChatHistoryService extends IService<ChatHistory> {


    /**
     * 添加对话消息
     *
     * @param appId
     * @param userId
     * @param message
     * @param messageType
     * @return
     */
    boolean addChatMessage(Long appId, Long userId, String message, String messageType);


    /**
     * 删除对话消息
     *
     * @param appId
     * @return
     */
    boolean deleteByAppId(Long appId);


    /**
     * 查询应用下的对话消息
     *
     * @param appId
     * @param pageSize
     * @param lastCreateTime
     * @param loginUser
     * @return
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);

    /**
     * 构造历史消息查询条件
     *
     * @param chatHistoryQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 加载历史消息
     *
     * @param appId
     * @param chatMemory
     * @param maxMessages
     * @return
     */
    Integer loadChatHistory(Long appId, MessageWindowChatMemory chatMemory, int maxMessages);

}
