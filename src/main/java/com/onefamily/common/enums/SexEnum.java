package com.onefamily.common.enums;

public enum SexEnum {

    MALE(1, "male", "男"),
    FEMALE(2, "female", "女"),
    UNKNOWN(3, "unknown", "未知");

    private int value;
    private String code;
    private String desc;

    SexEnum(int value, String code, String desc) {
        this.value = value;
        this.code = code;
        this.desc = desc;
    }

    public static SexEnum fromValue(int value) {
        for(SexEnum each : SexEnum.values()) {
            if(value == each.getValue())
                return each;
        }
        return null;
    }

    public static SexEnum fromCode(String code) {
        for(SexEnum each : SexEnum.values()) {
            if(each.getCode().equalsIgnoreCase(code) )
                return each;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
