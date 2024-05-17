package com.rpc.example.fault.retry;

import com.rpc.example.spi.SpiLoader;

/**
 * 重试策略工厂（用于获取重试器对象）
 *
 * @author 15304
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试器
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new FixedIntervalRetryStrategy();

    /**
     * 获取实例
     *
     * @param key
     * @return RetryStrategy
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }

}
