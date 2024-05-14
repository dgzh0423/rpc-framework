package com.rpc.example.server;

/**
 * @author 15304
 */
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port 端口号
     */
    void doStart(int port);
}
