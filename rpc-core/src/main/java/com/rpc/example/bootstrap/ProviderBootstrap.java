package com.rpc.example.bootstrap;

import com.rpc.example.RpcApplication;
import com.rpc.example.config.RegistryConfig;
import com.rpc.example.config.RpcConfig;
import com.rpc.example.model.ServiceMetaInfo;
import com.rpc.example.model.ServiceRegisterInfo;
import com.rpc.example.registry.LocalRegistry;
import com.rpc.example.registry.Registry;
import com.rpc.example.registry.RegistryFactory;
import com.rpc.example.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * @author 15304
 */
public class ProviderBootstrap {

    /**
     * 提供方初始化
     * @param serviceRegisterInfoList 服务注册信息
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC框架自动初始化
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            // serviceName:com.rpc.example.common.service.UserService
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }

        //启动 TCP web 服务
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());

        // 启动 HTTP web 服务
        // HttpServer httpServer = new VertxHttpServer();
        // todo 这里有个问题，我们是先启动的provider，读取的端口号是老的，而consumer中关于服务端的配置实际是没生效的，只是修改了
        // 在RPC框架初始化时，让 provider 也读取配置文件，consumer与 provider 需要使用相同的配置
        // 如果将配置文件放在rpc-core中，能否实现全局配置(provider 和 consumer 配置保持一致) ---不行，使用的是默认配置
        // httpServer.doStart(rpcConfig.getServerPort());
    }

}
