package com.rpc.example.fault.retry;

import com.github.rholder.retry.*;
import com.rpc.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author 15304
 */
@Slf4j
public class ExponentialRetryStrategy implements RetryStrategy{

    /**
     * 默认初始值1s，然后每次重试间隔乘2（即间隔为2的幂次方）
     * @param callable
     * @return
     * @throws ExecutionException
     * @throws RetryException
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .retryIfResult(result -> result.getException() != null)
                .withWaitStrategy(WaitStrategies.exponentialWait(2, 1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }

}
