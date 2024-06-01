package com.rpc.examplespringbootprovider.impl;

import com.rpc.example.common.model.User;
import com.rpc.example.common.service.UserService;
import com.rpc.rpcspringbootstarter.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author 15304
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
