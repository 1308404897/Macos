package com.macos.framework.annotation;


import java.lang.annotation.*;

/**
 * @author zheng.liming
 * @date 2019/8/26
 * @description 环绕通知
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Around {
    String value();
}
