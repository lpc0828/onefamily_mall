package com.onefamily.mall.service.sms;


import com.onefamily.platform.dataformat.ResultVO;

public interface ISmsService {

    /**
     * 【达令家业务线】发送手机验证码通用接口
     * @param mobile 待发验证码手机号
     * @param moduleCode 业务模块编码，如果为空 则走默认值;
     * @param limitSendInterval 限制发送频率的时间间隔，单位：秒.
     *                          当 < 0 时， 默认60s；
     *                          当 = 0 时，不缓存；</br>
     *                          当 > 0 时，根据指定的值进行缓存
     * @param expireSeconds 获取到的验证码 超时时间，单位：秒
     *                      当 为 null 或 <= 0 时， 超时时间默认10分钟
     *                      当 > 0 时， 根据指定的时间进行缓存
     * @return 返回结果
     *          true 表示发送成功或者在限制发送时间范围内，
     *          false 表示发送失败，需要根据message排查原因
     */
    ResultVO<String> sendVerifyCode(String mobile, String moduleCode, int limitSendInterval, Integer expireSeconds);


    /**
     * 【达令家业务线】验证手机验证码通用接口
     * @param mobile 待验证验证码手机号
     * @param moduleCode 业务模块编码，如果为空 则走默认值，、
     *                   请务必与发送验证码时保持一致！
     * @param verifyCode 用户最新收到的短信验证码
     * @return 返回结果
     *     true 表示验证成功
     *     false 表示验证失败
     */
    ResultVO<String> authVerifyCode(String mobile, String moduleCode, String verifyCode);
}
