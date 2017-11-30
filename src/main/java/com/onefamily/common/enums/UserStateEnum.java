package com.onefamily.common.enums;

/**
 */
public enum UserStateEnum {

    NORMAL(1, "normal", "正常"),
    DELETE(2, "delete", "删除"),
    DEPRECATED(3, "deprecate", "废弃");

    private int value;
    private String code;
    private String desc;

    UserStateEnum(int value, String code, String desc) {
        this.value = value;
        this.code = code;
        this.desc = desc;
    }

    public static UserStateEnum fromCode(String code) {
        for(UserStateEnum each : UserStateEnum.values()) {
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
