package com.rpc.example;

import com.rpc.example.config.RegistryConfig;
import com.rpc.example.config.RpcConfig;
import com.rpc.example.constant.RpcConstant;
import com.rpc.example.registry.Registry;
import com.rpc.example.registry.RegistryFactory;
import com.rpc.example.serializer.Serializer;
import com.rpc.example.serializer.SerializerFactory;
import com.rpc.example.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;


/**
 * RPC 框架应用
 * 相当于 holder，存放了项目全局用到的配置信息--双检锁单例模式实现
 * @author 15304
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig RPC框架配置
     */
    public static void init(RpcConfig newRpcConfig) {

        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());

        // 加载序列化器
        String serializerKey = rpcConfig.getSerializer();
        Serializer serializer = SerializerFactory.getInstance(serializerKey);
        log.info("serializer init, type = {}", serializer);

        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        // 创建并注册 Shutdown Hook，JVM 正常退出时会执行注册中心的destroy操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * provider 和 consumer 启动时，框架自动初始化
     */
    public static void init() {
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        } catch (Exception e) {
            // 配置加载失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }


    /**
     * 获取配置
     *
     * @return RpcConfig
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
