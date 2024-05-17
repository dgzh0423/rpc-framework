package com.rpc.example.fault.retry;

import com.rpc.example.model.RpcResponse;
import java.util.concurrent.Callable;

/**
 * 不重试
 * @author 15304
 */
public class NoRetryStrategy implements RetryStrategy{

    /**
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }

}
