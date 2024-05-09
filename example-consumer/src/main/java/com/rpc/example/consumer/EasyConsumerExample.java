package com.rpc.example.consumer;

import com.rpc.example.common.model.User;
import com.rpc.example.common.service.UserService;
import com.rpc.example.proxy.ServiceProxyFactory;

/**
 * @author 15304
 */
public class EasyConsumerExample {

    public static void main(String[] args) {
        // 静态代理
        // UserService userService = new UserServiceProxy();

        //动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("RPC test");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
    }
}
