package com.rpc.example.loadbalancer;

import com.rpc.example.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 一致性哈希负载均衡器
 *
 * @author 15304
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性 Hash 环，存放虚拟节点
     */
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 每个服务节点对应的虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }

        // 构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 由于 requestParams 是调用方法名，故调用相同方法的请求会命中同一个服务器节点
        int hash = getHash(requestParams);

        // 选择最接近且大于等于调用请求 hash 值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null) {
            // 如果没有大于等于调用请求 hash 值的虚拟节点，则返回环首部的节点
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }


    /**
     * Hash 算法，考虑了null值和优化性能的实现
     * todo 如何实现根据请求方IP地址的hash值，保证相同IP的请求会发送到同一个服务节点
     * @param key 传入的对象
     * @return hash值
     */
    private int getHash(Object key) {
        // 处理key为null的情况
        if (key == null) {
            // 或者选择抛出IllegalArgumentException，取决于业务需求
            return 0;
        }

        // 使用JDK提供的Objects类中的hash方法来优化hashCode的计算
        // Objects.hash方法内部做了优化，能更好地处理大型对象以及减少哈希冲突
        return Objects.hash(key);
    }

}
