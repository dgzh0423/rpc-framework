package com.rpc.example.consumer;

import com.rpc.example.bootstrap.ConsumerBootstrap;
import com.rpc.example.common.model.User;
import com.rpc.example.common.service.UserService;
import com.rpc.example.proxy.ServiceProxyFactory;


/**
 * @author 15304
 */
public class ConsumerExample {

    public static void main(String[] args) {
        // 消费方初始化
        ConsumerBootstrap.init();

        // 生成代理对象
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
        //int number = userService.getNumber();
        //System.out.println(number);
    }
}
