package com.rpc.example.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.rpc.example.fault.retry.FixedIntervalRetryStrategy;
import com.rpc.example.fault.retry.RetryStrategy;
import com.rpc.example.fault.tolerant.FailOverTolerantStrategy;
import com.rpc.example.fault.tolerant.TolerantStrategy;
import com.rpc.example.loadbalancer.LoadBalancer;
import com.rpc.example.loadbalancer.RoundRobinLoadBalancer;
import com.rpc.example.registry.Registry;
import com.rpc.example.registry.ZooKeeperRegistry;
import com.rpc.example.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 加载器
 * 自定义实现，支持键值对映射
 *
 * @author 15304
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载的类：接口名 =>（key => 实现类）
     */
    private static final Map<String, Map<String, Class<?>>> LOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    private static final Map<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();

    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(
            Serializer.class, Registry.class, LoadBalancer.class, RetryStrategy.class, TolerantStrategy.class);

    /**
     * 加载所有类型
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 获取某个接口的实例
     *
     * @param tClass
     * @param key
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = LOADER_MAP.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", tClassName, key));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!INSTANCE_CACHE.containsKey(implClassName)) {
            try {
                INSTANCE_CACHE.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) INSTANCE_CACHE.get(implClassName);
    }

    /**
     * 加载某个类型
     *
     * @param loadClass
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        // 扫描路径，用户自定义的 SPI 优先级高于系统 SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            //log.info("加载 {} SPI配置文件 key={} className={}", scanDir.equals(RPC_CUSTOM_SPI_DIR) ? "自定义" : "系统", key, className);
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error", e);
                }
            }
        }
        LOADER_MAP.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }

    public static void main(String[] args) {
        loadAll();
        System.out.println("LOADER_MAP: " + LOADER_MAP);
        Serializer serializer = getInstance(Serializer.class, "protobuf");
        System.out.println(serializer);
        ZooKeeperRegistry zookeeper = getInstance(Registry.class, "zookeeper");
        System.out.println(zookeeper);
        RoundRobinLoadBalancer roundRobinLoadBalancer = getInstance(LoadBalancer.class, "roundRobin");
        System.out.println(roundRobinLoadBalancer);
        FixedIntervalRetryStrategy fixedIntervalRetryStrategy = getInstance(RetryStrategy.class, "fixedInterval");
        System.out.println(fixedIntervalRetryStrategy);
        FailOverTolerantStrategy failOver = getInstance(TolerantStrategy.class, "failOver");
        System.out.println(failOver);
        System.out.println("INSTANCE_CACHE: " + INSTANCE_CACHE);
    }

}
