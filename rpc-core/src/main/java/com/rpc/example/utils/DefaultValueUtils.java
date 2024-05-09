package com.rpc.example.utils;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于 MOCK 根据服务返回类型生成默认值
 * @author 15304
 */
public class DefaultValueUtils {

    private static final Map<Class<?>, Object> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put(boolean.class, false);
        DEFAULTS.put(byte.class, (byte) 0);
        DEFAULTS.put(char.class, '\u0000');
        DEFAULTS.put(short.class, (short) 0);
        DEFAULTS.put(int.class, 0);
        DEFAULTS.put(long.class, 0L);
        DEFAULTS.put(float.class, 0.0f);
        DEFAULTS.put(double.class, 0.0d);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getDefaultValue(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            // 基本数据类型
            return (T) getPrimitiveDefault(clazz);
        } else {
            // 包装类型和其他引用类型
            return clazz.isArray() ? getDefaultArray(clazz) : null;
        }
    }

    private static Object getPrimitiveDefault(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            throw new IllegalArgumentException("Not a primitive type: " + clazz.getName());
        }
        return DEFAULTS.get(clazz);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getDefaultArray(Class<T> clazz) {
        // 对于数组，返回空数组
        return (T) Array.newInstance(clazz.getComponentType(), 0);
    }

}