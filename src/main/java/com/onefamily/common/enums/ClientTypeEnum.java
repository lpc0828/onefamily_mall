package com.onefamily.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by pc.liu on 2017/8/17.
 */
public enum ClientTypeEnum {

    MiniProgram("wxapp", "小程序"),
    Android("android", "安卓客户端"),
    IOS("ios", "苹果客户端"),
    WxTouch("wxtouch", "微信公众号touch")
    ;

    private String code;
    private String name;

    ClientTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ClientTypeEnum codeOf(String code) {
        for (ClientTypeEnum memberTypeEnum : ClientTypeEnum.values()) {
            if (StringUtils.equalsIgnoreCase(memberTypeEnum.code, code)) {
                return memberTypeEnum;
            }
        }
        return null;
    }

    public static ClientTypeEnum convertFromPlatform(String platform) {
        if (StringUtils.isBlank(platform)) {
            return MiniProgram;
        } else if (StringUtils.equalsIgnoreCase("wxapp", platform)) {
            return MiniProgram;
        } else if (StringUtils.equalsIgnoreCase("android", platform)) {
            return Android;
        } else if (StringUtils.equalsIgnoreCase("ios", platform)) {
            return IOS;
        } else if (StringUtils.equalsIgnoreCase("wxtouch", platform)) {
            return WxTouch;
        }

        return null;
    }

    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
