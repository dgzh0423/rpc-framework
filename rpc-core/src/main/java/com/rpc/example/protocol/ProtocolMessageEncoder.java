package com.rpc.example.protocol;

import com.rpc.example.serializer.Serializer;
import com.rpc.example.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议消息编码器
 *
 * @author 15304
 */
public class ProtocolMessageEncoder {

    /**
     * 编码
     *
     * @param protocolMessage 自定义协议信息
     * @return Buffer
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        if (protocolMessage == null || protocolMessage.getHeader() == null) {
            return Buffer.buffer();
        }
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 依次向缓冲区写入字节
        Buffer buffer = Buffer.buffer();
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        // 对rpcRequest 或 rpcResponse序列化后再写入消息体中
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 写入 body 长度
        buffer.appendInt(bodyBytes.length);
        // 写入消息体
        buffer.appendBytes(bodyBytes);
        return buffer;
    }
}
