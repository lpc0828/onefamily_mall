package com.onefamily.platform.sqls.mapping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 字段名称, 默认将按照驼峰命名法机型转换
     * @return
     */
    String name() default "";

    /**
     * 序列号
     * @return
     */
    String sequence() default "";

    /**
     * 是否需要和数据库表实例进行转化
     * @return
     */
    boolean isMapping() default true;

    /**
     * 是否是主键
     * @return
     */
    boolean isPrimaryKey() default false;
}
