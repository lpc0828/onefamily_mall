package com.onefamily.platform.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class StringTool {

	// 0100888888880099 => 0100888888880100
	public static final String UTF_8 = "UTF-8";

	public static String getNextCardCode(String oldCode) {
		String newCode = "" + (Long.parseLong(oldCode) + 1);
		while (newCode.length() < 16) {
			newCode = "0" + newCode;
		}
		return newCode;
	}

	/**
	 * 分隔字符 , 注意：如果前后有分隔符，String.split会多出来一个。该方法自动去掉前后分隔符再调用 String.split 注意：特殊字符 $ % 等，需要使用 转义 $, 改为 \\$ aibo zeng
	 * 2009-06-09
	 * 
	 * @param str
	 * @param ch
	 * @return
	 */
	public static String[] split(String str, char ch) {
		if (str == null) {
			return null;
		}
		if (str.charAt(0) == ch) {
			str = str.substring(1);
		}
		if (str.charAt(str.length() - 1) == ch) {
			str = str.substring(0, str.length() - 1);
		}
		return str.split(ch + "");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// System.out.println(getNextCardCode("0000888888880099"));
		/*
		 * String[] strArg = StringTool.split("$1$WX0102$0000000000000001$25036671", '$');
		 * System.out.println(strArg.length); for(int i=0;i<strArg.length;i++){ System.out.println(strArg[i]); }
		 */ String[] strArg = "1$WX0102$0000000000000001$25036671".split("\\$");
		System.out.println(strArg.length);
		for (int i = 0; i < strArg.length; i++) {
			System.out.println(strArg[i]);
		}

	}

	/**
	 * 判断字符串是否为null或空字符串,在模糊查询的时候很有意义
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0 || str.trim().length() == 0;
	}

	/**
	 * 判断字符串是否为null或空字符串,在模糊查询的时候很有意义
	 */
	public static boolean isNotEmpty(String str) {
		return (str != null && !"".equals(str.trim()));
	}

	public static boolean isNotEmpty(Long o) {
		return (o != null);
	}

	public static boolean isNotEmpty(Integer o) {
		return (o != null);
	}

	public static boolean isNotEmpty(Date o) {
		return (o != null);
	}

	public static boolean isNotEmpty(BigDecimal o) {
		return (o != null);
	}

	// public static boolean isNotEmpty(Object o){
	// return (o!=null );
	// }

	/**
	 * 字符串编码转换的实现方法
	 * 
	 * @param str
	 *            待转换编码的字符串
	 * @param newCharset
	 *            目标编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
		if (str != null) {
			// 用默认字符编码解码字符串。
			byte[] bs = str.getBytes();
			// 用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return null;
	}

	/**
	 * 只接受instanceof String 的类型
	 * 
	 * @param strObj
	 * @return
	 */
	public static String trimToEmpty(Object strObj) {
		if (strObj == null) {
			return "";
		}
		if (isEmpty(strObj.toString())) {
			return "";
		} else {
			return strObj.toString().trim();
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
}
