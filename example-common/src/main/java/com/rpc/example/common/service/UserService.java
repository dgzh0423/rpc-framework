package com.rpc.example.common.service;

import com.rpc.example.common.model.User;

/**
 * 用户服务
 * @author 15304
 */
public interface UserService {

    /**
     * 获取用户
     * @param user
     * @return user
     */
    User getUser(User user);

    /**
     * 用于 mock 测试
     * @return 1
     */
    default int getNumber(){
        return 1;
    }
}
