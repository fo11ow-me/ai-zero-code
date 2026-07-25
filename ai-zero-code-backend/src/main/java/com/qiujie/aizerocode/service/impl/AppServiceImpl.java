package com.qiujie.aizerocode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.qiujie.aizerocode.core.AiCodegenFacade;
import com.qiujie.aizerocode.core.builder.VueProjectBuilder;
import com.qiujie.aizerocode.core.handler.StreamHandlerExecutor;
import com.qiujie.aizerocode.exception.BusinessException;
import com.qiujie.aizerocode.exception.ErrorCode;
import com.qiujie.aizerocode.exception.ThrowUtils;
import com.qiujie.aizerocode.model.dto.app.AppDeployRequest;
import com.qiujie.aizerocode.model.dto.app.AppQueryRequest;
import com.qiujie.aizerocode.model.entity.App;
import com.qiujie.aizerocode.mapper.AppMapper;
import com.qiujie.aizerocode.model.entity.User;
import com.qiujie.aizerocode.model.enums.ChatHistoryMessageTypeEnum;
import com.qiujie.aizerocode.model.enums.CodeGenTypeEnum;
import com.qiujie.aizerocode.model.vo.AppVO;
import com.qiujie.aizerocode.model.vo.UserVO;
import com.qiujie.aizerocode.service.AppService;
import com.qiujie.aizerocode.service.ChatHistoryService;
import com.qiujie.aizerocode.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qiujie.aizerocode.constant.AppConstant.*;

/**
 * 应用 服务层实现。
 *
 * @author qiujie
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Autowired
    private UserService userService;

    @Autowired
    private AiCodegenFacade aiCodegenFacade;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private StreamHandlerExecutor streamHandlerExecutor;

    @Autowired
    private VueProjectBuilder vueProjectBuilder;

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = new AppVO();
            BeanUtil.copyProperties(app, appVO);
            appVO.setUser(userVOMap.get(app.getUserId()));
            return appVO;
        }).collect(Collectors.toList());
    }


    /**
     * 聊天并生成代码
     *
     * @param appId
     * @param userMessage
     * @param lginUser
     * @return
     */
    @Override
    public Flux<String> chatToCodegen(Long appId, String userMessage, User lginUser) {
        // 1. 校验参数
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId 错误");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage), ErrorCode.PARAMS_ERROR, "用户提示词不能为空");
        // 2. 获取应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 权限校验，仅允许本人参与自己创建的用于构建应用的对话
        ThrowUtils.throwIf(!lginUser.getId().equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR, "无权限");
        // 4. 获取应用的代码生成模式
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "代码生成模式错误");
        // 5. 将用户消息保存到数据库中
        chatHistoryService.addChatMessage(appId, lginUser.getId(), userMessage, ChatHistoryMessageTypeEnum.USER.getValue());
        // 6. 调用ai生成代码流
        Flux<String> flux = aiCodegenFacade.generateAndSaveCodeStream(userMessage, codeGenTypeEnum, appId);
        // 7. 返回流，并将ai消息存放到数据库
        return streamHandlerExecutor.execute(appId, codeGenTypeEnum, lginUser, flux);
    }


    /**
     * 部署应用，将生成的代码文件复制到部署目录并更新数据库
     *
     * @param appDeployRequest 部署请求（包含 appId）
     * @param loginUser        当前登录用户
     * @return
     */
    @Override
    public String deployApp(AppDeployRequest appDeployRequest, User loginUser) {
        // 1. 校验参数
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId 错误");
        // 2. 获取应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 权限校验，仅允许本人部署自己的应用
        ThrowUtils.throwIf(!loginUser.getId().equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR, "无权限");
        // 4. 校验源代码目录是否存在
        String sourcePath = CODE_SAVE_PATH + File.separator + app.getCodeGenType() + File.separator + appId;
        File sourceDir = new File(sourcePath);
        ThrowUtils.throwIf(!sourceDir.exists() || !sourceDir.isDirectory(), ErrorCode.SYSTEM_ERROR, "源代码尚未生成，请先生成代码");
        // 5. vue项目单独处理
        if (app.getCodeGenType().equals(CodeGenTypeEnum.VUE_PROJECT.getValue())) {
            boolean result = vueProjectBuilder.buildProject(sourcePath);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "Vue 项目构建失败，请重试！");
            // 调整sourceDir为 dist
            sourceDir = new File(sourcePath, "dist");
        }
        // 6. 生成部署标识并复制文件到部署目录
        String deployKey = app.getDeployKey();
        deployKey = StrUtil.isBlank(deployKey) ? RandomUtil.randomString(10) : deployKey;
        String targetPath = APP_DEPLOY_PATH + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(targetPath), true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部署失败：" + e.getMessage());
        }
        // 7. 更新应用部署信息
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean result = this.updateById(updateApp);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "部署失败");
        return String.format("%s/%S", APP_DEPLOY_HOST, deployKey);
    }


    /**
     * 删除应用，删除数据库记录
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(@NonNull Serializable id) {
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            log.error("删除对话记录失败：{}", e.getMessage());
        }
        // 删除应用
        return super.removeById(appId);
    }
}
