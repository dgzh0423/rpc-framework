package com.rpc.example.bootstrap;

import com.rpc.example.RpcApplication;

/**
 * @author 15304
 */
public class ConsumerBootstrap {

    /**
     * 消费方初始化
     */
    public static void init() {
        // RPC 框架初始化
        RpcApplication.init();
    }
}
