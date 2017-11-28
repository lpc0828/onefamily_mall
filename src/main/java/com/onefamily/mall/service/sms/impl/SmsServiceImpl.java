package com.onefamily.mall.service.sms.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onefamily.common.RedisPool;
import com.onefamily.mall.service.sms.ISmsService;
import com.onefamily.platform.ResultVO;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Map;

@Service
public class SmsServiceImpl implements ISmsService {

    private static final String VerifyCodeLimitIntervalKey = "verify_code_send_limit";
    private static final String DefaultModelCode = "xc_sms";
    private static final int DefaultSendIntervalSeconds = 1*60-1;
    private static final int DefaultVerifyCodeExpireSeconds = 10*60-1;

    private static final String VerifyCodeCacheKey = "verify_code_key";

    private String genLimitIntervalKey(String moduleCode, String mobile) {
        return StringUtils.join(Lists.newArrayList(VerifyCodeLimitIntervalKey, moduleCode, mobile), "_");
    }

    private String genVerifyCodeKey(String moduleCode, String mobile) {
        return StringUtils.join(Lists.newArrayList(VerifyCodeCacheKey, moduleCode, mobile), "_");
    }

    public ResultVO<String> sendVerifyCode(String mobile, String moduleCode, int limitSendInterval, Integer expireSeconds) {
        ResultVO<String> resultVO = new ResultVO<String>();
        do {
            if (StringUtils.isBlank(mobile)) {
                resultVO.format(false, "请输入手机号"); break;
            }
            if (!mobile.matches("^1[0-9]{10}$")) {
                resultVO.format(false, "请输入合法手机号"); break;
            }
            if (StringUtils.isBlank(moduleCode)) {
                moduleCode = DefaultModelCode;
            }
            if (expireSeconds == null || expireSeconds <= 0) {
                expireSeconds = DefaultVerifyCodeExpireSeconds;
            }
            String intervalLimitKey = genLimitIntervalKey(moduleCode, mobile);
            Jedis jedis = RedisPool.getJedis();
            try {
                if (jedis != null) {
                    String code = jedis.get(intervalLimitKey);
                    if (StringUtils.isNotBlank(code)) {
                        resultVO.format(true, "短信验证码已发送，请注意查收"); break;
                    }
                }
            } finally {
                if (jedis != null) {
                    RedisPool.returnResource(jedis);
                }
            }
            // 走到这里证明验证码需要重发，或者未曾发送
            Map<String, String> params = Maps.newHashMap();
            params.put("caller", "dalingjia");
            params.put("module", moduleCode);
            params.put("expire", String.valueOf(expireSeconds));

            String verifyCode = "";
            jedis = RedisPool.getJedis();
            try {
                if (jedis != null) {
                    String codeKey = genVerifyCodeKey(moduleCode, mobile);
                    verifyCode = jedis.get(codeKey);
                    if (StringUtils.isNotBlank(verifyCode)) {
                        resultVO.format(true, String.format("验证码发送成功! %s", verifyCode)); break;
                    }

                    verifyCode = Integer.toHexString(RandomUtils.nextInt(0x1000, 0xffff));
                    jedis.set(codeKey, verifyCode);
                    jedis.expire(codeKey, DefaultVerifyCodeExpireSeconds);
                    if (limitSendInterval < 0) {
                        jedis.set(intervalLimitKey, "1");
                        jedis.expire(intervalLimitKey, DefaultSendIntervalSeconds);
                    } else if (limitSendInterval > 0) {
                        jedis.set(intervalLimitKey, "1");
                        jedis.expire(intervalLimitKey, limitSendInterval);
                    }
                }
            } finally{
                if (jedis != null) {
                    RedisPool.returnResource(jedis);
                }
            }
            resultVO.format(true, String.format("验证码发送成功! %s", verifyCode)); break;
        } while (false);
        return resultVO;
    }


    public ResultVO<String> authVerifyCode(String mobile, String moduleCode, String verifyCode) {
        ResultVO<String> resultVO = new ResultVO<String>();
        do {
            if (StringUtils.isBlank(mobile)) {
                resultVO.format(false, "输入手机号"); break;
            }
            if (!mobile.matches("^1[0-9]{10}$")) {
                resultVO.format(false, "请输入合法手机号"); break;
            }
            if (StringUtils.isBlank(verifyCode)) {
                resultVO.format(false, "请输入验证码"); break;
            }
            verifyCode = StringUtils.trimToEmpty(verifyCode);
            if (StringUtils.isBlank(moduleCode)) {
                moduleCode = DefaultModelCode;
            }
            String codeKey = genVerifyCodeKey(moduleCode, mobile);
            String cacheVerifyCode = "";
            Jedis jedis = RedisPool.getJedis();
            try {
                if (jedis != null) {
                    cacheVerifyCode = jedis.get(codeKey);
                }
            } finally{
                if (jedis != null) {
                    RedisPool.returnResource(jedis);
                }
            }
            if (StringUtils.isBlank(cacheVerifyCode)) {
                resultVO.format(false, "验证码已过期,请重新获取!"); break;
            }
            if (!StringUtils.equalsIgnoreCase(verifyCode, cacheVerifyCode)) {
                resultVO.format(false, "验证码不正确,请重新输入!"); break;
            }
            resultVO.format(true, "验证通过!"); break;
        } while (false);

        return resultVO;
    }
}
