package com.rpc.example.fault.tolerant;

import com.rpc.example.model.RpcResponse;

import java.util.Map;

/**
 * @author 15304
 */
public interface TolerantStrategy {

    /**
     * 容错处理
     * @param context 上下文，用于传递数据
     * @param e 异常
     * @return RpcResponse
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
