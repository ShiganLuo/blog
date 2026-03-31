package com.baofeng.blog.common.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)  // 运行时可通过反射读取
@Target(ElementType.FIELD)            // 只能标记字段，字要求为String类型
public @interface MinioFile {
}
