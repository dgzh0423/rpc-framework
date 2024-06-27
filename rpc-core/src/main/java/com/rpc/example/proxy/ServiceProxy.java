package com.rpc.example.proxy;

import cn.hutool.core.collection.CollUtil;
import com.rpc.example.RpcApplication;
import com.rpc.example.config.RpcConfig;
import com.rpc.example.constant.RpcConstant;
import com.rpc.example.constant.TolerantStrategyConstant;
import com.rpc.example.fault.retry.RetryStrategy;
import com.rpc.example.fault.retry.RetryStrategyFactory;
import com.rpc.example.fault.tolerant.TolerantStrategy;
import com.rpc.example.fault.tolerant.TolerantStrategyFactory;
import com.rpc.example.loadbalancer.LoadBalancer;
import com.rpc.example.loadbalancer.LoadBalancerFactory;
import com.rpc.example.model.RpcRequest;
import com.rpc.example.model.RpcResponse;
import com.rpc.example.model.ServiceMetaInfo;
import com.rpc.example.registry.Registry;
import com.rpc.example.registry.RegistryFactory;
import com.rpc.example.server.tcp.VertxTcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * jdk动态代理（代替调用方来对服务接口进行调用）
 * <a href="https://javaguide.cn/java/basis/proxy.html#_3-1-jdk-%E5%8A%A8%E6%80%81%E4%BB%A3%E7%90%86%E6%9C%BA%E5%88%B6"/a>
 *
 * @author 15304
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        // 指定序列化器
        // final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        // 序列化
        // byte[] bodyBytes = serializer.serialize(rpcRequest);

        // 获取RPC框架的全局配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        System.out.println("rpcConfig:" + rpcConfig);

        // 根据rpc配置文件中配置的注册中心来选择特定类型的注册中心（ETCD，ZooKeeper。。。），类似选择不同类型的序列化器
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        // 从注册中心获取服务节点地址（服务可能分布在不同服务器上）
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            throw new RuntimeException("暂无服务地址");
        }

        // 使用特定负载均衡算法来选择实际要请求的服务节点地址，默认随机
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("methodName", rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        // 发送HTTP请求
        //try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
        //        .body(bodyBytes)
        //        .execute()) {
        //    byte[] result = httpResponse.bodyBytes();
        //    RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
        //    return rpcResponse.getData();
        //}

        // 发送TCP请求
        RpcResponse rpcResponse;
        try {
            // 先重试
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            // todo 记录请求日志
            rpcResponse = retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo));
        } catch (Exception e){
            // 重试仍然失败，再执行容错
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            // 构造上下文，包括从注册中心获取的服务节点列表，当前要请求的服务节点（通过负载均衡算法选择的），当前的RPC请求
            Map<String, Object> context = new HashMap<>();
            context.put(TolerantStrategyConstant.SERVICE_LIST, serviceMetaInfoList);
            context.put(TolerantStrategyConstant.CURRENT_SERVICE, selectedServiceMetaInfo);
            context.put(TolerantStrategyConstant.RPC_REQUEST, rpcRequest);
            rpcResponse = tolerantStrategy.doTolerant(context, e);
        }

        return rpcResponse.getData();

    }
}
