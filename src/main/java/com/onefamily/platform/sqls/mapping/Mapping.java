package com.onefamily.platform.sqls.mapping;


import com.onefamily.platform.sqls.mapping.annotations.Column;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.portable.ApplicationException;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class Mapping {

    public  final static Mapping m = new Mapping();

    private final Map<String, Map<String, MappingField>> mfMap = new TreeMap<String, Map<String, MappingField>>();

    private final Map<String, Constructor<?>> consMap = new TreeMap<String, Constructor<?>>();

    public Mapping() {

    }

    public Mapping(Class<?>... clazzs) {
        if (clazzs != null) {
            for (Class<?> clazz : clazzs) {
                getFieldMap(clazz);
            }
        }
    }


    public <E extends Serializable> E convertMap(Class<E> clazz, Map<String, Object> map) {
        if (clazz == null || map == null) {
            return null;
        }
        Map<String, MappingField> fieldMap = getFieldMap(clazz);
        if (fieldMap == null) {
            return null;
        }
        Constructor<?> c = consMap.get(clazz.getName());
        if (c == null) {
            return null;
        }
        Collection<MappingField> col = fieldMap.values();
        return this.convertObjectFromMap(col, c, map);
    }

    /**
     * 将List转换成指定对象的List。
     *
     * @param clazz
     *            指定的转换后的对象。
     * @param mapList
     *            List集合每个对象为map。每个map包含具体的对象信息。
     * @return List。list内每个对象元素为clazz参数指定的对象。
     * @exception ApplicationException
     *                转换失败。
     * @Author wangshuo
     * @since 1.0.1
     */
    public <E extends Serializable> List<E> convertListMap(Class<E> clazz, List<Map<String, Object>> mapList) {
        if (clazz == null || mapList == null) {
            return null;
        }
        if (mapList.size() == 0) {
            return new ArrayList<E>();
        }

        Map<String, MappingField> fieldMap = getFieldMap(clazz);
        if (fieldMap == null) {
            return null;
        }
        Constructor<?> c = consMap.get(clazz.getName());
        if (c == null) {
            return null;
        }
        Collection<MappingField> col = fieldMap.values();
        List<E> ret = new ArrayList<E>();
        for (Map<String, Object> map : mapList) {
            E e = this.convertObjectFromMap(col, c, map);
            ret.add(e);
        }
        return ret;
    }

    /**
     * 将对象转换成指定的对象。
     *
     * @param clazz
     *            指定转换成的对象。
     * @param obj
     *            被转换的对象。
     * @return clazz参数指定转换成的对象。
     * @exception ApplicationException
     *                转换失败。
     * @Author wangshuo
     * @since 1.0.0
     */
    public <E extends Serializable> E convertObject(Class<?> clazz, Object obj) {
        if (clazz == null || obj == null) {
            return null;
        }
        Map<String, MappingField> fieldMap = getFieldMap(clazz);
        // 被转换对象的field Map。
        Map<String, MappingField> objFieldMap = getFieldMap(obj.getClass());
        if (fieldMap == null || objFieldMap == null) {
            return null;
        }
        Constructor<?> c = consMap.get(clazz.getName());
        if (c == null) {
            return null;
        }
        Collection<MappingField> col = fieldMap.values();
        return this.convertObjectFromObject(col, c, objFieldMap, obj);
    }

    /**
     * 将原始数据的List转换成指定对象的List。
     *
     * @param clazz
     *            指定转换成的对象。
     * @param objList
     *            原始数据List集合。
     * @return List。list内每个对象元素为clazz参数指定的对象。
     * @exception ApplicationException
     *                转换失败。
     * @Author wangshuo
     * @since 1.0.1
     */
    public <E extends Serializable> List<E> convertList(Class<E> clazz, List<?> objList) {
        if (clazz == null || objList == null) {
            return null;
        }

        if (objList.size() == 0) {
            return new ArrayList<E>();
        }

        Map<String, MappingField> fieldMap = getFieldMap(clazz);
        // 被转换对象的field Map。
        Map<String, MappingField> objFieldMap = getFieldMap(objList.get(0).getClass());
        if (fieldMap == null || objFieldMap == null) {
            return null;
        }
        Constructor<?> c = consMap.get(clazz.getName());
        if (c == null) {
            return null;
        }
        Collection<MappingField> col = fieldMap.values();
        List<E> ret = new ArrayList<E>();
        for (Object obj : objList) {
            E e = this.convertObjectFromObject(col, c, objFieldMap, obj);
            ret.add(e);
        }
        return ret;
    }


    private <E extends Serializable> E convertObjectFromMap(Collection<MappingField> col, Constructor<?> c, Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        try {
            E e = (E) c.newInstance();
            for (MappingField mf : col) {
                if (!mf.isMapping()) {
                    continue;
                }
                Object obj = map.get(mf.getKeyName());
                if (obj != null) {
                    mf.setFieldValue(e, obj);
                }
            }
            return e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Serializable> E convertObjectFromObject(Collection<MappingField> col, Constructor<?> c,
                    Map<String, MappingField> objFieldMap, Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            E e = (E) c.newInstance();
            for (MappingField mf : col) {
                if (!mf.isMapping()) {
                    continue;
                }
                Object fieldValue = null;
                // 被转化对象的field信息。
                MappingField objField = objFieldMap.get(mf.getKeyName());
                if (objField != null) {
                    // 获得被转化对象的该字段的值。
                    fieldValue = objField.getFieldValue(obj);
                }
                if (fieldValue != null) {
                    mf.setFieldValue(e, fieldValue);
                }
            }
            return e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, MappingField> getFieldMap(Class<?> clazz) {
        String className = clazz.getName();
        Map<String, MappingField> map = mfMap.get(className);
        try {
            if (map == null) {
                Field[] fields = clazz.getDeclaredFields();
                if (fields == null || fields.length == 0) {
                    return null;
                }
                map = new TreeMap<String, MappingField>();
                for (Field field : fields) {
                    if (StringUtils.equalsIgnoreCase("serialVersionUID", field.getName())) {
                        continue;
                    }
                    Column column = getAnnotation(field);
                    MappingField mf = new MappingField(field, column);
                    map.put(mf.getKeyName(), mf);
                }
                mfMap.put(className, map);
                Constructor<?>  c = clazz.getDeclaredConstructor();
                consMap.put(className, c);
            }
            return map;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Column getAnnotation(Field f) {
        if (f.isAnnotationPresent(Column.class)) {
            return f.getAnnotation(Column.class);
        }
        return null;
    }
}
