package com.baofeng.blog.common.util.minio;

import com.baofeng.blog.common.annotation.MinioFile;
import com.baofeng.blog.common.annotation.MinioScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MinioUrlConverter {

    @Value("${file.public-base-url}")
    private String prefix;


    /**
     * 反射字段缓存（Class -> Fields）
     */
    private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 对外统一入口
     */
    public void convert(Object target) {
        if (target == null) return;
        Set<Integer> visited = new HashSet<>();
        convertInternal(target, 0, visited);
    }

    // ================= 核心递归 =================

    private void convertInternal(Object target, int depth, Set<Integer> visited) {
        if (target == null) return;

        // 循环引用保护（identity）
        int identity = System.identityHashCode(target);
        if (!visited.add(identity)) {
            return;
        }

        Class<?> clazz = target.getClass();

        // 基础类型直接跳过
        if (isSimpleType(clazz)) {
            return;
        }

        // Map 支持
        if (target instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                convertInternal(value, depth + 1, visited);
            }
            return;
        }

        // Optional 支持
        if (target instanceof Optional<?> optional) {
            optional.ifPresent(v -> convertInternal(v, depth + 1, visited));
            return;
        }

        // Collection 支持
        if (target instanceof Collection<?> collection) {
            for (Object element : collection) {
                convertInternal(element, depth + 1, visited);
            }
            return;
        }

        // 深度 & 注解控制
        MinioScan scan = clazz.getAnnotation(MinioScan.class);
        if (scan != null) {
            if (!scan.deep()) return;
            if (scan.maxDepth() >= 0 && depth > scan.maxDepth()) return;
        }

        // 普通对象字段处理
        for (Field field : getFields(clazz)) {
            field.setAccessible(true);

            try {
                Object value = field.get(target);
                if (value == null) continue;

                // @MinioFile
                if (field.isAnnotationPresent(MinioFile.class)
                        && value instanceof String objectName
                        && !objectName.isBlank()) {

                    field.set(target, buildPermanentUrl(objectName));
                    continue;
                }

                // 递归进入
                convertInternal(value, depth + 1, visited);

            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        "Failed to convert MinIO url: "
                                + clazz.getSimpleName() + "." + field.getName(),
                        e
                );
            }
        }
    }

    // ================= 工具方法 =================

    private Field[] getFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, Class::getDeclaredFields);
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.equals(String.class)
                || Number.class.isAssignableFrom(clazz)
                || clazz.equals(Boolean.class)
                || clazz.equals(Character.class)
                || clazz.isEnum()
                || clazz.getPackageName().startsWith("java.");
    }

    private String buildPermanentUrl(String objectName) {
        return prefix  + objectName;
    }
}
