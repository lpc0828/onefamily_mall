package com.onefamily.platform.sqls.mapping;


import com.onefamily.platform.sqls.mapping.annotations.Column;

import java.lang.reflect.Field;

public class MappingField {

    /**
     * 字段的Field属性。
     */
    private Field field;

    /**
     * 对外转换使用的key。默认是成员变量的名称。即Field的getName()。
     */
    private String keyName;

    /**
     * 是否映射填充,默认是。
     */
    private boolean isMapping = true;
    /**
     * 是否主键,默认否。
     */
    private boolean isPrimaryKey = false;
    /**
     * 主键序列
     */
    private String sequenceName;

    public MappingField(Field field, Column ann) {
        super();
        // 如果标注不为null，使用标注定义的内容来设置后两个字段。
        if (ann != null) {
            this.isMapping = ann.isMapping();
            this.isPrimaryKey = ann.isPrimaryKey();
            if (ann.name() != null && ann.name().trim().length() != 0) {
                this.keyName = ann.name();
            }
            if (isPrimaryKey) {
                this.sequenceName = ann.sequence();
            }
        }
        // 如果ann没有设置keyName值，那么默认使用field名作为该值。
        if (this.keyName == null) {
            this.keyName = field.getName();
        }
        this.field = field;
        this.field.setAccessible(true);
    }

    public Field getField() {
        return field;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public boolean isMapping() {
        return isMapping;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public Object getFieldValue(Object entity) throws IllegalArgumentException, IllegalAccessException {
        return this.field.get(entity);
    }

    public void setFieldValue(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        this.field.set(obj, value);
    }
}
