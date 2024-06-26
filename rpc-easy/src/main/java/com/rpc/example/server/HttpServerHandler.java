package com.rpc.example.server;

import com.rpc.example.model.RpcRequest;
import com.rpc.example.model.RpcResponse;
import com.rpc.example.registry.LocalRegistry;
import com.rpc.example.serializer.JdkSerializer;
import com.rpc.example.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP 请求处理器
 * @author 15304
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {

    /**
     * @param request 处理接收到的请求，该请求体中有我们自定义的 rpcRequest
     */
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器JDK
        final Serializer serializer = new JdkSerializer();

        // 记录日志
        log.info("Received request: {} {}", request.method(), request.uri());

        // 异步处理 HTTP 请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                // 反序列化请求为封装的rpcRequest对象
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                log.error("deserialize request error: ", e);
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为 null，直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                log.error("invoke service error: ", e);
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应
     * @param request 请求
     * @param rpcResponse 封装的返回
     * @param serializer 指定的序列化器
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化封装的返回体rpcResponse
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            log.error("serialize response error: ", e);
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
