package com.rpc.example.provider;

import com.rpc.example.RpcApplication;
import com.rpc.example.common.service.UserService;
import com.rpc.example.registry.LocalRegistry;
import com.rpc.example.server.HttpServer;
import com.rpc.example.server.VertxHttpServer;

/**
 * @author 15304
 */
public class ProviderExample {

    public static void main(String[] args) {
        // RPC框架初始化
        //RpcApplication.init();

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        // todo 这里有个问题，我们是先启动的provider，读取的端口号是老的，而consumer中关于服务端的配置实际是没生效的，只是修改了
        // 在RPC框架初始化时，让 provider 也读取配置文件，consumer与 provider 需要使用相同的配置
        // 如果将配置文件放在rpc-core中，能否实现全局配置(provider 和 consumer 配置保持一致) ---不行，使用的是默认配置
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
