package com.rpc.example.registry;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rpc.example.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * CaffeineCache,用于缓存服务注册信息
 * @author 15304
 */
@Slf4j
public class CaffeineCache {

    private static Cache<String, List<ServiceMetaInfo>> serviceCache;

    /**
     * 初始化缓存
     */
    public void init() {
        serviceCache = Caffeine.newBuilder()
                // 初始数量
                .initialCapacity(10)
                // 最大条数
                .maximumSize(10)
                // expireAfterWrite和expireAfterAccess同时存在时，以expireAfterWrite为准
                // 最后一次写操作后经过指定时间过期
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .expireAfterAccess(30, TimeUnit.SECONDS)
                // 监听缓存被移除
                .removalListener((key, val, removalCause) -> log.info("listener remove : {} ,{} ,{}", key, val, removalCause))
                // 记录缓存的命中情况
                .recordStats()
                .build();
    }

    /**
     * 写入缓存
     * @param serviceKey 服务名
     * @param newServiceMetaInfoList 服务元信息列表
     */
    public void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceMetaInfoList) {
        serviceCache.put(serviceKey, newServiceMetaInfoList);
    }

    /**
     * 读取缓存
     * @param serviceKey 服务名
     * @return 服务元信息列表
     */
    public List<ServiceMetaInfo> readCache(String serviceKey) {
        // get(key, k -> value)方法支持缓存不存在时，原子地将值写入缓存
        List<ServiceMetaInfo> cachedServices = serviceCache.getIfPresent(serviceKey);
        // 如果缓存不存在，则返回null -- 会有NPE问题
        // 解决：如果缓存不存在，返回一个默认的空list
        return cachedServices != null ? cachedServices : new ArrayList<>();
    }

    /**
     * 清除特定服务缓存
     * @param serviceKey 服务名
     */
    public void clearCache(String serviceKey) {
        serviceCache.invalidate(serviceKey);
    }

    /**
     * 清空所有缓存
     */
    public void clearAll() {
        // serviceCache == null;
        serviceCache.invalidateAll();
    }
}
