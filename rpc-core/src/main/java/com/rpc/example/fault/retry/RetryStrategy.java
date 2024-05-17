package com.rpc.example.fault.retry;

import com.rpc.example.model.RpcResponse;
import java.util.concurrent.Callable;

/**
 * 重试策略接口
 * @author 15304
 */
public interface RetryStrategy {

    /**
     * 重试
     * @param callable
     * @return
     * @throws Exception
     */
    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;
}
