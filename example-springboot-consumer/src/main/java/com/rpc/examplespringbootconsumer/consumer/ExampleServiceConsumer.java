package com.rpc.examplespringbootconsumer.consumer;

import com.rpc.example.common.model.User;
import com.rpc.example.common.service.UserService;
import com.rpc.rpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

/**
 * @author 15304
 */
@Service
public class ExampleServiceConsumer {

    /**
     * 使用 Rpc 框架注入
     */
    @RpcReference
    private UserService userService;

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
