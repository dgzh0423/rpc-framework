package com.rpc.example.loadbalancer;

/**
 * 负载均衡器键名常量
 *
 * @author 15304
 */
public interface LoadBalancerKeys {

    /**
     * 轮询算法
     */
    String ROUND_ROBIN = "roundRobin";

    String RANDOM = "random";

    String CONSISTENT_HASH = "consistentHash";

}
