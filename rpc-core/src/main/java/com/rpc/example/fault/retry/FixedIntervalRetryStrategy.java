package com.rpc.example.fault.retry;

import com.github.rholder.retry.*;
import com.rpc.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔（3s）重试, 重试3次
 * @author 15304
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{

    /**
     * 重试
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
                .withWaitStrategy(WaitStrategies.fixedWait(3000, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
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
