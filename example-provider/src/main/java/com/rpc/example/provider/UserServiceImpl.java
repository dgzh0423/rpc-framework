package com.rpc.example.provider;

import com.rpc.example.common.model.User;
import com.rpc.example.common.service.UserService;

/**
 * 用户服务实现类
 * @author 15304
 */
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
