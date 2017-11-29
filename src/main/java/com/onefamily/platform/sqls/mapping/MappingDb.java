package com.onefamily.platform.sqls.mapping;

import com.onefamily.platform.sqls.mapping.annotations.Table;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappingDb {

    private Class<?> clazz;

    private Object object;

    private boolean mapUnderscoreToCamelCase = true;

    private MappingDb() {
    }

    public MappingDb(Class<?> clazz) {
        this();
        this.clazz = clazz;
    }

    public MappingDb(Object object) {
        this();
        this.clazz = object.getClass();
        this.object = object;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public static String camelToUnderscore(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        Pattern pattern = Pattern.compile("[A-Z]");
        StringBuilder builder = new StringBuilder(param);
        Matcher matcher = pattern.matcher(param);
        int i =0;
        while (matcher.find()) {
            builder.replace(matcher.start()+i, matcher.end()+i, StringUtils.join("_", matcher.group().toLowerCase()));
            i++;
        }
        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }

        return builder.toString();
    }

    /**
     *
     * @param param
     * @return
     */
    public static String underscoreToCamel(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        Pattern pattern = Pattern.compile("_");
        String[] args = pattern.split("_");
        StringBuilder builder = new StringBuilder("");
        for (String arg : args) {
            if (StringUtils.isBlank(arg)) {
                continue;
            }
            if (builder.equals("")) {
                builder.append(arg);
            } else {
                builder.append(arg.replace(String.valueOf(arg.charAt(0)), String.valueOf(arg.charAt(0)).toUpperCase()));
            }
        }
        return builder.toString();
    }


    public List<String> getColumns() {
        List<String> columns = new ArrayList<String>();
        Map<String, MappingField> map = Mapping.m.getFieldMap(this.clazz);
        Set<String> keySet = map.keySet();
        String keyName = null;
        MappingField objField = null;
        for (String key : keySet) {
            objField = map.get(map.get(key).getKeyName());
            if (!objField.isMapping()) {
                continue;
            }
            keyName = objField.getKeyName();
            if (mapUnderscoreToCamelCase) {
                keyName = camelToUnderscore(keyName);
                if (null == keyName || "".equals(keyName)) {
                    continue;
                }
            }
            columns.add(keyName);
        }
        return columns;
    }

    /**
     * 将对象属性转换对应的数据库字段
     *
     * @return List<String>
     * @Author 杨健/YangJian
     * @Date 2015年5月7日 上午11:48:07
     * @Version 1.0.0
     */
    public List<MappingField> getFields() {
        List<MappingField> columns = new ArrayList<MappingField>();
        Map<String, MappingField> map = Mapping.m.getFieldMap(this.clazz);
        Set<String> keySet = map.keySet();
        MappingField objField = null;
        for (String key : keySet) {
            objField = map.get(map.get(key).getKeyName());
            if (!objField.isMapping()) {
                continue;
            }
            columns.add(objField);
        }
        return columns;
    }

    /**
     * 获取数据库表名
     *
     * @return String
     * @Author 杨健/YangJian
     * @Date 2015年10月16日 上午11:03:46
     * @Version 1.0.0
     */
    public String getTableName() {
        Table table = clazz.getAnnotation(Table.class);
        if (isNotEmpty(table)) {
            return table.name();
        }
        if (mapUnderscoreToCamelCase) {
            return camelToUnderscore(clazz.getSimpleName());
        } else {
            return clazz.getSimpleName();
        }
    }

    /**
     * 判断一个对象是否为空。它支持如下对象类型：
     * <ul>
     * <li>null : 一定为空
     * <li>字符串 : ""为空,多个空格也为空
     * <li>数组
     * <li>集合
     * <li>Map
     * <li>其他对象 : 一定不为空
     * </ul>
     *
     * @param obj
     *            任意对象
     * @return 是否为空
     */
    public final static boolean isEmpty(final Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return "".equals(String.valueOf(obj).trim());
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map<?, ?>) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        return false;
    }

    public final static boolean isNotEmpty(final Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 获取主键字段名
     *
     * @return String
     * @Author 杨健/YangJian
     * @Date 2015年12月3日 下午8:17:47
     * @Version 1.0.0
     */
    public String getIdName() {
        String idName = null;
        MappingField objField = null;
        Map<String, MappingField> map = Mapping.m.getFieldMap(this.clazz);

        try {

            for (String key : map.keySet()) {
                objField = map.get(map.get(key).getKeyName());
                if (objField != null && (objField.isPrimaryKey() || "id".equalsIgnoreCase(key))) {
                    idName = key;
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return idName;
    }

    /**
     * 获取主键值
     *
     * @return Object
     * @Author 杨健/YangJian
     * @Date 2015年12月3日 下午8:19:58
     * @Version 1.0.0
     */
    public Object getIdValue() {

        Object fieldValue = null;
        MappingField objField = null;
        Map<String, MappingField> map = Mapping.m.getFieldMap(this.clazz);

        try {
            for (String key : map.keySet()) {
                objField = map.get(map.get(key).getKeyName());
                if (objField != null && (objField.isPrimaryKey() || "id".equalsIgnoreCase(key))) {
                    fieldValue = objField.getFieldValue(this.object);
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fieldValue;
    }

    /**
     * 获取给定字段值
     *
     * @param column
     * @return Object
     * @Author 杨健/YangJian
     * @Date 2015年12月3日 下午8:20:13
     * @Version 1.0.0
     */
    public Object getValue(String column) {

        if (mapUnderscoreToCamelCase) {
            column = underscoreToCamel(column);
        }

        Map<String, MappingField> map = Mapping.m.getFieldMap(this.clazz);
        Object fieldValue = null;
        try {

            MappingField objField = map.get(map.get(column).getKeyName());
            if (objField != null) {
                fieldValue = objField.getFieldValue(this.object);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fieldValue;
    }

    /**
     * 获取所有值
     *
     * @return Object[]
     * @Author 杨健/YangJian
     * @Date 2015年12月3日 下午8:20:51
     * @Version 1.0.0
     */
    public Object[] getValues(boolean useNewId) {

        // 被转换对象的field Map，获取属性对应的值
        this.setMapUnderscoreToCamelCase(false);

        Map<String, MappingField> map = Mapping.m.getFieldMap(this.clazz);
        List<String> columns = getColumns();
        Object[] values = new Object[columns.size()];
        Object fieldValue = null;
        MappingField objField = null;
        try {
            int i = 0;
            for (String key : columns) {
                // 被转化对象的field信息。
                objField = map.get(map.get(key).getKeyName());
                if (!objField.isMapping()) {
                    continue;
                }
                if (objField != null) {
                    // 获得被转化对象的该字段的值。
                    fieldValue = objField.getFieldValue(this.object);
                    if ((fieldValue == null || "".equals(fieldValue)) && (objField.isPrimaryKey() || "id".equalsIgnoreCase(key)) && useNewId) {
                        fieldValue = StringUtils.join("nextval ('", objField.getSequenceName(), "')");
                    }
                }
                values[i++] = fieldValue;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return values;
    }
}
