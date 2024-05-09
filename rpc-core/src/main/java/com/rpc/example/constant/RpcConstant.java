package com.rpc.example.constant;

/**
 * RPC 相关常量
 *
 * @author 15304
 */
public interface RpcConstant {

    /**
     * 默认配置文件加载前缀
     * 可以读取到类似：rpc.name=xxx  rpc.version=x.x
     */
    String DEFAULT_CONFIG_PREFIX = "rpc";

    /**
     * 默认服务版本
     */
    String DEFAULT_SERVICE_VERSION = "1.0";

}
