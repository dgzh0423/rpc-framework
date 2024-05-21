package com.rpc.example.config;

import com.rpc.example.fault.retry.RetryStrategyKeys;
import com.rpc.example.fault.tolerant.TolerantStrategyKeys;
import com.rpc.example.loadbalancer.LoadBalancerKeys;
import com.rpc.example.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 默认框架配置
 * @author 15304
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "rpc-framework";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.RANDOM;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;

    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;
}
