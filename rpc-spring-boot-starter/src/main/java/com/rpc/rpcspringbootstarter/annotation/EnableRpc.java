package com.rpc.rpcspringbootstarter.annotation;

import com.rpc.rpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.rpc.rpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.rpc.rpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 Rpc 注解
 * @author 15304
 */
@Target({ElementType.TYPE})  // 注解作用在类,接口或枚举上
@Retention(RetentionPolicy.RUNTIME) // 该注解在编译后仍会被保留在class文件中，并且可以在运行时通过反射访问到。
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    /**
     * 需要启动 server
     */
    boolean needServer() default true;
}
