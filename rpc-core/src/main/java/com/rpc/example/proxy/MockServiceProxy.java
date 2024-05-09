package com.rpc.example.proxy;

import com.rpc.example.utils.DefaultValueUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * jdk动态代理生成 MOCK 调用对象，它调用的方法返回默认值
 * @author 15304
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {

    /**
     * 代理调用
     * @return 调用方法的默认值
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型，生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return DefaultValueUtils.getDefaultValue(methodReturnType);
    }

}
