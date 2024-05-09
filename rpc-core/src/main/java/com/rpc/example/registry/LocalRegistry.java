package com.rpc.example.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 *
 * @author 15304
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> MAP = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName 服务名
     * @param implClass 服务实现类
     */
    public static void register(String serviceName, Class<?> implClass) {
        MAP.put(serviceName, implClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名
     * @return 服务实现类
     */
    public static Class<?> get(String serviceName) {
        return MAP.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName 服务名
     */
    public static void remove(String serviceName) {
        MAP.remove(serviceName);
    }
}
