package com.rpc.example.fault.retry;

import com.github.rholder.retry.*;
import com.rpc.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LinearRetryStrategy implements RetryStrategy{

    /**
     * 指定初始等待值1s，然后重试间隔随次数等差递增(1s)
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
                .withWaitStrategy(WaitStrategies.incrementingWait(1000, TimeUnit.MILLISECONDS,1000, TimeUnit.MILLISECONDS))
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
