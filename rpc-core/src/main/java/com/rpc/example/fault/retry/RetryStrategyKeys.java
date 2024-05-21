package com.rpc.example.fault.retry;

/**
 * 重试策略键名常量
 * @author 15304
 */
public interface RetryStrategyKeys {

    String NO = "no";

    String FIXED_INTERVAL = "fixedInterval";

    String LINEAR = "linear";

    String EXPONENTIAL = "exponential";
}
