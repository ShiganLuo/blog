package com.baofeng.blog.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MinioScan {

    /**
     * 是否启用嵌套扫描
     */
    boolean deep() default true;

    /**
     * 最大递归深度
     * -1 表示不限制
     */
    int maxDepth() default -1;
}
