package com.onefamily.common.enums;


/**
 * 是否
 */
public enum YesNoEnum {

	YES(1, "是", "Y"),
	NO(0, "否", "N"), 
	
	;
	
	private int code;
	private String name;
	private String value;
	
	private YesNoEnum(int code, String name, String value) {
		this.code = code;
		this.name = name;
		this.value = value;
	}

	public static YesNoEnum codeOf(int code) {
		for (YesNoEnum memberTypeEnum : YesNoEnum.values()) {
			if (memberTypeEnum.code == code) {
				return memberTypeEnum;
			}
		}
		return NO;
	}
	
	public static YesNoEnum ynOf(String value) {
		for (YesNoEnum en : YesNoEnum.values()) {
			if (en.value.equalsIgnoreCase(value)) {
				return en;
			}
		}
		return NO;
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}