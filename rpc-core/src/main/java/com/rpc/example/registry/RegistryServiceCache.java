package com.rpc.example.registry;

import com.rpc.example.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心服务本地缓存，只支持保存一个服务缓存
 * @author 15304
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     *
     * @param newServiceCache 消费端发现的服务列表
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }

    /**
     * 读缓存
     *
     * @return List<ServiceMetaInfo> 读取消费端已发现的服务列表，再次调用同一服务时不需要再去注册中心查询
     */
    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }
}
