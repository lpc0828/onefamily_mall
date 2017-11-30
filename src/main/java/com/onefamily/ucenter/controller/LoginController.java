package com.onefamily.ucenter.controller;

import com.google.common.collect.Maps;
import com.onefamily.common.enums.ClientTypeEnum;
import com.onefamily.common.enums.YesNoEnum;
import com.onefamily.mall.service.sms.ISmsService;
import com.onefamily.platform.controller.BaseController;
import com.onefamily.platform.dataformat.JsonWrapper;
import com.onefamily.platform.dataformat.ResultVO;
import com.onefamily.ucenter.constants.Headers;
import com.onefamily.ucenter.model.User;
import com.onefamily.ucenter.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RequestMapping("uc/login")
@Controller
public class LoginController extends BaseController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ISmsService smsService;

    @RequestMapping("smsLogin.do")
    @ResponseBody
    JsonWrapper<Map<String, Object>> smsLogin(String mobile, String verifyCode, HttpServletRequest request) {
        JsonWrapper<Map<String, Object>> jsonWrapper = initJsonWrapper(request);
        try {
            ResultVO<String> smsResultVO = smsService.authVerifyCode(mobile, VerifyCodeController.ModuleCode, verifyCode);
            if (!smsResultVO.isRetBool()) {
                jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
                jsonWrapper.setErrorMsg(smsResultVO.getMessage());
            } else {
               ResultVO<User> userResultVO = userService.queryByMobile(mobile);
               if (!userResultVO.isRetBool()) {
                   jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
                   jsonWrapper.setErrorMsg(userResultVO.getMessage());
               } else if (userResultVO.getT() == null) {
                   jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
                   jsonWrapper.setErrorMsg("用户不存在");
               } else {
                   Map<String, Object> userMap = Maps.newHashMap();
                   userMap.put("uid", userResultVO.getT().getId());
                   userMap.put("utoken", userResultVO.getT().getAppToken());
                   userMap.put("headimgurl", userResultVO.getT().getHeadimgurl());
                   userMap.put("nickName", userResultVO.getT().getNickName());
                   userMap.put("ctime", userResultVO.getT().getCreatedDate().getTime());
                   userMap.put("certYn", YesNoEnum.codeOf(userResultVO.getT().getCertificationYn()).getValue());
                   jsonWrapper.setStatus(JsonWrapper.StatusEnum.Success.getCode());
                   jsonWrapper.setErrorMsg(userResultVO.getMessage());
                   jsonWrapper.setData(userMap);
               }
            }
        } finally {
            jsonWrapper.timeWatchStop();
        }
        return jsonWrapper;
    }

    @RequestMapping("smsRegister.do")
    @ResponseBody
    JsonWrapper<Map<String, Object>> smsRegister(String mobile, String verifyCode, HttpServletRequest request) {
        JsonWrapper<Map<String, Object>> jsonWrapper = initJsonWrapper(request);
        try {
            ResultVO<String> smsResultVO = smsService.authVerifyCode(mobile, VerifyCodeController.ModuleCode, verifyCode);
            if (!smsResultVO.isRetBool()) {
                jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
                jsonWrapper.setErrorMsg(smsResultVO.getMessage());
            } else {
                ResultVO<User> userResultVO = userService.queryByMobile(mobile);
                if (!userResultVO.isRetBool()) {
                    jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
                    jsonWrapper.setErrorMsg(userResultVO.getMessage());
                } else {
                    if (userResultVO.getT() != null) {
                        jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
                        jsonWrapper.setErrorMsg("手机用户已存在, 请勿重复注册");
                    } else {
                        userResultVO = userService.createMobileUser(mobile, null, request.getHeader(Headers.Platform));
                        if (userResultVO.isRetBool()) {
                            Map<String, Object> userMap = Maps.newHashMap();
                            userMap.put("uid", userResultVO.getT().getId());
                            userMap.put("utoken", userResultVO.getT().getAppToken());
                            userMap.put("nickName", userResultVO.getT().getNickName());
                            userMap.put("headimgurl", userResultVO.getT().getHeadimgurl());
                            userMap.put("ctime", userResultVO.getT().getCreatedDate().getTime());
                            userMap.put("certYn", YesNoEnum.codeOf(userResultVO.getT().getCertificationYn()).getValue());
                            jsonWrapper.setStatus(JsonWrapper.StatusEnum.Success.getCode());
                            jsonWrapper.setErrorMsg(userResultVO.getMessage());
                            jsonWrapper.setData(userMap);
                        } else {
                            jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
                            jsonWrapper.setErrorMsg(userResultVO.getMessage());
                        }
                    }
                }
            }
        } finally {
            jsonWrapper.timeWatchStop();
        }
        return jsonWrapper;
    }
}
