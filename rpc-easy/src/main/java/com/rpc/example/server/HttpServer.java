package com.rpc.example.server;

/**
 * @author 15304
 */
public interface HttpServer {

    /**
     * 启动服务器
     *
     * @param port
     */
    void doStart(int port);
}
