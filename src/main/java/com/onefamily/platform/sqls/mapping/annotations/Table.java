package com.onefamily.platform.sqls.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对数据库表的映射对象进行注解;
 * 主要用于对象和表名的映射
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    /**
     * table name
     * @return
     */
    String name() default "";
}
