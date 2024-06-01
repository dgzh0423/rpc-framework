package com.rpc.examplespringbootconsumer.consumer;

import com.rpc.example.common.model.User;
import com.rpc.example.common.service.UserService;
import com.rpc.rpcspringbootstarter.annotation.RpcReference;

/**
 * @author 15304
 */
public class ExampleServiceConsumer {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;

    public ExampleServiceConsumer(UserService userService) {
        this.userService = userService;
    }

    /**
     * 测试方法
     */
    public void test() {
        User user = new User();
        user.setName("yupi");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }
}
