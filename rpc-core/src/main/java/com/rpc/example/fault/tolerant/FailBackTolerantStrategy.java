package com.rpc.example.fault.tolerant;

import com.rpc.example.RpcApplication;
import com.rpc.example.constant.TolerantStrategyConstant;
import com.rpc.example.fault.retry.RetryStrategy;
import com.rpc.example.fault.retry.RetryStrategyFactory;
import com.rpc.example.model.RpcRequest;
import com.rpc.example.model.RpcResponse;
import com.rpc.example.model.ServiceMetaInfo;
import com.rpc.example.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author 15304
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy{

    /**
     * 失败恢复 - 重试其他服务节点，在故障服务恢复正常后触发,目的是将流量切换回原来的服务实例。
     *
     * @param context 上下文，用于传递数据
     * @param e       异常
     * @return        RpcResponse
     */
    @Override
    @SuppressWarnings("unchecked")
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get(TolerantStrategyConstant.SERVICE_LIST);
        ServiceMetaInfo selectedServiceMetaInfo = (ServiceMetaInfo) context.get(TolerantStrategyConstant.CURRENT_SERVICE);
        RpcRequest rpcRequest = (RpcRequest) context.get(TolerantStrategyConstant.RPC_REQUEST);
        if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
            log.error("FailOverTolerantStrategy doTolerant serviceMetaInfoList is empty");
            return null;
        }
        // 重试 selectedServiceMetaInfo 之外的其他服务
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            if (serviceMetaInfo.equals(selectedServiceMetaInfo)) {
                continue;
            }
            // 重试
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetryStrategy());
            try {
                return retryStrategy.doRetry(() -> VertxTcpClient.doRequest(rpcRequest, serviceMetaInfo));
            } catch (Exception ex) {
                // 如果重试再失败，继续重试下一个
                log.error("FailBackTolerantStrategy doTolerant {} retry fail", serviceMetaInfo);
            }
        }
        // 所有服务都重试失败
        throw new RuntimeException("FailBackTolerantStrategy doTolerant all service nodes retry fail");
    }
}
