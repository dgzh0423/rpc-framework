package com.rpc.example.fault.tolerant;

import com.rpc.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author 15304
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{

    private final RpcResponse defaultResponse;

    public FailSafeTolerantStrategy(RpcResponse defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    public FailSafeTolerantStrategy(){
        this.defaultResponse = new RpcResponse();
    }

    /**
     * 静默处理 - 不通知调用方失败，只记录日志，并返回默认响应
     * @param context 上下文，用于传递数据
     * @param e 异常
     * @return defaultResponse
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("FailSafeTolerantStrategy doTolerant", e);
        return defaultResponse;
    }
}
