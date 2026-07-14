package com.qiujie.aizerocode.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.qiujie.aizerocode.model.dto.app.AppQueryRequest;
import com.qiujie.aizerocode.model.entity.App;
import com.qiujie.aizerocode.model.vo.AppVO;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author qiujie
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用视图
     *
     * @param app
     * @return
     */
    AppVO getAppVO(App app);


    /**
     * 获取查询包装类
     *
     * @param appQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);


    /**
     * 获取应用视图列表
     *
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

}
