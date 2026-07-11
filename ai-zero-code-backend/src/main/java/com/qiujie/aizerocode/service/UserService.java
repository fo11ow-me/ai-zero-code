package com.qiujie.aizerocode.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.qiujie.aizerocode.model.dto.UserQueryRequest;
import com.qiujie.aizerocode.model.entity.User;
import com.qiujie.aizerocode.model.vo.LoginUserVO;
import com.qiujie.aizerocode.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author qiujie
 * @since 2026-07-10
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 获取登录用户信息
     *
     * @param user 用户
     * @return 登录用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);


    /**
     * 获取用户列表
     *
     * @param userList 用户列表
     * @return 用户列表
     */
    List<UserVO> getUserVOList(List<User> userList);


    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 登录用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    /**
     * 用户登出
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取加密密码
     *
     * @param userPassword 用户密码
     * @return 加密密码
     */
    String getEncryptPassword(String userPassword);
}

