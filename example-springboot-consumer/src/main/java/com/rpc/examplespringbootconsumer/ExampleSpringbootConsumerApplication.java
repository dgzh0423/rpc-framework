package com.rpc.examplespringbootconsumer;

import com.rpc.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 15304
 */
@SpringBootApplication
@EnableRpc(needServer = false) // 启动RPC框架，但不启动web服务器
public class ExampleSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
    }

}
